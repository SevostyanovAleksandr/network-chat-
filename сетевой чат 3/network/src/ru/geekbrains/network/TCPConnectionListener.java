package ru.geekbrains.network;
/** описываем различные события которые могут возникнуть
 * слой абстрации позваляющий подсовывать евентлистнер в разные события
 */
public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);// готовое событие когда запустили соеденение и можем с ним работать

    void onReceiveString(TCPConnection tcpConnection, String value);// когда соеденение приняло строчку входящую

    void onDisconnect(TCPConnection tcpConnection);// дисконет когда соеденение порвалось

    void onException(TCPConnection tcpConnection, Exception e);// обработка исключений, передача самого исключения
}
