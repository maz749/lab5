//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
import lab6.lab.common.CommandRequest;
import lab6.lab.common.CommandResponse;
import lab6.lab.common.manager.CommandExecutor;
import lab6.lab.common.manager.FileStorage;
import lab6.lab.common.manager.MusicBandCollection;
import lab6.lab.common.manager.MusicBandFactory;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5001;
    private static final int TIMEOUT_MS = 5000;
    private static final int MAX_RETRIES = 3;
    private static final int MAX_PACKET_SIZE = 65507;
    private final CommandParser commandParser = new CommandParser();
    private final ResponseHandler responseHandler = new ResponseHandler();
    private final CommandExecutor commandExecutor = new CommandExecutor(new MusicBandCollection(), new FileStorage(new MusicBandFactory()));
    private DatagramChannel channel;

    public Client() {
    }

    public void start() {
        try {
            this.channel = DatagramChannel.open();
            this.channel.configureBlocking(false);
            System.out.println("Клиент запущен. Введите 'help' для списка команд.");
            Scanner scanner = new Scanner(System.in);

            while(true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Выход из клиента.");
                    break;
                }

                try {
                    if (input.toLowerCase().startsWith("execute_script")) {
                        String[] parts = input.split("\\s+", 2);
                        String fileName = parts.length > 1 ? parts[1] : null;
                        if (fileName != null && !fileName.trim().isEmpty()) {
                            this.executeScript(fileName);
                        } else {
                            System.out.println("Ошибка: Не указано имя файла скрипта.");
                        }
                    } else {
                        CommandRequest request = this.commandParser.parseCommand(input, (BufferedReader)null);
                        if (request != null) {
                            this.sendRequest(request);
                            CommandResponse response = this.receiveResponse();
                            if (response != null) {
                                this.responseHandler.handleResponse(response);
                            } else {
                                System.out.println("Нет ответа от сервера после всех попыток.");
                            }
                        }
                    }
                } catch (IOException var16) {
                    IOException e = var16;
                    System.out.println("Ошибка ввода-вывода: " + e.getMessage());
                } catch (ClassNotFoundException var17) {
                    ClassNotFoundException e = var17;
                    System.out.println("Ошибка десериализации ответа от сервера: " + e.getMessage());
                } catch (Exception var18) {
                    Exception e = var18;
                    System.out.println("Ошибка обработки команды: " + e.getMessage());
                }
            }
        } catch (IOException var19) {
            IOException e = var19;
            System.out.println("Ошибка клиента: " + e.getMessage());
        } finally {
            try {
                if (this.channel != null) {
                    this.channel.close();
                }
            } catch (IOException var15) {
                IOException e = var15;
                System.out.println("Ошибка закрытия канала: " + e.getMessage());
            }

        }

    }

    public void executeScript(String fileName) throws IOException, ClassNotFoundException {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

            String line;
            try {
                while((line = fileReader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        System.out.println("Выполняется команда: " + line);
                        if (line.toLowerCase().startsWith("execute_script")) {
                            System.out.println("Ошибка: Вложенные вызовы execute_script не поддерживаются.");
                        } else {
                            CommandRequest request = this.commandParser.parseCommand(line, fileReader);
                            if (request != null) {
                                this.sendRequest(request);
                                CommandResponse response = this.receiveResponse();
                                if (response != null) {
                                    this.responseHandler.handleResponse(response);
                                } else {
                                    System.out.println("Нет ответа от сервера для команды: " + line);
                                }
                            }
                        }
                    }
                }
            } catch (Throwable var7) {
                try {
                    fileReader.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }

                throw var7;
            }

            fileReader.close();
        } catch (IOException var8) {
            IOException e = var8;
            System.out.println("Ошибка при выполнении скрипта: " + e.getMessage());
            throw e;
        }
    }

    private void sendRequest(CommandRequest request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();
        byte[] data = baos.toByteArray();
        if (data.length > 65507) {
            throw new IOException("Размер данных превышает максимальный размер UDP пакета (65507 байт).");
        } else {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            this.channel.send(buffer, new InetSocketAddress("localhost", 5001));
        }
    }

    private CommandResponse receiveResponse() throws IOException, ClassNotFoundException {
        for(int attempt = 0; attempt < 3; ++attempt) {
            long startTime = System.currentTimeMillis();

            while(System.currentTimeMillis() - startTime < 5000L) {
                ByteBuffer buffer = ByteBuffer.allocate(65535);
                SocketAddress address = this.channel.receive(buffer);
                if (address != null) {
                    buffer.flip();
                    ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    return (CommandResponse)ois.readObject();
                }

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException var8) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.println("Попытка " + (attempt + 1) + " из 3");
        }

        return null;
    }

    public CommandExecutor getCommandExecutor() {
        return this.commandExecutor;
    }
}
