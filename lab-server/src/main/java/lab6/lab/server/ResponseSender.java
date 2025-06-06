//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResponseSender {
    private static final Logger logger = LogManager.getLogger(ResponseSender.class);

    public ResponseSender() {
    }

    public void sendResponse(DatagramSocket socket, CommandResponse response, SocketAddress clientAddress) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();
        byte[] data = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress);
        logger.info("Отправка ответа клиенту: размер пакета {}", data.length);
        socket.send(packet);
    }
}
