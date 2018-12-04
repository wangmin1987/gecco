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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;

@Service
public class EsInit {
	private static final Logger LOGGER = LoggerFactory.getLogger(EsInit.class);
	@Autowired
    TransportClient transportClient;
	
	private String indexName="badousport_information";
	private String indexType="information";
	
	public  void createInfoMapping(){
		CreateIndexRequest request = new CreateIndexRequest(indexName);
		System.out.println(transportClient.admin().indices().create(request));
		XContentBuilder mapping = null;
		try {
			mapping = XContentFactory.jsonBuilder()
			        .startObject()
					.startObject("properties")
					.startObject("id")
					    .field("type","keyword")
					    .endObject()
					.startObject("title")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
//					.startObject("title_img")
//					    .field("type","object")
//					    .endObject()
					.startObject("title_imgs")
					.field("type","text")
					//.startArray("title_imgs")
					//    .endArray()
					  .endObject()
					.startObject("author_id")
					    .field("type","keyword")
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
					.startObject("url")
					    .field("type","keyword")
					    .endObject()
					.startObject("pub_date")
					    .field("type","date")
					    .field("format","yyyy-MM-dd HH:mm")
					    .endObject()   
					.startObject("tag")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("keyword")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("source")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject() 
					.startObject("source_icon")
					    .field("type","keyword")
					    .endObject()
					.startObject("data_type")
					    .field("type","byte")
					    .endObject() 
					.startObject("release_status")
					    .field("type","byte")
					    .endObject() 
				.endObject()
			.endObject();
			PutMappingRequest ma = Requests.putMappingRequest(indexName)
					.type(indexType)
					.source(mapping);
			transportClient.admin().indices().putMapping(ma).actionGet();
			System.out.println("mapping create success...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public  void createInfostatisticsMapping(){
		CreateIndexRequest request = new CreateIndexRequest("information_statistics");
		System.out.println(transportClient.admin().indices().create(request));
		XContentBuilder mapping = null;
		try {
			mapping = XContentFactory.jsonBuilder()
			        .startObject()
					.startObject("properties")
					.startObject("id")
					    .field("type","keyword")
					    .endObject()
					.startObject("favorites_count")
					    .field("type","integer")
					    .endObject()
					.startObject("forward_count")
				    .field("type","integer")
				    .endObject()
					.startObject("comment_count")
				    .field("type","integer")
				    .endObject()
					.startObject("browse_count")
				    .field("type","integer")
				    .endObject()
					.startObject("hot_value")
				    .field("type","scaled_float")
				    .field("scaling_factor","1000")
				    .endObject()
					.startObject("article_quality")
				    .field("type","scaled_float")
				    .field("scaling_factor","1000")
				    .endObject()
					.startObject("aging")
				    .field("type","short")
				    .endObject()
					.startObject("statistics_date")
				    .field("type","date")
				    .field("format","yyyy-MM-dd HH:mm:ss")
				    .endObject()
					.startObject("labels")
					.startObject("properties")
					
					.startObject("label")
				    .field("type","text")
				    .field("analyzer","ik_max_word")
				    .field("search_analyzer","ik_max_word")
				    .endObject() 
					
					.startObject("label_level")
				    .field("type","short")
				    .endObject() 
					
					.startObject("weight")
				    .field("type","scaled_float")
				    .field("scaling_factor","1000")
				    .endObject() 
				   
					.endObject() 
				    .endObject()
					.startObject("features")
					.field("type","text")
					.endObject()
					.startObject("pub_date")
				    .field("type","date")
				    .field("format","yyyy-MM-dd HH:mm")
				    .endObject()
					.startObject("channel_id")
				    .field("type","keyword")
				    .endObject()
				.endObject()
			.endObject();
			PutMappingRequest ma = Requests.putMappingRequest("information_statistics")
					.type("statistics")
					.source(mapping);
			transportClient.admin().indices().putMapping(ma).actionGet();
			System.out.println("mapping create success...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public  void createCommunityMapping(){
		CreateIndexRequest request = new CreateIndexRequest("badousport_community");
		System.out.println(transportClient.admin().indices().create(request));
		XContentBuilder mapping = null;
		try {
			mapping = XContentFactory.jsonBuilder()
			        .startObject()
					.startObject("properties")
					.startObject("id")
					    .field("type","keyword")
					    .endObject()
					.startObject("community_name")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("introduction")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("community_label_id")
					    .field("type","text")
					    .endObject()
					.startObject("community_classify_id")
					    .field("type","text")
					    .endObject()
					.startObject("sort_val")
					    .field("type","byte")
					    .endObject() 
					.startObject("is_valid")
					    .field("type","byte")
					    .endObject() 
				.endObject()
			.endObject();
			PutMappingRequest ma = Requests.putMappingRequest("badousport_community")
					.type("community")
					.source(mapping);
			transportClient.admin().indices().putMapping(ma).actionGet();
			System.out.println("mapping create success...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public  void createUserMapping(){
		CreateIndexRequest request = new CreateIndexRequest("badousport_user");
		System.out.println(transportClient.admin().indices().create(request));
		XContentBuilder mapping = null;
		try {
			mapping = XContentFactory.jsonBuilder()
			        .startObject()
					.startObject("properties")
					.startObject("id")
					    .field("type","keyword")
					    .endObject()
					.startObject("nick_name")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("head_img")
					    .field("type","keyword")
					    .endObject()
					.startObject("introduction")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("user_category")
					    .field("type","text")
					    .field("analyzer","ik_max_word")
					    .field("search_analyzer","ik_max_word")
					    .endObject()
					.startObject("user_type")
					    .field("type","byte")
					    .endObject() 
					.startObject("is_valid")
					    .field("type","byte")
					    .endObject() 
				.endObject()
			.endObject();
			PutMappingRequest ma = Requests.putMappingRequest("badousport_user")
					.type("userinfo")
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


		IndexResponse response = transportClient.prepareIndex(indexName,indexType,data.getId())
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
			bulkProcessor.add(new IndexRequest(indexName,indexType,data.getId())
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
		DeleteResponse response = transportClient.prepareDelete(indexName,indexType,id)
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
			bulkProcessor.add(new DeleteRequest(indexName,indexType, id));
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
		UpdateResponse response = transportClient.prepareUpdate(indexName,indexType,id)
				.setDoc(data, XContentType.JSON)
		        .get();
		return response.getResult()==DocWriteResponse.Result.UPDATED?true:false;
	}
	public String getDataById(String id) {
		GetResponse response = transportClient.prepareGet(indexName,indexType,id)
		        .get();
		return response.getSourceAsString();
	}
	public SearchResponse searchData(QueryBuilder qb,int pageNum,int pageSize) {
		SearchResponse searchResponse = transportClient.prepareSearch(indexName)
				.setTypes(indexType)
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
	public void deleteAll(String ine) {
		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClient)
				//.filter(QueryBuilders.boolQuery().mustNot(QueryBuilders.queryStringQuery("新浪微博").field("source"))) 
		    .filter(QueryBuilders.matchAllQuery()) 
		    .source(ine)                                  
		    .get();                                             
		   response.getDeleted();   
	}
}
