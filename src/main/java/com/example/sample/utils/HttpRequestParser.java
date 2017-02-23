package com.example.sample.utils;

import java.util.HashMap;

public class HttpRequestParser {

	private static String lastMethod;
	
	private static String lastData;
	
	private static int contentLength;
	
	public static HashMap<String, String> parserHttpRequest(String header) {
		
		HashMap<String, String> httpRequestHeaderMap = new HashMap<>();
		
		String[] headers = header.split("\r\n");
		
		int next = 0;
		for(String data : headers) {
			if(next == 0) {
				lastMethod = data.split(" /")[0];
				lastData = data.substring(data.indexOf("?") + "?".length() , data.indexOf(" H"));
				
				if(lastData.indexOf("=") != -1) {
					lastData = "";
				}
				
				httpRequestHeaderMap.put(null, data);
			} else if(next == headers.length - 1) {
				lastData = data;
			} else {
				String[] content = data.split(": ");
				if(content != null && content.length > 1) {
					httpRequestHeaderMap.put(content[0], content[content.length-1]);
					//Maybe have more requirement
					switch (content[0]) {
						case "Content-Length":
							contentLength = Integer.valueOf(content[content.length-1]);
							break;
					}
				}
			}
			next++;
		}
		
		return httpRequestHeaderMap;
	}
	
	public static String getLastMethod() {
		return lastMethod;
	}
	
	public static String getLastData() {
		return lastData;
	}
	
	public static int getLastContentLength() {
		return contentLength;
	}
	
}
