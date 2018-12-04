package com.isport.utils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	public static String doPost(String serverUrl, Map<String, String> paramMap) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(serverUrl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (paramMap != null) {
			for (Entry<String, String> param : paramMap.entrySet()) {
				nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			}
		}

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();
			String str = EntityUtils.toString(resEntity, "UTF-8");
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String doGet(String serverUrl, String charset) {
		if (charset.isEmpty()) {
			charset = "UTF-8";
		}
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(serverUrl);
		try {
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity resEntity = response.getEntity();
			String str = EntityUtils.toString(resEntity, charset);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String doPostBodyJson(String serverUrl, String bodyParam) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(serverUrl);
		httpPost.addHeader("Content-type", "application/json; charset=utf-8");
		httpPost.setHeader("Accept", "application/json");
		try {
			httpPost.setEntity(new StringEntity(bodyParam, Charset.forName("UTF-8")));
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();
			String str = EntityUtils.toString(resEntity, "UTF-8");
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String ascii2native(String ascii) {
		int n = ascii.length() / 6;
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0, j = 2; i < n; i++, j += 6) {
			String code = ascii.substring(j, j + 4);
			char ch = (char) Integer.parseInt(code, 16);
			sb.append(ch);
		}
		return sb.toString();
	}
	
	public static String doGetHeader(String serverUrl) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(serverUrl);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
		try {
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity resEntity = response.getEntity();
			String str = EntityUtils.toString(resEntity, "UTF-8");
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String doInstagramGet(String serverUrl) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httpGet = new HttpPost(serverUrl);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
		httpGet.addHeader("Referer", "http://www.veryins.com/sports");
		httpGet.addHeader("cookie", "Hm_lvt_453ab3ca06e82d916be6d6937c3bf101=1536205047; Hm_lpvt_453ab3ca06e82d916be6d6937c3bf101=1536205514; connect.sid=s%3A4ySHm-1TJ_ChGk2Jqyb1jmRn-1plcGfJ.FA060YYU5wi%2BGOG%2FGPP%2BekTWb2OGlSTtCGVHp2MgDeQ");
		
		try {
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity resEntity = response.getEntity();
			String str = EntityUtils.toString(resEntity, "UTF-8");
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String doInstagramPost(String serverUrl) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httpGet = new HttpPost(serverUrl);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
		httpGet.addHeader("Referer", "http://www.veryins.com/sports");
		httpGet.addHeader("cookie", "Hm_lvt_453ab3ca06e82d916be6d6937c3bf101=1536205047; Hm_lpvt_453ab3ca06e82d916be6d6937c3bf101=1536205514; connect.sid=s%3A4ySHm-1TJ_ChGk2Jqyb1jmRn-1plcGfJ.FA060YYU5wi%2BGOG%2FGPP%2BekTWb2OGlSTtCGVHp2MgDeQ");
		
		try {
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity resEntity = response.getEntity();
			String str = EntityUtils.toString(resEntity, "UTF-8");
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static InputStream doInstagramVideo(String serverUrl) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(serverUrl);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
		httpGet.addHeader("Referer", "http://www.veryins.com/sports");
		httpGet.addHeader("cookie", "Hm_lvt_453ab3ca06e82d916be6d6937c3bf101=1536205047; Hm_lpvt_453ab3ca06e82d916be6d6937c3bf101=1536205514; connect.sid=s%3A4ySHm-1TJ_ChGk2Jqyb1jmRn-1plcGfJ.FA060YYU5wi%2BGOG%2FGPP%2BekTWb2OGlSTtCGVHp2MgDeQ");
		
		try {
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity resEntity = response.getEntity();
			InputStream content = resEntity.getContent();
			
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
