package lab3.service;

import lab3.entity.c.Address;
import lab3.entity.c.Command;
import lab3.entity.c.Message;
import lab3.entity.p.Status;
import lab3.server.Config;
import lab3.util.SocketUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;


public class Partipant extends Thread{

    /* 状态机状态 */
    private static Status status = Status.MONITOR;

    /* 参与者地址 */
    private static Address localAddress;

    private static Socket socket;

    private static List<String> command;

    //private static Result result;

    private static final int SOCKET_READ_WRITE_TIME_OUT = 100;

    private static final Map<String, String> data = new HashMap<>();

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

    public Partipant(Config config){
        localAddress = new Address(
                config.getParticipant().get(0).getHost(),
                config.getParticipant().get(0).getPort()
                );
    }

    public Partipant(Address address){
        this.localAddress = address;
    }

    public static void main(String[] args) {
        Partipant p = new Partipant(new Address("127.0.0.1", 8002));
        p.start();
    }

    @Override
    public void run() {

        while (true){
            switch (status){
                case MONITOR:{
                    monitor();
                    status = Status.WAIT_REQUEST;
                    break;
                }
                case WAIT_REQUEST:{
                    if(waitRequest())
                        status = Status.READY;
                    break;
                }
                case READY:{
                    if(ready())
                        status = Status.WAIT_ACCEPT;
                    else
                        status = Status.MONITOR;
                    break;
                }
                case WAIT_ACCEPT:{
                    if(waitAccept())
                        status = Status.REPLY;
                    else
                        status = Status.MONITOR;
                    break;
                }
                case REPLY:{
                    if(reply())
                        status = Status.WAIT_REQUEST;
                    else
                        status = Status.MONITOR;
                    break;
                }
            }
        }

    }

    public void monitor(){
        try{
            ServerSocket serverSocket = new ServerSocket(localAddress.getPort());
            socket = serverSocket.accept();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public boolean waitRequest(){
        Result result = receiveMessageByAddress(socket);
        if(result.resultType == ResultType.SUCCESS){
            String msg = result.getValue();
            command = Arrays.stream(msg.split(Command.SPLIT)).collect(Collectors.toList());
            return true;
        }
        return false;
    }

    public boolean ready(){
        return sendMessageByAddress(socket, Command.OK);
    }

    public boolean waitAccept(){
        Result result = receiveMessageByAddress(socket);
        if(result.resultType == ResultType.SUCCESS &&
                Objects.equals(result.getValue(), Command.ACCEPT))
            return true;
        return false;
    }

    public boolean reply(){
        String commandType = command.remove(0);
        System.out.println(commandType);
        switch (commandType){

            case Command.GET:{
                return sendMessageByAddress(socket, data.get(command.get(0)));
            }
            case Command.SET:{
                data.put(command.get(0), command.get(1));
                return sendMessageByAddress(socket, Command.ACK);
            }
            case Command.DELETE:{
                int num = (int) command.stream().filter(data::containsKey).count();
                command.forEach(key->{
                    data.remove(key);
                });
                return sendMessageByAddress(socket, Integer.toString(num));
            }

            default:{
                return false;
            }
        }
    }

    public boolean sendMessageByAddress(Socket socket, String msg){
        try {
            socket.setSoTimeout(SOCKET_READ_WRITE_TIME_OUT);
            SocketUtil.writeMessage(socket, new Message(msg));
        } catch (IOException e){
            return false;
        }
        return true;
    }

    public Result receiveMessageByAddress(Socket socket) {
        Message message = null;
        try{
            socket.setSoTimeout(SOCKET_READ_WRITE_TIME_OUT);
            message = SocketUtil.readMessage(socket);
        }catch (IOException | ClassNotFoundException e){
            return new Result(ResultType.FAILURE,null);
        }
        return new Result(ResultType.SUCCESS, message.getMsg());
    }


}
