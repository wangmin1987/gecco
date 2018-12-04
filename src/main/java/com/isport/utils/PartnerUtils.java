package com.isport.utils;

public class PartnerUtils {
	
	public enum enumSex{
		MAN("男",1),WOMAN("女",2);
		
		int index;
		private String content;
		private enumSex(String content,int index){
			this.index = index;
			this.content = content;
		}
		
		public static String getContent(int index) {
			for(enumSex e:enumSex.values()) {
				if(e.index == index) {
					return e.content;
				}
			}
			return null;
		}
	}
	
	public enum enumWeight{
		W1("40KG以下",1),W2("40-45KG",2),W3("45-50KG",3),W4("50-55KG",4),W5("55-60KG",5),W6("60-70KG",6),W7("70-90KG",7),W8("70-90KG",8);
		
		int index;
		private String content;
		private enumWeight(String content,int index){
			this.index = index;
			this.content = content;
		}
		
		public static String getContent(int index) {
			for(enumWeight e:enumWeight.values()) {
				if(e.index == index) {
					return e.content;
				}
			}
			return null;
		}
	}
	
	public enum enumHeight{
		W1("1.4米以下",1),W2("1.4-1.6米",2),W3("1.6-1.7米",3),W4("1.7-1.8米",4),W5("1.8-1.9米",5),W6("1.9-2.0米",6),W7("2.0米以上",7);
		
		int index;
		private String content;
		private enumHeight(String content,int index){
			this.index = index;
			this.content = content;
		}
		
		public static String getContent(int index) {
			for(enumHeight e:enumHeight.values()) {
				if(e.index == index) {
					return e.content;
				}
			}
			return null;
		}
	}
	
	public enum enumCharacter{
		W1("孤傲高冷",1),W2("热情如火",2),W3("搞怪天才",3),W4("稳重敦实",4),W5("沉静如水",5),W6("积极进取",6),W7("豪放不羁",7),W8("小鸟依人",8),W9("青春阳光",9),W10("风趣幽默",10),W11("永不言弃",11);
		
		int index;
		private String content;
		private enumCharacter(String content,int index){
			this.index = index;
			this.content = content;
		}
		
		public static String getContent(int index) {
			for(enumCharacter e:enumCharacter.values()) {
				if(e.index == index) {
					return e.content;
				}
			}
			return null;
		}
	}
	
	public static String getConstell(int index) {
		String[] constells = {"白羊座","金牛座","双子座","巨蟹座","狮子座","处女座","天秤座","天蝎座","射手座","摩羯座","水瓶座","双鱼座"};
		return constells[index-1];
	}

}
