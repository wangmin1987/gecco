package com.isport.bean;

import java.util.List;
import java.util.Map;

public class SinaJsonBean {
private Result result;
private List<Map<String,Object>> data;



public Result getResult() {
	return result;
}

public void setResult(Result result) {
	this.result = result;
}

public List<Map<String, Object>> getData() {
	return data;
}

public void setData(List<Map<String, Object>> data) {
	this.data = data;
}


}
