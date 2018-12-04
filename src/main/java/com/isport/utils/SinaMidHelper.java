package com.isport.utils;


import org.apache.commons.lang.StringUtils;


public class SinaMidHelper {

	private static String[] str62keys = { "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
			"x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z" };

	public static long str62To10(String str62) {
		long i10 = 0;
		String[] array = strToArray(str62);
		for (int i = 0; i < array.length; i++) {
			long n = array.length - i - 1;
			String s = array[i];
			i10 += getIndex(s) * Math.pow(62, n);
		}
		return i10;
	}

	public static String intTostr62(int num) {
		String s62 = "";
		int r = 0;
		while (num != 0) {
			r = num % 62;
			s62 = str62keys[r] + s62;
			num = (int) Math.floor(num / 62);
		}
		return s62;
	}

	public static String[] strToArray(String str) {
		String[] s = new String[str.length()];
		for (int i = 0; i < str.length(); i++) {
			s[i] = str.substring(i, i + 1);
		}
		return s;
	}

	public static int getIndex(String s) {
		int t = 0;
		for (int i = 0; i < str62keys.length; i++) {
			if (s.equals(str62keys[i])) {
				t = i;
			}
		}
		return t;
	}

	public static String getcode(int postion) {
		return str62keys[postion];
	}

	public static String getidFromMid(String id) {
		String mid = "";
		for (int i = id.length() - 4; i > -4; i = i - 4) {
			int offset1 = i < 0 ? 0 : i;
			int offset2 = i + 4;
			String str = id.substring(offset1, offset2);
			str = String.valueOf(str62To10(str));
			if (offset1 > 0) {
				while (str.length() < 7) {
					str = "0" + str;
				}
			}
			mid = str + mid;
		}
		return mid;
	}

	public static String getmidFromId(String id) {
		String mid = "";
		for (int i = id.length() - 7; i > -7; i = i - 7) {
			int offset1 = i < 0 ? 0 : i;
			int offset2 = i + 7;
			String str = id.substring(offset1, offset2);
			int num = Integer.parseInt(str);
			String code62 = intTostr62(num);
			mid = code62 + mid;
		}
		return mid;
	}
	//20150126修改
	public static String Id2Mid(String str10) {
		String mid = "";
		int count = 1 ;
		for (int i = str10.length() - 7; i > -7; i = i - 7) // 从最后往前以7字节为一组读取字符
		{
			int offset = i < 0 ? 0 : i;
			int len = i < 0 ? str10.length() % 7 : 7;
			String temp = str10.substring(offset, offset + len);
			String url =IntToEnode62(Integer.valueOf(temp));
			if(count!=3){//z xghm uXym 生成的链接从右往左的前2组，4位一组，不足4位的补0
				for(int j=0;j<4-url.length();j++){
					url = "0"+url;
				}
			}
			mid = url+mid;                       
                      count++;
               }
		return mid;
	}
	public static String IntToEnode62(Integer int10) {
		String s62 = "";
		int r = 0;
		while (int10 != 0) {
			r = int10 % 62;
			s62 = str62keys[r] + s62;
			int10 = (int) Math.floor(int10 / 62.0);
		}
		return s62;
	}
	public static String getUrl(String wbid,String openid){
		String url = "";
		if(StringUtils.isNotBlank(wbid) && StringUtils.isNotBlank(openid)){
			url = "http://weibo.com/"+openid + "/" +Id2Mid(wbid);
		}
		return url;
	}
	

	public static void main(String[] args) {
		//3800711634986055  2216875995;3800674175508352   3053184035
		String url = getUrl("4288544551956494", "1731169267");
		System.out.println(url);
		
		/*String url = "http://weibo.com/"
                + bean.getOpenid()
                + "/" +getmidFromId("3760135077558854");
		System.out.println(getmidFromId("3760135077558854"));*/
	}
	
	public static String null2String(Object o){
		String ss = "";
		if(null != o){
			ss = o.toString();
		}
		return ss;
	}

}
