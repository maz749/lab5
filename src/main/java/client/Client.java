package client;

import common.CommandRequest;
import common.CommandResponse;
import manager.CommandExecutor;
import manager.FileStorage;
import manager.MusicBandCollection;
import manager.MusicBandFactory;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

/**
 * Основной класс клиентского приложения, отвечающий за взаимодействие с пользователем и сервером.
 */
public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5001;
    private static final int TIMEOUT_MS = 5000;
    private static final int MAX_RETRIES = 3;
    private static final int MAX_PACKET_SIZE = 65507; // Максимальный размер UDP пакета (с учетом заголовков)

    private final CommandParser commandParser;
    private final ResponseHandler responseHandler;
    private final CommandExecutor commandExecutor;
    private DatagramChannel channel;

    public Client() {
        this.commandParser = new CommandParser();
        this.responseHandler = new ResponseHandler();
        this.commandExecutor = new CommandExecutor(new MusicBandCollection(), new FileStorage(new MusicBandFactory()), this);
    }

    public void start() {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            System.out.println("Клиент запущен. Введите 'help' для списка команд.");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Выход из клиента.");
                    break;
                }

                try {
                    if (input.toLowerCase().startsWith("execute_script")) {
                        commandExecutor.executeCommand(input, null);
                    } else {
                        CommandRequest request = commandParser.parseCommand(input, null);
                        if (request != null) {
                            sendRequest(request);
                            CommandResponse response = receiveResponse();
                            if (response != null) {
                                responseHandler.handleResponse(response);
                            } else {
                                System.out.println("Нет ответа от сервера после всех попыток.");
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка обработки команды: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка клиента: " + e.getMessage());
        } finally {
            try {
                if (channel != null) channel.close();
            } catch (IOException e) {
                System.out.println("Ошибка закрытия канала: " + e.getMessage());
            }
        }
    }

    public void processScriptCommand(String commandLine, BufferedReader scriptReader) throws IOException {
        try {
            CommandRequest request = commandParser.parseCommand(commandLine, scriptReader);
            if (request != null) {
                sendRequest(request);
                CommandResponse response = receiveResponse();
                if (response != null) {
                    responseHandler.handleResponse(response);
                } else {
                    System.out.println("Нет ответа от сервера после всех попыток.");
                    throw new IOException("Сервер не ответил на команду: " + commandLine);
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка десериализации ответа: " + e.getMessage());
            throw new IOException("Ошибка обработки команды в скрипте: " + e.getMessage());
        }
    }

    private void sendRequest(CommandRequest request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();

        byte[] data = baos.toByteArray();
        if (data.length > MAX_PACKET_SIZE) {
            throw new IOException("Размер данных превышает максимальный размер UDP пакета (" + MAX_PACKET_SIZE + " байт).");
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        channel.send(buffer, new InetSocketAddress(SERVER_HOST, SERVER_PORT));
    }

    private CommandResponse receiveResponse() throws IOException, ClassNotFoundException {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
                ByteBuffer buffer = ByteBuffer.allocate(65535);
                SocketAddress address = channel.receive(buffer);
                if (address != null) {
                    buffer.flip();
                    ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    return (CommandResponse) ois.readObject();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Попытка " + (attempt + 1) + " из " + MAX_RETRIES);
        }
        return null;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }
}