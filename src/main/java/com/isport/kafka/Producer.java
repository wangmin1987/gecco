package com.isport.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void send(String key, String data) {
		kafkaTemplate.send("news", key, data);
	}

}
