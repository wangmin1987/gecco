package com.isport.bean;

import java.util.List;

import com.isport.utils.StringUtils;

public class BodyBean{
    private List<Data> data;
   
    public void setData(List<Data> data) {
         this.data = data;
     }
     public List<Data> getData() {
         return data;
     }
     
 public class Data{	    
    private String url;
    private String[] mthumbs; // 头图
    private String ltitle;
    private String stitle;
    private String title;
    private long ctime;
    private String[] labels_show;
//    private String[] tags;
    private int type;
    private String thumb;
    
    public String getTitleS() {
    	String result="";
    	if(!StringUtils.isNUll(this.title)) {
    		result = this.title;
    	}else if(!StringUtils.isNUll(this.stitle)) {
    		result = this.stitle;
    	}else if(!StringUtils.isNUll(this.stitle)) {
    		result = this.stitle;
    	}
    	return result;
    }
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

//	public String[] getTags() {
//		return tags;
//	}
//
//	public void setTags(String[] tags) {
//		this.tags = tags;
//	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getStitle() {
		return stitle;
	}

	public void setStitle(String stitle) {
		this.stitle = stitle;
	}
	//sina type = 1 图文，=2 轮播，=3 视频
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFthumb() {
		if(mthumbs!=null && mthumbs.length>0) {
			String url = mthumbs[0];
			if(url.indexOf("http:")==-1) {
				url ="http:"+url;
			}
			return url;
		}else {
			return "";
		}
	}
	public String[] getMthumbs() {
		return mthumbs;
	}

	public void setMthumbs(String[] mthumbs) {
		this.mthumbs = mthumbs;
	}

	public String getLtitle() {
		return ltitle;
	}

	public void setLtitle(String ltitle) {
		this.ltitle = ltitle;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public String getLabels_show() {
		String result = "";
		if(labels_show!=null && labels_show.length>0) {
			for(String label:labels_show) {
				result += label+ " ";
			}
		}
		return result;
	}

	public void setLabels_show(String[] labels_show) {
		this.labels_show = labels_show;
	}
	
      
}
}

