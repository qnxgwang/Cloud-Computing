package lab3.service;

import lab3.entity.c.Address;
import lab3.entity.p.Status;
import lab3.server.Config;


public class Partipant extends Thread{

    private Object Exception;

    /* 状态机状态 */
    private static int status = Status.START;

    /* 参与者地址 */
    private static Address localAddress;

    public Partipant(Config config){
        localAddress = new Address(
                config.getParticipant().get(0).getHost(),
                config.getParticipant().get(0).getPort()
                );
    }

    @Override
    public void run() {

        while (true){
            switch (status){
                case Status.START:{
                    fun1();
                    // status = Status.
                    break;
                }
                case Status.OK:{
                    fun2();
                    // status = Status.
                    break;
                }
                case Status.DEL:{
                    fun3();
                    // status = Status.
                    break;
                }
                case Status.DEL_OK:{
                    fun4();
                    // status = Status.
                    break;
                }
            }
        }

    }

    public void fun1(){

    }
    public void fun2(){

    }
    public void fun3(){

    }
    public void fun4(){

    }

}
