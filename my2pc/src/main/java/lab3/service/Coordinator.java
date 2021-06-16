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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Coordinator extends Thread{

    /* 协调者本机地址 */
    private static Address localAddress;

    /* 客户机套接字 */
    private static Socket clientSocket;

    /* 状态机码 */
    private Status status;

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

    /* 测试用构造方法 */
    public Coordinator(){
        deadList.add(new Address("127.0.0.1", 8002));
        status = Status.COMMAND;
        queue.add(new Task(new Date(), "SET\r\nKEY1\r\nV1"));
        queue.add(new Task(new Date(), "SET\r\nKEY2\r\nV2"));
        queue.add(new Task(new Date(), "SET\r\nKEY3\r\nV3"));
        queue.add(new Task(new Date(), "GET\r\nKEY1"));
        queue.add(new Task(new Date(), "GET\r\nKEY3"));
    }

    /* 测试用main函数 */
    public static void main(String[] args) {
        Coordinator c = new Coordinator();
        c.start();
    }
    enum ResultType{
        SUCCESS,
        FAILURE,
    }
    static class Result{
        private ResultType resultType;
        private String value;

        public Result(ResultType resultType, String value) {
            this.resultType = resultType;
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /* 协调者向参与者发起连接请求 */
    public void connect(){

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
    }

    @Override
    public void run() {

        connect();

        //状态机
        while (true){
            switch(status){
                case CLIENT:{
                    monitorClient();
                    status = Status.COMMAND;
                    break;
                }
                case COMMAND:{
                    if(sendTask())
                        status = Status.WAIT_READY;
                    break;
                }
                case WAIT_READY:{
                    if(waitReady())
                        status = Status.COMMIT;
                    else
                        status = Status.COMMAND;
                    break;
                }
                case COMMIT:{
                    if (sendAccept())
                        status = Status.WAIT_ACK;
                    else
                        status = Status.COMMAND;
                    break;
                }
                case WAIT_ACK:{
                    if(waitAck())
                        status = Status.COMMAND;
                    else
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
            clientSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /* 向参与者发送第一阶段请求 */
    public boolean sendTask() {

        try {
            Task task = queue.take();
            AtomicBoolean flag = new AtomicBoolean(true);
            aliveMap.forEach((address, socket)->{
                if(! sendTaskByAddress(socket, task))
                    flag.set(false);
            });
            return flag.get();
        }catch (InterruptedException e){
            return false;
        }

    }

    public boolean sendTaskByAddress(Socket socket, Task task){

        String source = task.getMsg();
        List<String> stringList = Arrays.stream(source.split(Command.SPLIT)).collect(Collectors.toList());
        command = stringList.remove(0);
        System.out.println(command);

        switch (command){
            case Command.SET:
            case Command.GET: {
                String msg = source;
                return sendMessageByAddress(socket, msg);
            }
            case Command.DELETE:{
                List<String> sendList = DivideUtil.getList(stringList);

                sendMessageByAddress(socket, Command.DELETE + Command.SPLIT + sendList.size());
                sendList.forEach(str->{
                    sendMessageByAddress(socket, str);
                });
                return true;
            }
            default: {
                return false;
            }

        }

    }

    /* 等待第一阶段响应 */
    public boolean waitReady(){

        AtomicBoolean flag = new AtomicBoolean(true);
        aliveMap.forEach((address, socket)->{
            Result result = receiveMessageByAddress(socket);
            if((result.resultType == ResultType.FAILURE) ||
                            ! (Objects.equals(result.getValue(), Command.OK)))
                flag.set(false);
        });

        return flag.get();

    }

    /* 向参与者发送第二阶段请求 */
    public boolean sendAccept(){

        AtomicBoolean flag = new AtomicBoolean(true);
        aliveMap.forEach((address, socket)->{
            if(! sendMessageByAddress(socket, Command.ACCEPT))
                flag.set(false);
        });

        return flag.get();

    }

    /* 等待第二阶段响应 */
    public boolean waitAck(){

        switch (command){
            case Command.GET:
            case Command.DELETE: {
                AtomicBoolean flag = new AtomicBoolean(true);
                aliveMap.forEach((address, socket)->{
                    Result result = receiveMessageByAddress(socket);
                    if(result.resultType == ResultType.SUCCESS)
                        //replyClient(result.getValue());
                        System.out.println(result.getValue());
                    else
                        flag.set(false);
                });
                return flag.get();
            }
            case Command.SET:{
                AtomicBoolean flag = new AtomicBoolean(true);
                aliveMap.forEach((address, socket)->{
                    Result result = receiveMessageByAddress(socket);
                    if(result.resultType == ResultType.SUCCESS && Objects.equals(result.getValue(), Command.ACK))
                        //replyClient(result.getValue());
                        System.out.println(result.getValue());
                    else
                        flag.set(false);
                });
                return flag.get();
            }
            default: {
                return false;
            }
        }

    }

    public boolean sendMessageByAddress(Socket socket, String msg){

        try {
            socket.setSoTimeout(SOCKET_READ_WRITE_TIME_OUT);
            SocketUtil.writeMessage(socket, new Message(msg));
        } catch (IOException e){
            unConnect(socket);
            return false;
        }

        return true;

    }

    public Result receiveMessageByAddress(Socket socket) {

        Message message;
        try{
            socket.setSoTimeout(SOCKET_READ_WRITE_TIME_OUT);
            message = SocketUtil.readMessage(socket);
        }catch (IOException | ClassNotFoundException e){
            unConnect(socket);
            return new Result(ResultType.FAILURE, null);
        }
        return new Result(ResultType.SUCCESS, message.getMsg());

    }

    public void unConnect(Socket socket){

        String IP = socket.getInetAddress().getHostAddress();
        int port = socket.getPort();
        aliveMap.remove(IP);
        deadList.add(new Address(IP,port));

    }

}
