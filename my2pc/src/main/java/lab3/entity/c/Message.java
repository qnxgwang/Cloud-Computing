package lab3.entity.c;

import java.io.Serializable;

public class Message implements Serializable {
    String msg;

    public String getMsg() {
        return msg;
    }

    public Message(String msg) {
        this.msg = msg;
    }
}
