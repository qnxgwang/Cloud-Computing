package lab3.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8000);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hello\r\n213\r\n4534\r\n65421462".getBytes());
        outputStream.flush();
        socket.close();
    }
}
