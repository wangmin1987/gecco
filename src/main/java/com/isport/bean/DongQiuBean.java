package com.isport.bean;

/**
 * Copyright 2018 bejson.com 
 */
import java.util.List;
import java.util.Map;

/**
* Auto-generated: 2018-09-03 10:24:22

*/
public class DongQiuBean {

   private List<Map<String,Object>> data;
   
    
    
    public List<Map<String, Object>> getData() {
	return data;
}



public void setData(List<Map<String, Object>> data) {
	this.data = data;
}



	public class Data {

        private long id;
        private String title;
        private String description;
        private long user_id;
        private String type;
        private String display_time;
        private String thumb;
        private int comments_total;
        private String web_url;
        private String official_account;
        public void setId(long id) {
             this.id = id;
         }
         public long getId() {
             return id;
         }

        public void setTitle(String title) {
             this.title = title;
         }
         public String getTitle() {
             return title;
         }

        public void setDescription(String description) {
             this.description = description;
         }
         public String getDescription() {
             return description;
         }

        public void setUser_id(long user_id) {
             this.user_id = user_id;
         }
         public long getUser_id() {
             return user_id;
         }

        public void setType(String type) {
             this.type = type;
         }
         public String getType() {
             return type;
         }

        public void setDisplay_time(String display_time) {
             this.display_time = display_time;
         }
         public String getDisplay_time() {
             return display_time;
         }

        public void setThumb(String thumb) {
             this.thumb = thumb;
         }
         public String getThumb() {
             return thumb;
         }

        public void setComments_total(int comments_total) {
             this.comments_total = comments_total;
         }
         public int getComments_total() {
             return comments_total;
         }

        public void setWeb_url(String web_url) {
             this.web_url = web_url;
         }
         public String getWeb_url() {
             return web_url;
         }

        public void setOfficial_account(String official_account) {
             this.official_account = official_account;
         }
         public String getOfficial_account() {
             return official_account;
         }

    }

}
