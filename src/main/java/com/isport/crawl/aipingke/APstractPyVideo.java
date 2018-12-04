package com.isport.crawl.aipingke;

import java.util.Date;

import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.selenium.AbstractDriver;
import com.isport.utils.DateUtils;
import com.isport.utils.StringUtils;
/**
 *       乒羽抽象类
 * @author 八斗体育
 *
 */
public abstract class APstractPyVideo extends APbstractDriver {
	/**
	 * 设置默认发布时间
	 * 
	 * @param newsInfoBean
	 */
	protected void setDefaultPubDate(NewsInfoBean newsInfoBean) {
		if (StringUtils.isNUll(newsInfoBean.getPub_date())) {
			newsInfoBean.setPub_date(DateUtils.getStrYYYYMMDDHHmmss(new Date()));
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

}
