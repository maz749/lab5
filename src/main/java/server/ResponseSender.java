package server;

import common.CommandResponse;

import java.net.*;
import java.io.*;

/**
 * Класс для сериализации и отправки ответов клиенту.
 */
public class ResponseSender {
    public void sendResponse(DatagramSocket socket, CommandResponse response, SocketAddress clientAddress) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();

        byte[] data = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress);
        socket.send(packet);
    }
}