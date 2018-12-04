package com.isport.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;

@Service
public class EsUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(EsUtils.class);
	@Autowired
    TransportClient transportClient;
	
	 @Value("${elasticsearch.suffix}")
	 private String esSuffix;
	
	public  void createMapping(){
		CreateIndexRequest request = new CreateIndexRequest(Constants.ISPORT_ES_INDEX.value+esSuffix);
		System.out.println(transportClient.admin().indices().create(request));
		XContentBuilder mapping = null;
		try {
			mapping = XContentFactory.jsonBuilder()
			        .startObject()
					.startObject("properties")
					.startObject("id")
					    .field("type","text")
					    .endObject()
					.startObject("title")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("title_img")
					    .field("type","text")
					    .endObject()
					.startObject("summary")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("content")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("pubDate")
					    .field("type","date")
					    .field("format","yyyy-MM-dd HH:mm")
					    .endObject()   
					.startObject("source")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject() 
					.startObject("source_icon")
					    .field("type","text")
					    .endObject() 
					.startObject("url")
					    .field("type","text")
					    .endObject()     
					.startObject("block")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()  					    
					.startObject("key_word")
					    .field("type","text")
					    .endObject()
					.startObject("comment_num")
					    .field("type","integer")
					    .endObject()
					.startObject("author")
					    .field("type","text")
					    .endObject()
					.startObject("author_img")
					    .field("type","text")
					    .endObject()    
					.startObject("tag")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject() 
				.endObject()
			.endObject();
			PutMappingRequest ma = Requests.putMappingRequest(Constants.ISPORT_ES_INDEX.value+esSuffix)
					.type(Constants.ISPORT_ES_TYPE.value)
					.source(mapping);
			transportClient.admin().indices().putMapping(ma).actionGet();
			System.out.println("mapping create success...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	Gson gson=new Gson();
	public boolean createIndex(String indexName,String typeName,String data) {
		IndexResponse response = transportClient.prepareIndex(indexName,typeName)
		        .setSource(data, XContentType.JSON)
		        .get();
		return response.getResult()==DocWriteResponse.Result.CREATED?true:false;
	}
	public boolean createIndex(NewsInfoBean data) {


		IndexResponse response = transportClient.prepareIndex(Constants.ISPORT_ES_INDEX.value+esSuffix,Constants.ISPORT_ES_TYPE.value,data.getId())
		        .setSource(gson.toJson(data), XContentType.JSON)
		        .get();
		return (response.getResult()==DocWriteResponse.Result.CREATED || response.getResult()==DocWriteResponse.Result.UPDATED) ? true:false;
	}
	public boolean batchCreateIndex(List<NewsInfoBean> datas) {
		try {
		BulkProcessor bulkProcessor=BulkProcessor.builder(transportClient, new BulkProcessor.Listener() {
			
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				
			}
		}).setBulkActions(1000)
	        .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
	        .setFlushInterval(TimeValue.timeValueSeconds(5))
	        .setConcurrentRequests(0)
	        .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
	        .build();
		for (NewsInfoBean data : datas) {
			bulkProcessor.add(new IndexRequest(Constants.ISPORT_ES_INDEX.value+esSuffix,Constants.ISPORT_ES_TYPE.value,data.getId())
					.source(transBean2Map(data)));
		}
		bulkProcessor.flush();
		bulkProcessor.close();
		}catch(Exception e) {
			LOGGER.error(StringUtils.getStackTrace(e));
			return false;
		}
		return true;
	}
	public boolean deleteIndex(String indexName,String typeName,String id) {
		DeleteResponse response = transportClient.prepareDelete(indexName,typeName,id)
		        .get();
		return response.getResult()==DocWriteResponse.Result.DELETED?true:false;
	}
	public boolean deleteIndex(String id) {
		DeleteResponse response = transportClient.prepareDelete(Constants.ISPORT_ES_INDEX.value+esSuffix,Constants.ISPORT_ES_TYPE.value,id)
		        .get();
		return response.getResult()==DocWriteResponse.Result.DELETED?true:false;
	}
	public boolean batchDeleteIndex(List<String> ids) {
		try {
		BulkProcessor bulkProcessor=BulkProcessor.builder(transportClient, new BulkProcessor.Listener() {
			
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				
			}
		}).setBulkActions(1000)
		        .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
		        .setFlushInterval(TimeValue.timeValueSeconds(5))
		        .setConcurrentRequests(0)
		        .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
		        .build();
		for (String id : ids) {
			bulkProcessor.add(new DeleteRequest(Constants.ISPORT_ES_INDEX.value+esSuffix,Constants.ISPORT_ES_TYPE.value, id));
		}
		bulkProcessor.close();
		}catch(Exception e) {
			LOGGER.error(StringUtils.getStackTrace(e));
			return false;
		}
		return true;
	}
	public boolean updateIndex(String indexName,String typeName,String id,String data) {
		UpdateResponse response = transportClient.prepareUpdate(indexName,typeName,id)
				.setDoc(data, XContentType.JSON)
		        .get();
		return response.getResult()==DocWriteResponse.Result.UPDATED?true:false;
	}
	public boolean updateIndex(String id,String data) {
		UpdateResponse response = transportClient.prepareUpdate(Constants.ISPORT_ES_INDEX.value+esSuffix,Constants.ISPORT_ES_TYPE.value,id)
				.setDoc(data, XContentType.JSON)
		        .get();
		return response.getResult()==DocWriteResponse.Result.UPDATED?true:false;
	}
	public String getDataById(String id) {
		GetResponse response = transportClient.prepareGet(Constants.ISPORT_ES_INDEX.value+esSuffix,Constants.ISPORT_ES_TYPE.value,id)
		        .get();
		return response.getSourceAsString();
	}
	public SearchResponse searchData(QueryBuilder qb,int pageNum,int pageSize) {
		SearchResponse searchResponse = transportClient.prepareSearch(Constants.ISPORT_ES_INDEX.value+esSuffix)
				.setTypes(Constants.ISPORT_ES_TYPE.value)
				.setQuery(qb).setFrom((pageNum-1)*pageSize).setSize(pageSize)
				.get();
		return searchResponse;
	}
	public static Map<String, Object> transBean2Map(Object obj) {

        if(obj == null){
            return null;
        }        
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }
        return map;
    }
}
