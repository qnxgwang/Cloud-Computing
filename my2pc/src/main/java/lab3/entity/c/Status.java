package lab3.entity.c;

public interface Status {
    int COMMAND = 0x01;

    int WAIT_READY = 0x02;

    int COMMIT = 0x03;

    int WAIT_ACK = 0x04;

    int CLIENT = 0x05;

}
