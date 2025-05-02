package server;

import common.CommandRequest;

import java.io.*;
import java.net.*;

public class RequestHandler {
    private SocketAddress clientAddress;
    private static final int MAX_PACKET_SIZE = 65507; // Максимальный размер UDP пакета

    public CommandRequest receiveRequest(DatagramSocket socket) throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[65535];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        if (packet.getLength() > MAX_PACKET_SIZE) {
            throw new IOException("Размер пакета превышает допустимый лимит (" + MAX_PACKET_SIZE + " байт).");
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
        ObjectInputStream ois = new ObjectInputStream(bais);
        clientAddress = packet.getSocketAddress();
        return (CommandRequest) ois.readObject();
    }

    public SocketAddress getClientAddress() {
        return clientAddress;
    }
}