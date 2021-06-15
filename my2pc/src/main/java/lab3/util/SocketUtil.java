package lab3.util;

import lab3.entity.c.Message;

import java.io.*;
import java.net.Socket;

public class SocketUtil {

    public static void writeMessage(Socket socket, Message responseMessage) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(responseMessage);
    }

    public static Message readMessage(Socket socket) throws IOException, ClassNotFoundException {
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Message requestMessage = (Message)objectInputStream.readObject();//字节流反序列化
        return requestMessage;
    }

}
