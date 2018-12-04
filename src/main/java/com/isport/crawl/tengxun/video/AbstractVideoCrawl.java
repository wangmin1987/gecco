package com.isport.crawl.tengxun.video;
 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date; 

import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.selenium.AbstractDriver; 
import com.isport.utils.StringUtils;

/**
 * 视频爬虫 抽象类
 * 
 * @author 八斗体育
 *
 */
public abstract class AbstractVideoCrawl extends AbstractDriver {
	 
	/**
	 * 设置默认发布时间
	 * 
	 * @param newsInfoBean
	 */
	protected void setDefaultPubDate(NewsInfoBean newsInfoBean) {
		if (StringUtils.isNUll(newsInfoBean.getPub_date())) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			newsInfoBean.setPub_date(df.format(new Date()));
		}
	}

	/**
	 * 是否是合法数据，是否入库判断
	 * 
	 * @param newsInfoBean
	 * @return
	 */
	protected boolean isNUll(NewsInfoBean newsInfoBean) {
		String title = newsInfoBean.getTitle(), vedioUrl = newsInfoBean.getVideo_url();
		return StringUtils.isNUll(title) || StringUtils.isNUll(vedioUrl);
	}

	/**
	 * 设置腾讯视频常量
	 * 
	 * @param newsInfoBean
	 */
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_QQ.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_QQ.value);
		String authorId = "c8ca0634b7414ba5a0927f4e323f1ecc", channelId = "e5e4aa2ca8864c97a589b9d798a75eb6";
		newsInfoBean.setAuthor_id(authorId);
		newsInfoBean.setChannel_id(channelId);
	}
}
