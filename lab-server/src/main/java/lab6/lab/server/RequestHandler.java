//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import lab6.lab.common.CommandRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestHandler {
    private static final Logger logger = LogManager.getLogger(RequestHandler.class);
    private SocketAddress clientAddress;
    private static final int MAX_PACKET_SIZE = 65507;

    public RequestHandler() {
    }

    public CommandRequest receiveRequest(DatagramSocket socket) throws IOException, ClassNotFoundException {
        byte[] buffer = new byte['\uffff'];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        logger.debug("Ожидание нового подключения или запроса...");
        socket.receive(packet);
        if (packet.getLength() > 65507) {
            throw new IOException("Размер пакета превышает допустимый лимит (65507 байт).");
        } else {
            logger.info("Получен новый запрос от клиента: размер пакета {}", packet.getLength());
            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            ObjectInputStream ois = new ObjectInputStream(bais);
            this.clientAddress = packet.getSocketAddress();
            return (CommandRequest)ois.readObject();
        }
    }

    public SocketAddress getClientAddress() {
        return this.clientAddress;
    }
}
