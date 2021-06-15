package lab3.test;

import lab3.my2pc.entity.c.Address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coor {

    /* 存活的参与者 */
    private static final Map<Address, String> aliveMap = new HashMap();

    /* 死亡的参与者 */
    private static final List<Address> deadList = new ArrayList<>();

    static {
        deadList.add(new Address("192",10));
        deadList.add(new Address("193",10));
    }

    public static void main(String[] args) {
        t();
        System.out.println(deadList.size());
        System.out.println(aliveMap.size());
    }
    public static void t(){
        deadList.forEach(address -> {
            aliveMap.put(address, "socket");
        });
        aliveMap.forEach((address, socket)->{
            deadList.remove(address);
        });
    }

}
