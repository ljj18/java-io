package com.ljj.io.bio;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * BIOI/O
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public final class ServerBetter {
	//
	private static int DEFAULT_PORT = 12345;
	//
	private static ServerSocket server;
	//
	private static ExecutorService executorService = Executors.newFixedThreadPool(60);
	//
	public static void start() throws IOException{
	    start(DEFAULT_PORT);
	}

	public synchronized static void start(int port) throws IOException{
		if(server != null) return;
		try{
			server = new ServerSocket(port);
			System.out.println("邦定的端号" + port);
			Socket socket;

			while(true){
				socket = server.accept();
				executorService.execute(new ServerHandler(socket));
			}
		}finally{
			if(server != null){
				System.out.println("服务务端启动失败");
				server.close();
				server = null;
			}
		}
	}
}