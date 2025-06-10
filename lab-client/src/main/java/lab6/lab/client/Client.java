package lab6.lab.client;

import lab6.lab.common.CommandRequest;
import lab6.lab.common.CommandResponse;
import lab6.lab.common.manager.CommandExecutor;
import lab6.lab.common.manager.MusicBandCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;
import java.util.Scanner;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5001;
    private static final int TIMEOUT_MS = 5000;
    private static final int MAX_RETRIES = 3;
    private static final int MAX_PACKET_SIZE = 65507;

    private final CommandParser commandParser;
    private final ResponseHandler responseHandler;
    private final CommandExecutor commandExecutor;
    private DatagramChannel channel;
    private String username;
    private String password;

    public Client() {
        this.commandParser = new CommandParser();
        this.responseHandler = new ResponseHandler();
        this.commandExecutor = new CommandExecutor(new MusicBandCollection(), null);
        logger.info("Client initialized");
    }

    public void start() {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            logger.info("Client started.");
            System.out.println("Клиент запущен. Введите 'login' или 'register' для начала работы.");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) {
                    logger.info("Client exiting.");
                    System.out.println("Выход из клиента.");
                    break;
                }

                try {
                    if (input.toLowerCase().startsWith("execute_script")) {
                        String[] parts = input.split("\\s+", 2);
                        String fileName = parts.length > 1 ? parts[1] : null;
                        if (fileName == null || fileName.trim().isEmpty()) {
                            logger.warn("Script file name not specified.");
                            System.out.println("Ошибка: Не указано имя файла скрипта.");
                            continue;
                        }
                        if (username == null || password == null) {
                            logger.warn("User not logged in for execute_script.");
                            System.out.println("Ошибка: Необходимо войти в систему перед выполнением скрипта.");
                            continue;
                        }
                        executeScript(fileName);
                    } else {
                        String[] parts = input.split("\\s+", 2);
                        String command = parts[0].toLowerCase();
                        String argument = parts.length > 1 ? parts[1] : null;

                        if (command.equals("login") || command.equals("register")) {
                            System.out.print("Введите логин: ");
                            String login = scanner.nextLine().trim();
                            System.out.print("Введите пароль: ");
                            String pwd = scanner.nextLine().trim();
                            CommandRequest request = new CommandRequest(command, argument, null, login, pwd);
                            sendRequest(request);
                            CommandResponse response = receiveResponse();
                            if (response != null) {
                                responseHandler.handleResponse(response);
                                if (response.isSuccess()) {
                                    username = login;
                                    password = pwd;
                                    logger.info("User {} logged in successfully.", username);
                                }
                            } else {
                                logger.warn("No response from server after all retries.");
                                System.out.println("Нет ответа от сервера после всех попыток.");
                            }
                        } else {
                            if (username == null || password == null) {
                                logger.warn("User not logged in for command: {}", command);
                                System.out.println("Ошибка: Необходимо войти в систему. Используйте команду 'login' или 'register'.");
                                continue;
                            }
                            CommandRequest request = commandParser.parseCommand(input, null, username, password);
                            if (request != null) {
                                sendRequest(request);
                                CommandResponse response = receiveResponse();
                                if (response != null) {
                                    responseHandler.handleResponse(response);
                                } else {
                                    logger.warn("No response from server for command: {}", command);
                                    System.out.println("Нет ответа от сервера после всех попыток.");
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("IO error: {}", e.getMessage());
                    System.out.println("Ошибка ввода-вывода: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    logger.error("Deserialization error: {}", e.getMessage());
                    System.out.println("Ошибка десериализации ответа от сервера: " + e.getMessage());
                } catch (Exception e) {
                    logger.error("Command processing error: {}", e.getMessage());
                    System.out.println("Ошибка обработки команды: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Client error: {}", e.getMessage());
            System.out.println("Ошибка клиента: " + e.getMessage());
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                    logger.info("Channel closed.");
                }
            } catch (IOException e) {
                logger.error("Error closing channel: {}", e.getMessage());
                System.out.println("Ошибка закрытия канала: " + e.getMessage());
            }
        }
    }

    public void executeScript(String fileName) throws IOException, ClassNotFoundException {
        logger.info("Executing script: {}", fileName);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                logger.debug("Executing script command: {}", line);
                System.out.println("Выполняется команда: " + line);

                if (line.toLowerCase().startsWith("execute_script")) {
                    logger.warn("Nested execute_script not supported: {}", line);
                    System.out.println("Ошибка: Вложенные вызовы execute_script не поддерживаются.");
                    continue;
                }

                CommandRequest request = commandParser.parseCommand(line, fileReader, username, password);
                if (request != null) {
                    sendRequest(request);
                    CommandResponse response = receiveResponse();
                    if (response != null) {
                        responseHandler.handleResponse(response);
                    } else {
                        logger.warn("No server response for script command: {}", line);
                        System.out.println("Нет ответа от сервера для команды: " + line);
                    }
                } else {
                    logger.warn("Skipped invalid script command: {}", line);
                    System.out.println("Пропущена некорректная команда: " + line);
                }
            }
        } catch (IOException e) {
            logger.error("Error executing script {}: {}", fileName, e.getMessage());
            System.out.println("Ошибка при выполнении скрипта: " + e.getMessage());
            throw e;
        }
    }

    private void sendRequest(CommandRequest request) throws IOException {
        logger.debug("Sending request: command={}, object={}", request.getCommandName(), request.getObject());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();

        byte[] data = baos.toByteArray();
        if (data.length > MAX_PACKET_SIZE) {
            logger.error("Data size exceeds max packet size: {} bytes", data.length);
            throw new IOException("Размер данных превышает максимальный размер UDP пакета (" + MAX_PACKET_SIZE + " байт).");
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        channel.send(buffer, new InetSocketAddress(SERVER_HOST, SERVER_PORT));
        logger.info("Request sent: {} bytes", data.length);
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
                    CommandResponse response = (CommandResponse) ois.readObject();
                    logger.debug("Received response: success={}, message={}", response.isSuccess(), response.getMessage());
                    return response;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            logger.warn("Retry attempt {} of {}", attempt + 1, MAX_RETRIES);
            System.out.println("Попытка " + (attempt + 1) + " из " + MAX_RETRIES);
        }
        return null;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }
}