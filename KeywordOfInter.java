package com.testing.inter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.testing.UI.AutoLogger;

public class KeywordOfInter {

	public HttpClientKw client;
	public Map<String, String> paramMap;
	
	public KeywordOfInter() {
		client=new HttpClientKw();
		paramMap=new HashMap<String,String>();
	}
	
	public String testGet(String url,String input) {
		String response=null;
		try {
			String param=toParam(input);
			response =client.doGet(url, param);
			return response;
		} catch (Exception e) {
			AutoLogger.log.error("get方法发送失败，请检查");
			AutoLogger.log.error(e,e.fillInStackTrace());
			return response;
		}
		
	}
	
	public String testPost(String url,String input) {
		String response=null;
		try {
			String param=toParam(input);
			System.out.println("param:"+param);
			response =client.doPost(url, param);
			return response;
		} catch (Exception e) {
			AutoLogger.log.error("post方法发送失败，请检查");
			AutoLogger.log.error(e,e.fillInStackTrace());
			return response;
		}
		
	}
	
	public void saveCookie() {
		client.saveCookie();
	}

	public void clearCookie() {
		client.clearCookie();
	}
	
	public void addHeader(String originJson) {
		Map<String, String> jsonmap=new HashMap<String, String>();
		String headerJson=toParam(originJson);
		System.out.println(headerJson);
		try {
			JSONObject json = new JSONObject(headerJson);
			System.out.println("json:"+json);
			Iterator<String> jsonit = json.keys();
			while (jsonit.hasNext()) {
				String jsonkey = jsonit.next();
				jsonmap.put(jsonkey, json.get(jsonkey).toString());
			}
		} catch (JSONException e) {
			AutoLogger.log.error("头域参数格式错误，请检查");
			AutoLogger.log.error(e,e.fillInStackTrace());
		}
		client.addHeader(jsonmap);
		
	}
	
	public void clearHeader() {
		client.clearHeader();
	}
	
	public void saveParam(String key,String json,String jsonPath) {
		String value;
		try {
			value = JsonPath.read(json,jsonPath).toString();
			paramMap.put(key, value);
		} catch (Exception e) {
			AutoLogger.log.error("保存参数失败");
			AutoLogger.log.error(e,e.fillInStackTrace());
		}
	}
	
	public String toParam(String origin) {
		String param=origin;
		for(String key:paramMap.keySet()) {
		param=param.replaceAll("\\{"+key+"\\}", paramMap.get(key));
		}
		return param;
	}

	public boolean assertSame(String result,String expect,String jsonPath) {
		boolean success=false;
		try {
			String actual=JsonPath.read(result,jsonPath).toString();
			if(actual!=null&&actual.equals(expect)) {
				AutoLogger.log.info("测试通过！");
				success=true;
				return success;
			}
			else {
				AutoLogger.log.info("测试失败！");
				success=false;
				return success;
			}
		} catch (Exception e) {
			AutoLogger.log.error("解析失败，请检查jsonPath表达式");
			AutoLogger.log.error(e,e.fillInStackTrace());
		}
		return success;
	}
	
	public void assertContains(String result,String expect,String jsonPath) {
		try {
			String actual=JsonPath.read(result,jsonPath).toString();
			if(actual!=null&&actual.contains(expect)) {
				AutoLogger.log.info("测试通过！");
			}
			else {
				AutoLogger.log.info("测试失败！");
			}
		} catch (Exception e) {
			AutoLogger.log.error("解析失败，请检查jsonPath表达式");
			AutoLogger.log.error(e,e.fillInStackTrace());
		}
	}
}
