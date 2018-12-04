package com.isport.crawl.tengxun.esports;

import java.lang.reflect.Field;

import com.geccocrawler.gecco.annotation.FieldRenderName;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.response.HttpResponse;
import com.geccocrawler.gecco.spider.SpiderBean;
import com.geccocrawler.gecco.spider.render.CustomFieldRender;

import net.sf.cglib.beans.BeanMap;

@FieldRenderName("txBJHRender")
public class TxRender implements CustomFieldRender {

	@Override
	public void render(HttpRequest request, HttpResponse response, BeanMap beanMap, SpiderBean bean, Field field) {
		//获取网页原始内容
		String content =response.getContent();
		// 进内容注入到对应的field中
		beanMap.put(field.getName(), content);
	}

}
