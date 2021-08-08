package ru.geekbrains.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
/** что нужно минимум для одного TCP соеденения **/
    /**
     * Сокет который связан с соеденинем
     **/
    private final Socket socket;
    /**
     * поток который слушает входящие соедение постояно
     * читает поток ввода, если строчка прилетела
     * то поток генеррирует соббытие
     **/
    private final Thread rxThread;

    private final TCPConnectionListener eventListener;
    /**
     * поток ввода
     **/
    private final BufferedReader in;
    /**
     * поток вывода
     */
    private final BufferedWriter out;
    /**
     * будет два конструктора
     * который на вход примет готовый сокет
     * и с этим сокетом создаст соеденение
     */


    //cоздает сокет по параметрам айпи адреса и номера порта

    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));//чар сет указали в жествкую кодировку
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        /**создаем поток который будет слушать все входящие сообщения . */
        rxThread = new Thread(new Runnable() {
            @Override
            // метод ран слушает все входящие сообщения
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) {
                        //String msg = in.readLine();
                        eventListener.onReceiveString(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {

                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }
    /**
     * метод отправить сообщение
     */
    public synchronized void sendString(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }
    /**
     * метод дисконекта
     */
    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }
/** для информации для логов штатноесредство для того чтобы
 * представить обьект штатными средствами
 */
// переопределение метода в предке от которого
// мы наследуемсяя String toString можем переопределить его и написать
// cвою реализацию ПОЛИМОРФИЗМ
    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
