package ru.geekbrains.chat.server;

import ru.geekbrains.network.TCPConnection;
import ru.geekbrains.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * //этот класс умеет слушать какое то порт и принимать входящее соеденение
 */
public class ChatServer implements TCPConnectionListener {
    // cоздали экземпляр себя же чтобы начал работу с констурктора
    public static void main(String[] args) {
        new ChatServer();
    }
    /** списки
     *+++++++++
     */
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server погнали ");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {//сервер сокет который слушает тсп порт 8189
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());// в бесконечном цикле висим в методе аксепт который ждеет тсп соедение и возвращат
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);// логируем если что то случилось при подключении
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);// добавили в колеекцию
        sendToAllConnections("Клиент присоеднился к нашей беседе : " + tcpConnection);
    }

    @Override//что делаем если приняли строчку
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Клиент отключился" + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }
    /** Сделаем приватный метод который ничего не возвращает
     * который отправляет всем сообщение
     */
    private void sendToAllConnections(String value) {
        System.out.println(value);
        final int cnt = connections.size();// создали константу чтобы каждый раз не вызывать метод сайз
        for (int i = 0; i < cnt; i++) connections.get(i).sendString(value);//значение записали , пробежали по списку всех соеденений, и все отправили это сообщение
    }
}
