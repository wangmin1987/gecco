package com.isport.crawl.cctv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.selenium.AbstractDriver;
import com.isport.crawl.video.Duplicate;
import com.isport.utils.DateUtils;
import com.isport.utils.StringUtils;

/**
 * 排球抽象
 * @author 八斗体育
 *
 */  
public class AbstractCctvVideo extends AbstractDriver {  

	@Autowired
	protected Duplicate duplicate;
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
		newsInfoBean.setSource(Constants.NEWS_SOURCE_CCTV.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_CCTV.value);
		String authorId = "390630d910ae413f827638f910d2832a", channelId = "0a309fa2948e46bba3d742e3397eb649";
		newsInfoBean.setAuthor_id(authorId);
		newsInfoBean.setChannel_id(channelId);
	}
}
