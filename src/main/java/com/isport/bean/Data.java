package com.isport.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Data {
	private String similar_result; // 查重结果 true-成功（无重复） false-失败（有重复）
	private String similar_id; // 已存在的资讯ID

	public String getSimilar_result() {
		return similar_result;
	}

	public void setSimilar_result(String similar_result) {
		this.similar_result = similar_result;
	}

	public String getSimilar_id() {
		return similar_id;
	}

	public void setSimilar_id(String similar_id) {
		this.similar_id = similar_id;
	}

}
