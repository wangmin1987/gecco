package com.isport.crawl.xiguaji;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;

@Service
public class XiguajiDetail extends AbstractDetailPipeLine {

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 设置标题
		String title = jo.getString("title");
		newsInfoBean.setTitle(title);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_XIGUAJI.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_XIGUAJI.value);
	}

	@Override
	protected String getBodyExpession() {
		return "#js_content";
	}

	@Override
	protected void parseCleanData(Element element) {
		Elements iframes = element.select("iframe");
		if (iframes.size() > 0) {
			for (Element iframe : iframes) {
				Element parent = getLastParent(iframe, element);
				parent.remove();
			}
		}
	}

	private Element getLastParent(Element el, Element element) {
		Element parent = el.parent();
		if (parent == null || parent.equals(element)) {
			return el;
		}
		return getLastParent(parent, element);
	}
}
