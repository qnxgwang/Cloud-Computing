package lab3.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8000);
        Socket socket = serverSocket.accept();
        BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        String msg = "";
        String str = "";
        while ((str = mBufferedReader.readLine()) != null) {
            msg += str;
            msg += "\r\n";
        }
        System.out.println(msg);

    }
}
