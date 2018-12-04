
package com.isport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages= {"com.isport","com.geccocrawler"})
@EnableAsync
public class CrawlServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CrawlServiceApplication.class, args);
	}
}