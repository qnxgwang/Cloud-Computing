package lab3.entity.c;

import java.util.Date;

public class Task {

        Date date;

        String msg;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Task(Date date, String msg) {
            this.date = date;
            this.msg = msg;
        }

}
