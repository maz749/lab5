package lab6.lab.server;

import lab6.lab.common.CommandRequest;
import lab6.lab.common.CommandResponse;
import lab6.lab.common.database.DatabaseInitializer;
import lab6.lab.common.database.DatabaseConnectionManager;
import lab6.lab.common.database.UserRepository;
import lab6.lab.common.database.MusicBandRepository;
import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.models.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final int MAX_PACKET_SIZE = 65507;
    private final int port;
    private DatagramSocket socket;
    private final MusicBandCollection collection;
    private final UserRepository userRepository;
    private final MusicBandRepository musicBandRepository;
    private final AtomicBoolean running;
    private final ExecutorService requestReaderPool;
    private final ForkJoinPool requestProcessorPool;
    private final ForkJoinPool responseSenderPool;
    private final CommandProcessor commandProcessor;
    private final BlockingQueue<RequestTask> requestQueue;
    private final BlockingQueue<ResponseTask> responseQueue;

    public Server(int port) {
        this.port = port;
        this.collection = new MusicBandCollection();
        this.requestReaderPool = Executors.newCachedThreadPool();
        this.requestProcessorPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        this.responseSenderPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        this.requestQueue = new LinkedBlockingQueue<>();
        this.responseQueue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(false);

        try {
            DatabaseInitializer.initializeDatabase();
            this.userRepository = new UserRepository();
            this.musicBandRepository = new MusicBandRepository();
            this.commandProcessor = new CommandProcessor(collection, userRepository, musicBandRepository);
            loadCollection();
            logger.info("Server initialized on port {}", port);
        } catch (SQLException e) {
            logger.error("Failed to initialize database: {}", e.getMessage());
            throw new RuntimeException("Initialization failed", e);
        }
    }

    private void loadCollection() throws SQLException {
        collection.clear();
        for (MusicBand band : musicBandRepository.getAllBands()) {
            collection.add(band);
        }
        logger.info("Loaded {} music bands from database", collection.size());
    }

    public void start() {
        try {
            socket = new DatagramSocket(port);
            running.set(true);
            logger.info("Server started on port {}", port);

            // Start request reader threads
            for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
                requestReaderPool.submit(this::readRequests);
            }

            // Start request processor threads
            for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
                requestProcessorPool.submit(this::processRequests);
            }

            // Start response sender threads
            for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
                responseSenderPool.submit(this::sendResponses);
            }

            // Wait for shutdown signal
            while (running.get()) {
                Thread.sleep(1000);
            }
        } catch (SocketException e) {
            logger.error("Failed to create server socket: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Server interrupted: {}", e.getMessage());
        } finally {
            stop();
        }
    }

    private void readRequests() {
        byte[] buffer = new byte[MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (running.get()) {
            try {
                socket.receive(packet);
                CommandRequest request = deserializeRequest(packet);
                SocketAddress clientAddress = packet.getSocketAddress();
                requestQueue.put(new RequestTask(request, clientAddress));
                logger.debug("Received request from {}: {}", clientAddress, request.getCommandName());
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error reading request: {}", e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processRequests() {
        while (running.get()) {
            try {
                RequestTask task = requestQueue.take();
                requestProcessorPool.submit(() -> {
                    try {
                        CommandResponse response = processRequest(task.request(), task.clientAddress());
                        responseQueue.put(new ResponseTask(response, task.clientAddress()));
                    } catch (Exception e) {
                        logger.error("Error processing request: {}", e.getMessage());
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void sendResponses() {
        while (running.get()) {
            try {
                ResponseTask task = responseQueue.take();
                responseSenderPool.submit(() -> {
                    try {
                        sendResponse(task.response(), task.clientAddress());
                    } catch (Exception e) {
                        logger.error("Error sending response: {}", e.getMessage());
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private CommandRequest deserializeRequest(DatagramPacket packet) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (CommandRequest) ois.readObject();
    }

    private CommandResponse processRequest(CommandRequest request, SocketAddress clientAddress) {
        try {
            String command = request.getCommandName().toLowerCase();

            // Special handling for register and login commands
            if (command.equals("register")) {
                Long userId = userRepository.registerUser(request.getUsername(), request.getPassword());
                if (userId != null) {
                    logger.info("User {} registered successfully with id {}", request.getUsername(), userId);
                    return new CommandResponse("User " + request.getUsername() + " successfully registered.", null, true);
                } else {
                    logger.warn("Registration failed for user: {}", request.getUsername());
                    return new CommandResponse("Registration failed: User already exists.", null, false);
                }
            }

            if (command.equals("login")) {
                Long userId = userRepository.authenticateUser(request.getUsername(), request.getPassword());
                if (userId != null) {
                    logger.info("User {} logged in successfully with id {}", request.getUsername(), userId);
                    return new CommandResponse("User " + request.getUsername() + " successfully logged in.", null, true);
                } else {
                    logger.warn("Login failed for user: {}", request.getUsername());
                    return new CommandResponse("Login failed: Invalid username or password.", null, false);
                }
            }

            // For all other commands, require authentication
            Long userId = userRepository.authenticateUser(request.getUsername(), request.getPassword());
            if (userId == null) {
                logger.warn("Authentication failed for user: {}", request.getUsername());
                return new CommandResponse("Authentication failed", null, false);
            }
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
            logger.debug("Sent response to client {}: {} bytes", clientAddress, data.length);
        } catch (IOException e) {
            logger.error("Error sending response to {}: {}", clientAddress, e.getMessage());
        }
    }

    public void stop() {
        running.set(false);
        requestReaderPool.shutdown();
        requestProcessorPool.shutdown();
        responseSenderPool.shutdown();
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logger.info("Server socket closed");
            }
        } catch (Exception e) {
            logger.error("Error during server shutdown: {}", e.getMessage());
        }
    }

    private record RequestTask(CommandRequest request, SocketAddress clientAddress) {}
    private record ResponseTask(CommandResponse response, SocketAddress clientAddress) {}

    public static void main(String[] args) {
        int port = 5001;
        Server server = new Server(port);
        server.start();
    }
}