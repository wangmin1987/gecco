package com.isport.bean;

import java.util.List;

/**
* Auto-generated: 2018-09-06 14:57:29
*
* @author bejson.com (i@bejson.com)
* @website http://www.bejson.com/java2pojo/
*/
public class InstagramBan {

   private User user;
   public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }
    
    public class User {

        private Media media;
        
        public void setMedia(Media media) {
             this.media = media;
         }
         public Media getMedia() {
             return media;
         }
    }
    public class Page_info {

        private boolean has_next_page;
        private String end_cursor;
        public void setHas_next_page(boolean has_next_page) {
             this.has_next_page = has_next_page;
         }
         public boolean getHas_next_page() {
             return has_next_page;
         }

        public void setEnd_cursor(String end_cursor) {
             this.end_cursor = end_cursor;
         }
         public String getEnd_cursor() {
             return end_cursor;
         }

    }
    public class Media {

        private List<Nodes> nodes;
        private int count;
        private Page_info page_info;
        public void setNodes(List<Nodes> nodes) {
             this.nodes = nodes;
         }
         public List<Nodes> getNodes() {
             return nodes;
         }

        public void setCount(int count) {
             this.count = count;
         }
         public int getCount() {
             return count;
         }

        public void setPage_info(Page_info page_info) {
             this.page_info = page_info;
         }
         public Page_info getPage_info() {
             return page_info;
         }

    }
    public class Nodes {

        private List<String> thumbnail_resources;
        private String srcset;
        private String thumbnail_src;
        private String date;
        private String code;
        private String caption;
        private String _id;
        private boolean is_video;
        private String display_src;
        private int likes;
        private String __typename;
        public void setThumbnail_resources(List<String> thumbnail_resources) {
             this.thumbnail_resources = thumbnail_resources;
         }
         public List<String> getThumbnail_resources() {
             return thumbnail_resources;
         }

        public void setSrcset(String srcset) {
             this.srcset = srcset;
         }
         public String getSrcset() {
             return srcset;
         }

        public void setThumbnail_src(String thumbnail_src) {
             this.thumbnail_src = thumbnail_src;
         }
         public String getThumbnail_src() {
             return thumbnail_src;
         }

        public void setDate(String date) {
             this.date = date;
         }
         public String getDate() {
             return date;
         }

        public void setCode(String code) {
             this.code = code;
         }
         public String getCode() {
             return code;
         }

        public void setCaption(String caption) {
             this.caption = caption;
         }
         public String getCaption() {
             return caption;
         }

        public void set_id(String _id) {
             this._id = _id;
         }
         public String get_id() {
             return _id;
         }

        public void setIs_video(boolean is_video) {
             this.is_video = is_video;
         }
         public boolean getIs_video() {
             return is_video;
         }

        public void setDisplay_src(String display_src) {
             this.display_src = display_src;
         }
         public String getDisplay_src() {
             return display_src;
         }

        public void setLikes(int likes) {
             this.likes = likes;
         }
         public int getLikes() {
             return likes;
         }

        public void set__typename(String __typename) {
             this.__typename = __typename;
         }
         public String get__typename() {
             return __typename;
         }

    }

}
