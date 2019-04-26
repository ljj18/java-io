/**
 * 文件名称:          		IOScanner.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.utils;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.IOLifecycle;
import com.ljj.io.IScannerCallback;

/**
 * 
 * 
 * Version 1.0.0
 * 
 * @author liangjinjing
 * 
 * Date 2019-04-26 14:55
 * 
 */
public class IOScanner implements IOLifecycle {

    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    /*
     * 
     */
    private IScannerCallback callback;
    /*
     * 
     */
    private Scanner scaner;

    /**
     * 
     * @param callback
     */
    public IOScanner(IScannerCallback callback) {
        this.callback = callback;
        this.scaner = new Scanner(System.in);
    }

    /**
     * 
     *
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            new Thread(() -> {
                System.out.print("请输入表达式：");
                while (isRunning.get()) {
                    String msg = scaner.nextLine();
                    if ("q".equals(msg) || "Q".equals(msg)) {
                        break;
                    }
                    callback.onScannerMessage(msg);
                }
                isRunning.set(false);
                System.out.println("正常退出!!!");
                System.exit(1);
            }, "IO Scanner-1").start();
        }
    }

    /**
     *
     */
    public void stop() {
        if (!isRunning.get()) {
            scaner.close();
        }
    }
}
