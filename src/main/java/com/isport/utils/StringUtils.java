package com.isport.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.xfire.util.Base64;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StringUtils {
	@Autowired
	OssUtils ossUtils;
	public static String contentHandle(String str) {
		return str.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f\\x10]", "")
				.replaceAll("[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]", "")
				.replaceAll("&#","").replaceAll("0x0", "");
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	public static boolean isNUll(String str) {
		return (str==null||"".equals(str))?true:false;
	}
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			t.printStackTrace(pw);
			return sw.toString();
		} finally {
			pw.close();
		}
	}
	public static String md5(String plainText) {
		byte[] secretBytes = null;
		try {
			secretBytes = MessageDigest.getInstance("md5").digest(
					plainText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("没有md5这个算法！");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
		// 如果生成数字未满32位，需要前面补0
		for (int i = 0; i < 32 - md5code.length(); i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}
	public static String convertStreamToString(String filePath,StringBuffer sbf){
		File file=new File(filePath);
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String str="";
			while ((str=br.readLine())!=null) {
				sbf.append(str);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbf.toString();
	}
	public static List<String> convertStreamToString(String filePath){
		List<String>  wbList=new ArrayList<String>();
		File file=new File(filePath);
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String str="";
			while ((str=br.readLine())!=null) {
				wbList.add(str);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wbList;
	}
	public static String getUserInfo(String encryptedData, String sessionKey, String iv) {
	    String result = "";
	    // 被加密的数据
	    byte[] dataByte = Base64.decode(encryptedData);
	    // 加密秘钥
	    byte[] keyByte = Base64.decode(sessionKey);
	    // 偏移量
	    byte[] ivByte = Base64.decode(iv);
	    try {
	        // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
	        int base = 16;
	        if (keyByte.length % base != 0) {
	            int groups = keyByte.length / base
	                    + (keyByte.length % base != 0 ? 1 : 0);
	            byte[] temp = new byte[groups * base];
	            Arrays.fill(temp, (byte) 0);
	            System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
	            keyByte = temp;
	        }
	        // 初始化
	        Security.addProvider(new BouncyCastleProvider());
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
	        SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
	        AlgorithmParameters parameters = AlgorithmParameters
	                .getInstance("AES");
	        parameters.init(new IvParameterSpec(ivByte));
	        cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
	        byte[] resultByte = cipher.doFinal(dataByte);
	        if (null != resultByte && resultByte.length > 0) {
	            result = new String(resultByte, "UTF-8");
	        }
	    } catch (NoSuchAlgorithmException e) {
	    } catch (NoSuchPaddingException e) {
	    } catch (InvalidParameterSpecException e) {
	    } catch (IllegalBlockSizeException e) {
	    } catch (BadPaddingException e) {
	    } catch (UnsupportedEncodingException e) {
	    } catch (InvalidKeyException e) {
	    } catch (InvalidAlgorithmParameterException e) {
	    } catch (NoSuchProviderException e) {
	    }
	    return result;
	}
	public static String getRemoteLoginUserIp(HttpServletRequest request)
	{
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getRemoteAddr();
		}
		System.out.println("THE REAL IPS IS:"+ip);
		if (ip != null && ip.length() > 15)
		{   
			if (ip.indexOf(",") > 0)
			{
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		return ip;
	}
	public static String createSign(String characterEncoding, HashMap<String, Object> parameters,String wxKey){
		StringBuffer sb = new StringBuffer();
		Object[] key_arr = parameters.keySet().toArray();
		Arrays.sort(key_arr);//这个是重点，这一句不能少，要排序
		for (Object key : key_arr)
		{
			Object value = parameters.get(key);
			if (null != value && !"".equals(value) && !"sign".equals(key) && !"key".equals(key))
			{
				sb.append(key + "=" + value + "&");
			}
		}
		sb.append("key=" + wxKey);//最后加密时添加商户密钥，由于key值放在最后，所以不用添加到SortMap里面去，单独处理，编码方式采用UTF-8
		String sign = md5(sb.toString()).toUpperCase();
		return sign;
	}
	public static String checkParam() {
		return null;}
	public static String getSubString(String html,String beginStr,String endStr) {
		int stpos = html.indexOf(beginStr)+beginStr.length();
		int endpos = html.indexOf(endStr, stpos);
		String result = html.substring(stpos, endpos);
		return result;
		
	}
	
	public static List<String> getImgUrl(Document doc){
		List<String> imgList=new ArrayList<>();
		// 文章图片
		Elements elementsByTag = doc.getElementsByTag("img");
		for (Element element : elementsByTag) {
			imgList.add(element.attr("src"));
		}
		return imgList;
		
	}
	//替换正文中的图片路径
	public boolean uploadImg(Element fragDoc) {
		try {
			Elements imgs = fragDoc.select("img");
			for (Element ele : imgs) {
				if (ele.attr("src") != "") {
					System.out.println(ele.attr("src"));
					String href = ele.attr("src");
					if (href.indexOf("http") == -1) {
						href = "http:" + href;
					}

					String filePath = ossUtils.uploadImage(href);
					if(filePath==null) {
						return false;
					}
					System.out.println(filePath);
					ele.attr("src", filePath);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return true;

	}
//	@Test
	public List<String> getImageUrl() {
		String html1 = "<img alt=\"谌龙对金廷竖大拇指\" src=\"//n.sinaimg.cn/sports/transform/360/w650h510/20180826/4_nf-hifuvph1540167.jpg\"/>";
		String regEx = "(?<=\\<img alt=\"[\\u0391-\\uFFE5]{1,10}\" src=\").*?(?=\"\\/\\>)";
	    Pattern pattern = Pattern.compile(regEx);
	    Matcher matcher = pattern.matcher(html1);
	    List<String> list = new ArrayList<String>();
	    while(matcher.find()){
	    	System.out.println(matcher.group());
	    	list.add(matcher.group());
	    }
	    return list;
		
	}
	
	@Test
	public void saveImage() {
		String url = "http://n.sinaimg.cn/sports/transform/360/w650h510/20180826/4_nf-hifuvph1540167.jpg\r\n";
		InputStream is = null;
		FileOutputStream fout = null;
		try {
			System.out.println(url);
			URL weburl = new URL(url);
			is = weburl.openStream();
			String fileName = "/Users/dony/Desktop/"+System.currentTimeMillis()+".jpg";
			fout = new FileOutputStream(fileName);
			byte[] b = new byte[1024];
			int len;
			while((len = is.read(b))!=-1) {
				fout.write(b, 0, len);
			}
			fout.flush();
		}catch(Exception e) {
			
		}finally {
			try {
				fout.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * byte数组转字符串
	 * 
	 * @author wangyuanxi
	 * @return
	 */
	public static String byteArrayToStr(byte[] byteArray) {
	    if (byteArray == null) {
	        return "";
	    }
	    String str = new String(byteArray);
	    return str;
	}
	
	/**
	 * 截取json字符串
	 * 
	 * @author wangyuanxi
	 * @return
	 */
	public static String getJsonFromString(String string, String beginStr, String endStr) {
		int stpos = string.indexOf(beginStr) + beginStr.length();
		int endpos = string.lastIndexOf(endStr);
		return string.substring(stpos, endpos);
	}
	
}
