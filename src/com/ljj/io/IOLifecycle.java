/**
 * 文件名称:          		IOLifecycle.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io;

/**
 * 对角生命周期接口
 * 
 * Version		1.0.0      
 * 
 * @author		liangjinjing
 * 
 * Date			2019-04-25 18:12
 * 
 */
public interface IOLifecycle {
    
    /**
     * 开始
     */
    void start();
    
    /**
     * 结束
     */
    void stop();

}
