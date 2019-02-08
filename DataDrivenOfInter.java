package com.testing.inter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.testing.UI.AutoLogger;
import com.testing.common.ExcelWriter;

public class DataDrivenOfInter {
	public HttpClientKw client;
	public Map<String, String> paramMap;
	//加入成员变量，方便在每一行用例调用时，统一操作的行数、返回结果的断言、excel的写入。
	public String tmpResponse;
	public int line = 0; // 成员变量行数，用于在用例执行时保持执行行和写入行一致
	public ExcelWriter outExcel;
	
	public DataDrivenOfInter(String casePath,String resultPath) {
		client=new HttpClientKw();
		paramMap=new HashMap<String,String>();
		outExcel =new ExcelWriter(casePath, resultPath);
	}
	
	public DataDrivenOfInter(ExcelWriter excel) {
		client=new HttpClientKw();
		paramMap=new HashMap<String,String>();
		outExcel=excel;
	}
	
	public String testGet(String url,String input) {
		String response=null;
		try {
			String param=toParam(input);
			response =client.doGet(url, param);
			tmpResponse=response;
			outExcel.writeCell(line, 11, response);
			return response;
		} catch (Exception e) {
			AutoLogger.log.error("get方法发送失败，请检查");
			AutoLogger.log.error(e,e.fillInStackTrace());
			outExcel.writeFailCell(line, 10, "FAIL");
			outExcel.writeFailCell(line, 11, "get方法发送失败，请检查log");
			return response;
		}
		
	}
	
	public String testPost(String url,String input) {
		String response=null;
		try {
			String param=toParam(input);
			response =client.doPost(url, param);
			tmpResponse=response;
			outExcel.writeCell(line, 11, response);
			return response;
		} catch (Exception e) {
			AutoLogger.log.error("post方法发送失败，请检查");
			AutoLogger.log.error(e,e.fillInStackTrace());
			outExcel.writeFailCell(line, 10, "FAIL");
			outExcel.writeFailCell(line, 11, "get方法发送失败，请检查log");
			return response;
		}
		
	}
	
	public void saveCookie() {
		try {
			client.saveCookie();
			outExcel.writeCell(line, 10, "PASS");
		} catch (Exception e) {
			outExcel.writeCell(line, 10, "FAIL");
		}
	}

	public void clearCookie() {
		try {
			client.clearCookie();
			outExcel.writeCell(line, 10, "PASS");
		} catch (Exception e) {
			outExcel.writeCell(line, 10, "FAIL");
		}
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
			outExcel.writeCell(line, 10, "PASS");
		} catch (JSONException e) {
			AutoLogger.log.error("头域参数格式错误，请检查");
			AutoLogger.log.error(e,e.fillInStackTrace());
			outExcel.writeFailCell(line, 10, "FAIL");
		}
		client.addHeader(jsonmap);
		
	}
	
	public void clearHeader() {
		try {
			client.clearHeader();
			outExcel.writeCell(line, 10, "PASS");
		} catch (Exception e) {
			outExcel.writeFailCell(line, 10, "FAIL");
		}
	}
	
	public void saveParam(String key,String jsonPath) {
		String value;
		try {
			value = JsonPath.read(tmpResponse,jsonPath).toString();
			paramMap.put(key, value);
			outExcel.writeCell(line, 10, "PASS");
		} catch (Exception e) {
			AutoLogger.log.error("保存参数失败");
			AutoLogger.log.error(e,e.fillInStackTrace());
			outExcel.writeCell(line, 10, "FAIL");
		}
	}
	
	public String toParam(String origin) {
		String param=origin;
		for(String key:paramMap.keySet()) {
		param=param.replaceAll("\\{"+key+"\\}", paramMap.get(key));
		}
		return param;
	}

	public void assertSame(String jsonPath,String expect) {
		try {
			String actual=JsonPath.read(tmpResponse,jsonPath).toString();
			if(actual!=null&&actual.equals(expect)) {
				AutoLogger.log.info("测试通过！");
				outExcel.writeCell(line, 10, "PASS");
			}
			else {
				AutoLogger.log.info("测试失败！");
				outExcel.writeFailCell(line, 10, "FAIL");
			}
		} catch (Exception e) {
			AutoLogger.log.error("解析失败，请检查jsonPath表达式");
			AutoLogger.log.error(e,e.fillInStackTrace());
			outExcel.writeFailCell(line, 10, "FAIL");
		}
	}
	
	public void assertContains(String jsonPath,String expect) {
		try {
			String actual=JsonPath.read(tmpResponse,jsonPath).toString();
			if(actual!=null&&actual.contains(expect)) {
				AutoLogger.log.info("测试通过！");
				outExcel.writeCell(line, 10, "PASS");
			}
			else {
				AutoLogger.log.info("测试失败！");
				outExcel.writeFailCell(line, 10, "FAIL");
			}
		} catch (Exception e) {
			AutoLogger.log.error("解析失败，请检查jsonPath表达式");
			AutoLogger.log.error(e,e.fillInStackTrace());
			outExcel.writeFailCell(line, 10, "FAIL");
		}
	}
}
