package ru.geekbrains.chat.client;

import ru.geekbrains.network.TCPConnectionListener;
import sun.rmi.transport.tcp.TCPConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "192.168.0.104";
    private static final int PORT = 8189;

    private static final int WIDTH = ;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        /**
         * Есть особенность во всех графических интерфейсов есть ограничения по многопоточности
         * Практически их большинства графических интерфейсов нельзя работать из разных потоков
         * Какие то графичекие интерфейсы могут работать только из главного потока
         * Но у swinga боее жесткие ограничесния работать можно только их edt
         */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();// теперь благодаря этой строчке можно работать с edt
            }
        });

    }
    /**
     * создадим поле где мы будем писать наш текст
     * для графического интерфейса
     */
    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("Босс");// поле куда добавим свой никнейм
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;
    /**
     * конструктор в котором создаем наше окошко и работаем с ним
     */
    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//операция на закртие крестиком
        setSize(WIDTH, HEIGHT);// установим размер окна
        setLocationRelativeTo(null);// установим окно по середине
        setAlwaysOnTop(true);// установим окно по вверх всех окон
        log.setEditable(true);  //запрет редактирования
        log.setLineWrap(true);  //автоматический перенос строк

        add(log, BorderLayout.CENTER);;// добавляем в центр

        fieldInput.addActionListener(this);// кликаем на ентер
        add(fieldInput, BorderLayout.SOUTH);// добавим на юг
        add(fieldNickname, BorderLayout.NORTH);// добавим на север поле

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + msg);
    }
    
    private synchronized void printMsg(String msg) {   //Message
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());  //автоскрол
            }
        });
    }

    @Override
    public void onConnectionReady(ru.geekbrains.network.TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(ru.geekbrains.network.TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(ru.geekbrains.network.TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(ru.geekbrains.network.TCPConnection tcpConnection, Exception e) {

    }
}
