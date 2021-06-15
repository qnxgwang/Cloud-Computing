package lab3.util;

import java.util.ArrayList;
import java.util.List;

public class DivideUtil {

    public static List<String> getList(List<String> stringList){
        List<String> sendList = new ArrayList<>();
        String temp = "";
        while(!stringList.isEmpty()){
            String str = stringList.remove(0);
            String last = temp+" "+str;
            last = last.trim();
            if(last.getBytes().length > 1024){
                sendList.add(temp);
                temp = str;
            }else{
                temp = last;
            }
        }
        sendList.add(temp);
        return sendList;
    }
}
