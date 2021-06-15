package lab3.entity.c;

public class Address{
    String address;

    int port;

    public Address(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getIP() {
        return address;
    }

    public int getPort() {
        return port;
    }
}