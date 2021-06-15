package lab3.entity.p;

import lab3.util.LogUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class DataImf {
    public int selfPort;
    public int coodinatorPort;
    public InetAddress selfIp;
    public InetAddress  coodinatorIp;
    public Map<String,String> data;
    public LogUtil selfLog;
    public void dataImf(InetAddress selfIp,int selfPort,InetAddress coodinatorIp,int coordinatorPort) throws UnknownHostException {
        this.coodinatorIp=coodinatorIp;
        this.selfIp=selfIp;
        this.selfPort=selfPort;
        this.coodinatorPort=coordinatorPort;
        selfLog=new LogUtil();
        selfLog.inializeLog(10);
        System.out.println(InetAddress.getLocalHost().toString());
        data=new HashMap<String,String>();
    }

    public void showData(){
        for (String s : data.keySet()) {
            System.out.println("key:" + s);
            System.out.println("values:" + data.get(s));
        }
    }
}
