package com.example.sample.server;

import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.example.sample.utils.HttpRequestParser;

public class HttpSocketServer extends Thread{

	private static Logger logger = Logger.getLogger(HttpSocketServer.class);

	private static final int SERVER_PORT = 8080;

	private ServerSocket serverSocket;

	private boolean isRunning = true;

	public HttpSocketServer() {

		logger.info("初始化Server中...");

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (Exception e) {
			logger.error("Server初始化失敗...", e);
		}
	}

	public void startServer() {
		logger.info("Server已經啟動...");

		while (isRunning) {
			try {

		        
				Socket clientSocket = serverSocket.accept();

				logger.info("取得連線，InetAddress = " + clientSocket.getInetAddress());
				
				BufferedInputStream bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
				byte[] b = new byte[1024];
				String data = "";
				int length;
				
				while ((length = bufferedInputStream.read(b)) > 0) {
					data += new String(b, 0, length);
					
					//解析http header
					HttpRequestParser.parserHttpRequest(data);
					
					//因為目前瀏覽器預設就是keep-alive 因此不會斷掉連結，我們需要實作並判斷request是否結束
					if(HttpRequestParser.getLastMethod().equals("GET")) {
						break;
					} else if(HttpRequestParser.getLastMethod().equals("POST") && HttpRequestParser.getLastData().length() == HttpRequestParser.getLastContentLength()) {
						break;
					}
				}
				
				
				HashMap<String, String> httpRequestHeaderMap = HttpRequestParser.parserHttpRequest(data);
				
				for(String key : httpRequestHeaderMap.keySet()) {
					logger.info("Hi -> " + key + ": " + httpRequestHeaderMap.get(key));
				}
				
				String response = "<html><head></head><body><h1>Hello World!</h1></body></html>";
				String today = new Date().toString();

				PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
		        out.print("HTTP/1.1 200 \r\n"); // 版本 HTTP/1.1 ; 狀態 200
		        out.print("Content-Type: text/html; charset=UTF-8\r\n"); //這應該不用解釋惹
		        out.print("Content-length: " + response.length() + "\r\n"); //這部分對於HTTP 1.1非常重要，若沒有回傳正確長度，網頁87%會出問題
		        out.print("Status: 200\r\n"); 
		        out.print("Access-Control-Allow-Origin: *\r\n"); //回傳資料要有這個標頭，目前沒有鎖定任何網域
		        out.print("Date: " + today + "\r\n");
		        out.println("Connection: keep-alive\r\n");
		        out.print("\r\n"); // 結束協定
		        out.print(response); //通常結束協定後面都會接上我們的資料。
		        out.flush(); //更新數據

			} catch (Exception e) {
				logger.error("Socket連線有問題...", e);
			}
		}
	}

	@Override
	public void run() {
		startServer();
	}
	
	public static void main(String args[]) {
		(new HttpSocketServer()).start();
	}
	
}
