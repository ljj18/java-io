/**
 * 鏂囦欢鍚嶇О:          		AbstractTest.java
 * 鐗堟潈鎵�鏈堾 2019-2020 	鏃犻敗鐖辫秴淇℃伅鎶�鏈湁闄愬叕鍙革紝淇濈暀鎵�鏈夋潈鍒�
 * 缂栬瘧鍣�:           		JDK1.8
 */

package com.ljj.io.client;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO: 鏂囦欢娉ㄩ噴
 * 
 * Version 1.0.0
 * 
 * @author liangjinjing
 * 
 * Date 2019-04-26 15:42
 * 
 */
public abstract class AbstractIOClientTest implements IClientTest {

    /*
     * 
     */
    public static final int DEFAULT_PORT = 12345;
    /*
     * 
     */
    public static final String DEFAULT_HOST = "10.108.3.46";
    
    
    /*
     * 
     */
    protected IClient client;
    /*
     * 
     */
    private ClientScanner ioScanner;
    
    /**
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 鍚姩瀹㈡埛绔�
     */
    public abstract void startClient();

    /**
     * 
     */
    public AbstractIOClientTest() {
        // 娣诲姞绋嬪簭閫�鍑洪挬瀛�
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
    }

    /**
     * 
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            // 鍚姩瀹㈡埛绔�
            startClient();
            //
            startScanner();
        }
    }
    
    /**
     * 
     */
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            if (ioScanner != null) {
                ioScanner.stop();
            }
            client = null;
            ioScanner = null;
        }
    }
    
    /**
     * 
     */
    public IAcceptHandler getAcceptHandler() {
       return ioScanner;
    }

    /**
     * 
     */
    private void startScanner() {
        ioScanner = new ClientScanner((String msg) -> {
            if (client != null) {
                client.sendMsg(msg);
            }
        });
        ioScanner.start();
    }


}
