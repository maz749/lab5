package lab6.lab.server;

import lab6.lab.common.CommandRequest;
import lab6.lab.common.CommandResponse;
import lab6.lab.common.manager.DatabaseManager;
import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.manager.MusicBandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final int MAX_PACKET_SIZE = 65507;
    private final int port;
    private DatagramSocket socket;
    private final MusicBandCollection collection;
    private DatabaseManager databaseManager;
    private volatile boolean running;
    private final ExecutorService requestReaderPool;
    private final ForkJoinPool requestProcessorPool;
    private final ForkJoinPool responseSenderPool;
    private final CommandProcessor commandProcessor;

    public Server(int port) {
        this.port = port;
        this.collection = new MusicBandCollection();
        this.requestReaderPool = Executors.newCachedThreadPool();
        this.requestProcessorPool = new ForkJoinPool();
        this.responseSenderPool = new ForkJoinPool();
        try {
            MusicBandManager manager = new MusicBandManager(collection);
            this.databaseManager = manager.getDbManager();
            this.commandProcessor = new CommandProcessor(collection, databaseManager);
            logger.info("Server initialized on port {}", port);
        } catch (SQLException e) {
            logger.error("Failed to initialize MusicBandManager: {}", e.getMessage());
            throw new RuntimeException("Initialization failed", e);
        }
        this.running = false;
    }

    public void start() {
        try {
            socket = new DatagramSocket(port);
            running = true;
            logger.info("Server started on port {}", port);


            requestReaderPool.submit(this::receiveRequests);

        } catch (SocketException e) {
            logger.error("Failed to start server: {}", e.getMessage());
            stop();
        }
    }

    private void receiveRequests() {
        while (running) {
            try {
                byte[] buffer = new byte[MAX_PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                logger.info("Received request from client: {}", packet.getSocketAddress());

                // Process request in ForkJoinPool
                requestProcessorPool.submit(() -> {
                    try {
                        CommandRequest request = deserializeRequest(packet);
                        CommandResponse response = processRequest(request, packet.getSocketAddress());
                        responseSenderPool.submit(() -> sendResponse(response, packet.getSocketAddress()));
                    } catch (IOException | ClassNotFoundException e) {
                        logger.error("Error processing request: {}", e.getMessage());
                    }
                });
            } catch (IOException e) {
                if (running) {
                    logger.error("Error receiving request: {}", e.getMessage());
                }
            }
        }
    }

    private CommandRequest deserializeRequest(DatagramPacket packet) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
        ObjectInputStream ois = new ObjectInputStream(bais);
        CommandRequest request = (CommandRequest) ois.readObject();
        logger.debug("Deserialized request: command={}, object={}", request.getCommandName(), request.getObject());
        return request;
    }

    private CommandResponse processRequest(CommandRequest request, SocketAddress clientAddress) {
        try {
            // Authenticate user
            Long userId = databaseManager.authenticateUser(request.getUsername(), request.getPassword());
            CommandResponse response = commandProcessor.processRequest(request, userId);
            logger.info("Processed request {} from {}: success={}, message={}",
                    request.getCommandName(), clientAddress, response.isSuccess(), response.getMessage());
            return response;
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage());
            return new CommandResponse("Database error: " + e.getMessage(), null, false);
        } catch (Exception e) {
            logger.error("Unexpected error processing request: {}", e.getMessage());
            return new CommandResponse("Error: " + e.getMessage(), null, false);
        }
    }

    private void sendResponse(CommandResponse response, SocketAddress clientAddress) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(response);
            oos.flush();
            byte[] data = baos.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress);
            socket.send(packet);
            logger.info("Sent response to client {}: {} bytes", clientAddress, data.length);
        } catch (IOException e) {
            logger.error("Error sending response to {}: {}", clientAddress, e.getMessage());
        }
    }

    public void stop() {
        running = false;
        requestReaderPool.shutdown();
        requestProcessorPool.shutdown();
        responseSenderPool.shutdown();
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logger.info("Server socket closed.");
            }
            if (databaseManager != null) {
                databaseManager.close();
                logger.info("DatabaseManager closed.");
            }
        } catch (SQLException e) {
            logger.error("Error closing DatabaseManager: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 5001;
        Server server = new Server(port);
        server.start();
    }
}