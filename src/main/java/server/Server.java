package server;

import common.CommandRequest;
import common.CommandResponse;
import manager.MusicBandManager;

import java.net.*;
import java.io.*;

public class Server {
    private static final int PORT = 5001; // Изменено на 5001
    private final MusicBandManager manager;
    private final RequestHandler requestHandler;
    private final CommandProcessor commandProcessor;
    private final ResponseSender responseSender;
    private DatagramSocket socket;

    public Server(String fileName) {
        this.manager = new MusicBandManager();
        this.manager.loadFromFile(fileName);
        this.requestHandler = new RequestHandler();
        this.commandProcessor = new CommandProcessor(manager);
        this.responseSender = new ResponseSender();
    }

    public void start() {
        try {
            socket = new DatagramSocket(PORT);
            System.out.println("Сервер запущен на порту " + PORT);
            System.out.println("Ожидание запросов от клиентов. Консольный ввод не поддерживается.");

            while (true) {
                try {
                    CommandRequest request = requestHandler.receiveRequest(socket);
                    if (request != null) {
                        SocketAddress clientAddress = requestHandler.getClientAddress();
                        if (request.getCommandName().equals("admin_save") &&
                                clientAddress instanceof InetSocketAddress &&
                                ((InetSocketAddress) clientAddress).getAddress().isLoopbackAddress()) {
                            manager.executeCommand("save");
                            CommandResponse response = new CommandResponse("Коллекция сохранена на сервере.", null, true);
                            responseSender.sendResponse(socket, response, clientAddress);
                            System.out.println("Выполнена команда admin_save от localhost");
                        } else {
                            CommandResponse response = commandProcessor.processRequest(request);
                            responseSender.sendResponse(socket, response, clientAddress);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка обработки запроса: " + e.getMessage());
                }
            }
        } catch (SocketException e) {
            System.out.println("Ошибка сокета: " + e.getMessage());
            System.exit(1);
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("Сокет закрыт.");
                }
                manager.executeCommand("save");
                System.out.println("Сервер завершил работу, коллекция сохранена.");
            } catch (Exception e) {
                System.err.println("Ошибка при завершении работы сервера: " + e.getMessage());
            }
        }
    }
}