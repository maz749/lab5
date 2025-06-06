//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.server;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.List;
import lab6.lab.common.CommandRequest;
import lab6.lab.common.CommandResponse;
import lab6.lab.common.manager.MusicBandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final int PORT = 5001;
    private final MusicBandManager manager = new MusicBandManager();
    private final RequestHandler requestHandler;
    private final CommandProcessor commandProcessor;
    private final ResponseSender responseSender;
    private DatagramSocket socket;

    public Server(String fileName) {
        this.manager.loadFromFile(fileName);
        this.requestHandler = new RequestHandler();
        this.commandProcessor = new CommandProcessor(this.manager);
        this.responseSender = new ResponseSender();
    }

    public void start() {
        try {
            this.socket = new DatagramSocket(5001);
            logger.info("Сервер запущен на порту {}", 5001);

            while(true) {
                while(true) {
                    try {
                        CommandRequest request = this.requestHandler.receiveRequest(this.socket);
                        if (request != null) {
                            logger.info("Получен новый запрос от клиента: {}", request.getCommandName());
                            SocketAddress clientAddress = this.requestHandler.getClientAddress();
                            CommandResponse response;
                            if (request.getCommandName().equals("admin_save") && clientAddress instanceof InetSocketAddress && ((InetSocketAddress)clientAddress).getAddress().isLoopbackAddress()) {
                                this.manager.executeCommand("save");
                                response = new CommandResponse("Коллекция сохранена на сервере.", (List)null, true);
                                this.responseSender.sendResponse(this.socket, response, clientAddress);
                                logger.info("Выполнена команда admin_save от localhost");
                            } else {
                                response = this.commandProcessor.processRequest(request);
                                this.responseSender.sendResponse(this.socket, response, clientAddress);
                                logger.info("Отправлен ответ клиенту на команду: {}", request.getCommandName());
                            }
                        }
                    } catch (Exception var12) {
                        Exception e = var12;
                        logger.error("Ошибка обработки запроса: {}", e.getMessage());
                    }
                }
            }
        } catch (SocketException var13) {
            SocketException e = var13;
            logger.error("Ошибка сокета при запуске сервера: {}", e.getMessage());
            System.exit(1);
        } finally {
            try {
                if (this.socket != null && !this.socket.isClosed()) {
                    this.socket.close();
                    logger.info("Сокет закрыт.");
                }

                this.manager.executeCommand("save");
                logger.info("Сервер завершил работу, коллекция сохранена.");
            } catch (Exception var11) {
                Exception e = var11;
                logger.error("Ошибка при завершении работы сервера: {}", e.getMessage());
            }

        }

    }
}
