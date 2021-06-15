package lab3.util;

import java.util.Vector;

public class LogUtil {
    Vector<String> Log;
    int pos,num;
    public void inializeLog(int num){
        this.num=num;
        pos=0;
        Log=new Vector<String>(num);
        for(int i=0;i<num;++i){
            Log.add("");
        }
    }

    public String getLog(){
        return Log.elementAt(pos);
    }

    public void addLog(String str){

        if(++pos!=num){
            Log.set(pos,str);
        }
        else {
            pos=0;
            Log.set(pos,str);
        }
    }

    public void showLog(){
        for(int i=0;i<num&&i<Log.size();++i){
            System.out.println(Log.elementAt(i));
        }
    }
}
