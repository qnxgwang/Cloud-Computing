package lab3.util;

import java.util.concurrent.TimeoutException;

public class TimeOutThread extends Thread {
    private long timeout;

    /**
       * 计时是否被取消
       */
    private boolean isCanceled = false;

    /**
       * 当计时器超时时抛出的异常
       */
    private TimeoutException timeoutException;

    /**
       * 构造器
       * @param timeout 指定超时的时间
       */
    public TimeOutThread(long timeout,TimeoutException timeoutErr) {
        super();
        this.timeout = timeout;
        this.timeoutException = timeoutErr;
//设置本线程为守护线程
        this.setDaemon(true);
    }

    /**
       * 取消计时
       */
    public void cancel() {
        isCanceled = true;
    }

    /**
       * 启动超时计时器
       */
    public void run()
    {
        try {

            Thread.sleep(timeout);
            if(!isCanceled)throw timeoutException;
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}

