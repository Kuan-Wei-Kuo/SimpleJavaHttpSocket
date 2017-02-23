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

		logger.info("��l��Server��...");

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (Exception e) {
			logger.error("Server��l�ƥ���...", e);
		}
	}

	public void startServer() {
		logger.info("Server�w�g�Ұ�...");

		while (isRunning) {
			try {

		        
				Socket clientSocket = serverSocket.accept();

				logger.info("���o�s�u�AInetAddress = " + clientSocket.getInetAddress());
				
				BufferedInputStream bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
				byte[] b = new byte[1024];
				String data = "";
				int length;
				
				while ((length = bufferedInputStream.read(b)) > 0) {
					data += new String(b, 0, length);
					
					//�ѪRhttp header
					HttpRequestParser.parserHttpRequest(data);
					
					//�]���ثe�s�����w�]�N�Okeep-alive �]�����|�_���s���A�ڭ̻ݭn��@�çP�_request�O�_����
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
		        out.print("HTTP/1.1 200 \r\n"); // ���� HTTP/1.1 ; ���A 200
		        out.print("Content-Type: text/html; charset=UTF-8\r\n"); //�o���Ӥ��θ����S
		        out.print("Content-length: " + response.length() + "\r\n"); //�o�������HTTP 1.1�D�`���n�A�Y�S���^�ǥ��T���סA����87%�|�X���D
		        out.print("Status: 200\r\n"); 
		        out.print("Access-Control-Allow-Origin: *\r\n"); //�^�Ǹ�ƭn���o�Ӽ��Y�A�ثe�S����w�������
		        out.print("Date: " + today + "\r\n");
		        out.println("Connection: keep-alive\r\n");
		        out.print("\r\n"); // ������w
		        out.print(response); //�q�`������w�᭱���|���W�ڭ̪���ơC
		        out.flush(); //��s�ƾ�

			} catch (Exception e) {
				logger.error("Socket�s�u�����D...", e);
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
