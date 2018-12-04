package com.isport.crawl.xiguaji;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geccocrawler.gecco.request.HttpRequest;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.isport.utils.StringUtils;

public class XiguajiService {

	private static final Logger LOGGER = LoggerFactory.getLogger(XiguajiService.class);

	// 用户登录页面地址
	private static final String login_url = "https://zs.xiguaji.com/UserLogin";
	// 登录页校验码开始标识
	private static final String chk_tag = "chk:";
	// 登录接口地址
	private static final String ajax_login_url = "https://zs.xiguaji.com/Login/Login";
	// 登录用户名
	private static final String username = "17600315450";
	// 登录密码
	private static final String password = "badoutiyu666";
	// 登录cookie保存文件
	private static final String cookie_file = "cookie_xiguaji";

	static Gson gson = new Gson();

	/**
	 * 完整登录流程服务
	 * 成功后，登录cookie存放在“cookie_xiguaji”文件中，以供后续请求使用
	 * 
	 * @author wangyuanxi
	 * @return true-登录成功 false-登录失败
	 */
	public static boolean login() {
		try {
			/*
			 * STEP-1 请求用户登录页，获取网站Cookie和登录校验码chk
			 */

			//创建一个HttpContext对象，用来保存Cookie
			HttpClientContext httpClientContext = HttpClientContext.create();

			//构造自定义Header信息
			List<Header> headerList = Lists.newArrayList();
			headerList.add(new BasicHeader(HttpHeaders.ACCEPT,
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"));
			headerList.add(new BasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
					+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"));
			headerList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"));
			headerList.add(new BasicHeader(HttpHeaders.CACHE_CONTROL, "max-age=0"));
			headerList.add(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive"));
			headerList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE,
					"zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4,ja;q=0.2," + "de;q=0.2"));

			//构造自定义的HttpClient对象
			HttpClient httpClient = HttpClients.custom().setDefaultHeaders(headerList).build();

			//构造登录页请求对象
			URI uri = new URIBuilder(login_url).build();
			HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(uri).build();

			//执行请求，传入HttpContext，将会得到请求结果的信息
			HttpResponse response = httpClient.execute(httpUriRequest, httpClientContext);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "UTF-8");
			System.out.println(html);

			//从响应信息中获取登录校验码
			int index = html.indexOf(chk_tag);
			if (index == -1) {
				return false;
			}
			String str = html.substring(index + chk_tag.length(), index + chk_tag.length() + 10);
			String chk = str.substring(str.indexOf("'") + 1, str.lastIndexOf("'"));

			/*
			 * STEP-2 AJAX请求调用登录接口，获取网站登录Cookie
			 */

			//请求参数
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("email", username);
			paramMap.put("password", password);
			paramMap.put("validateCode", "");
			paramMap.put("chk", chk);
			String postData = gson.toJson(paramMap);

			//构造一个Post请求
			HttpPost httpPost = new HttpPost(ajax_login_url);
			httpPost.addHeader("Content-type", "application/json; charset=utf-8");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setEntity(new StringEntity(postData, Charset.forName("UTF-8")));

			//使用带有Cookie的HttpClient请求调用登录接口
			response = httpClient.execute(httpPost, httpClientContext);
			entity = response.getEntity();
			String responseData = EntityUtils.toString(entity, "UTF-8");
			ResponseBean result = gson.fromJson(responseData, ResponseBean.class);

			//从请求结果中获取Cookie，此时的Cookie已经带有登录信息了
			CookieStore cookieStore = httpClientContext.getCookieStore();

			//这个CookieStore保存了我们的登录信息，我们可以先将它保存到某个本地文件，后面直接读取使用
			saveCookieStore(cookieStore);

			return result.getStatus();
		} catch (Exception e) {
			LOGGER.error(StringUtils.getStackTrace(e));
		}
		return false;
	}

	/**
	 * 用户登录接口服务
	 * 
	 * @author wangyuanxi
	 * @param request 登录请求前，上一次请求的request对象
	 * @param args 登录所需参数
	 * @return true-登录成功 false-登录失败
	 */
	public static boolean userLogin(HttpRequest request, Object... args) {
		try {
			//从登录页中获取登录校验码
			String html = (String) args[0];
			int index = html.indexOf(chk_tag);
			if (index == -1) {
				return false;
			}
			String str = html.substring(index + chk_tag.length(), index + chk_tag.length() + 10);
			String chk = str.substring(str.indexOf("'") + 1, str.lastIndexOf("'"));

			//构造请求参数
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("email", username);
			paramMap.put("password", password);
			paramMap.put("validateCode", "");
			paramMap.put("chk", chk);
			String postData = gson.toJson(paramMap);

			//构造一个Post请求
			HttpPost httpPost = new HttpPost(ajax_login_url);
			httpPost.addHeader("Content-type", "application/json; charset=utf-8");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setEntity(new StringEntity(postData, Charset.forName("UTF-8")));

			//构造请求Cookie
			Map<String, String> cookies = request.getCookies();
			CookieStore cookieStore = new BasicCookieStore();
			for (Entry<String, String> entry : cookies.entrySet()) {
				BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), entry.getValue());
				cookie.setPath("/");
				cookie.setDomain(httpPost.getURI().getHost());
				cookieStore.addCookie(cookie);
			}
			System.out.println(cookieStore);

			//创建一个HttpContext对象，用来保存Cookie
			HttpClientContext httpClientContext = HttpClientContext.create();

			//构造自定义的HttpClient对象
			HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

			//使用带有Cookie的HttpClient请求调用登录接口
			HttpResponse response = httpClient.execute(httpPost, httpClientContext);
			HttpEntity entity = response.getEntity();
			String responseData = EntityUtils.toString(entity, "UTF-8");
			ResponseBean result = gson.fromJson(responseData, ResponseBean.class);

			//从请求结果中获取Cookie，此时的Cookie已经带有登录信息了
			cookieStore = httpClientContext.getCookieStore();

			//这个CookieStore保存了我们的登录信息，我们可以先将它保存到某个本地文件，后面直接读取使用
			saveCookieStore(cookieStore);

			LOGGER.info("login result: " + responseData);
			return result.getStatus();
		} catch (Exception e) {
			LOGGER.error("login exception: " + StringUtils.getStackTrace(e));
		}
		return false;
	}

	//使用序列化的方式保存CookieStore到本地文件，方便后续的读取使用
	public static void saveCookieStore(CookieStore cookieStore) throws IOException {
		FileOutputStream fs = new FileOutputStream(cookie_file);
		ObjectOutputStream os = new ObjectOutputStream(fs);
		os.writeObject(cookieStore);
		os.close();
	}

	//读取Cookie的序列化文件，读取后可以直接使用
	public static CookieStore readCookieStore() throws IOException, ClassNotFoundException {
		FileInputStream fs = new FileInputStream(cookie_file);
		ObjectInputStream ois = new ObjectInputStream(fs);
		CookieStore cookieStore = (CookieStore) ois.readObject();
		ois.close();
		return cookieStore;
	}

	public static void main(String[] args) {
		login();
	}
}

class ResponseBean {
	// 登录结果
	private boolean Status;
	// 跳转页
	private String RedirectUrl;

	public boolean getStatus() {
		return Status;
	}

	public void setStatus(boolean status) {
		Status = status;
	}

	public String getRedirectUrl() {
		return RedirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		RedirectUrl = redirectUrl;
	}

}
