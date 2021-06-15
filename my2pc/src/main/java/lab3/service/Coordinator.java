package lab3.service;

import lab3.entity.c.*;
import lab3.server.Config;
import lab3.util.DivideUtil;
import lab3.util.SocketUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class Coordinator extends Thread{

    /* 协调者本机地址 */
    private static Address localAddress;

    /* 客户机套接字 */
    private static Socket clientSocket;

    /* 状态机码 */
    private static int status = 0x00;

    /* 读写超时间隔 默认100ms */
    private static int SOCKET_READ_WRITE_TIME_OUT = 100;

    /* 客户机请求 */
    private static String command = "NULL";

    /* 存活的参与者 */
    private static final Map<Address, Socket> aliveMap = new HashMap();

    /* 死亡的参与者 */
    private static final List<Address> deadList = new ArrayList<>();

    /* 客户机请求阻塞队列 */
    private static final BlockingQueue<Task> queue = new ArrayBlockingQueue<>(10);

    public Coordinator(Config config){

        //初始化参与者地址
        config.getParticipant().forEach(address->{
            deadList.add(new Address(address.getHost(), address.getPort()));
        });

        //初始化协调者地址
        localAddress = new Address(
                config.getCoordinator().get(0).getHost(),
                config.getCoordinator().get(0).getPort());

        //初始化状态机状态
        status = Status.CLIENT;
    }

    @Override
    public void run() {

        // 协调者向参与者发起连接请求
        deadList.forEach(address -> {
            Socket socket = null;
            try {
                socket = new Socket(address.getIP(), address.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
            aliveMap.put(address, socket);
        });
        aliveMap.forEach((address, socket)->{
            deadList.remove(address);
        });

        //状态机
        while (true){
            switch(status){
                case Status.CLIENT:{
                    monitorClient();
                    status = Status.COMMAND;
                    break;
                }
                case Status.COMMAND:{
                    sendTask();
                    status = Status.WAIT_READY;
                    break;
                }
                case Status.WAIT_READY:{
                    waitReady();
                    status = Status.COMMIT;
                    break;
                }
                case Status.COMMIT:{
                    sendAccept();
                    status = Status.WAIT_ACK;
                    break;
                }
                case Status.WAIT_ACK:{
                    waitAck();
                    status = Status.COMMAND;
                    break;
                }
                default:break;
            }
        }
    }

    /* 监听客户机请求 */
    public void monitorClient() {
        String msg = "";
        try{
            ServerSocket serverSocket = new ServerSocket(localAddress.getPort());
            clientSocket = serverSocket.accept();
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                msg += str;
                msg += "\r\n";
            }
            queue.add(new Task(new Date(), msg.trim()));
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /* 响应客户机请求 */
    public void replyClient(String msg){
        try{
            BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            bufferedWriter.write(msg);
            bufferedWriter.close();
            clientSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /* 向参与者发送第一阶段请求 */
    public void sendTask() {

        try {
            Task task = queue.take();
            aliveMap.forEach((address, socket)->{
                sendTaskByAddress(socket, task);
            });
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public void sendTaskByAddress(Socket socket, Task task){
        String source = task.getMsg();
        List<String> stringList = Arrays.stream(source.split(Command.SPLIT)).collect(Collectors.toList());

        command = stringList.remove(0);
        System.out.println(command);
        switch (command){
            case Command.SET:
            case Command.GET: {
                String msg = source;
                sendMessageByAddress(socket, msg);
                break;
            }
            case Command.DELETE:{
                List<String> sendList = DivideUtil.getList(stringList);
                sendMessageByAddress(socket, Command.DELETE + Command.SPLIT + sendList.size());
                sendList.forEach(str->{
                    sendMessageByAddress(socket, str);
                });
                break;
            }
            default:break;
        }
    }

    /* 等待第一阶段响应 */
    public boolean waitReady(){

        aliveMap.forEach((address, socket)->{
            String msg = receiveMessageByAddress(socket);
            System.out.println(msg);
        });

        return true;
    }

    /* 向参与者发送第二阶段请求 */
    public void sendAccept(){

        aliveMap.forEach((address, socket)->{
            sendMessageByAddress(socket, Command.REPLY);
        });

    }

    /* 等待第二阶段响应 */
    public boolean waitAck(){

        switch (command){
            case Command.GET:
            case Command.DELETE: {
                aliveMap.forEach((address, socket)->{
                    String msg = receiveMessageByAddress(socket);
                    System.out.println(msg);
                    replyClient(msg);
                });
                break;
            }
            case Command.SET:{
                aliveMap.forEach((address, socket)->{
                    String msg = receiveMessageByAddress(socket);
                    System.out.println(msg);
                });
                break;
            }
            default:break;
        }
        return true;
    }

    public void sendMessageByAddress(Socket socket, String msg){
        try {
            socket.setSoTimeout(SOCKET_READ_WRITE_TIME_OUT);
            SocketUtil.writeMessage(socket, new Message(msg));
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
            String IP = socket.getInetAddress().getHostAddress();
            int port = socket.getPort();
            aliveMap.remove(IP);
            deadList.add(new Address(IP,port));
        }
    }

    public String receiveMessageByAddress(Socket socket) {
        Message message = null;
        try{
            socket.setSoTimeout(SOCKET_READ_WRITE_TIME_OUT);
            message = SocketUtil.readMessage(socket);
            socket.close();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            String IP = socket.getInetAddress().getHostAddress();
            int port = socket.getPort();
            aliveMap.remove(IP);
            deadList.add(new Address(IP,port));
        }
        return message.getMsg();
    }

}
