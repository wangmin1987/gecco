package com.isport.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig{
//    @Value("${spring.redis.host}")
//    private String host;
//    @Value("${spring.redis.port}")
//    private int port;
//    @Value("${spring.redis.timeout}")
//    private int timeout;
//    @Bean
//    public KeyGenerator wiselyKeyGenerator(){
//        return new KeyGenerator() {
//            @Override
//            public Object generate(Object target, Method method, Object... params) {
//                StringBuilder sb = new StringBuilder();
//                sb.append(target.getClass().getName());
//                sb.append(method.getName());
//                for (Object obj : params) {
//                    sb.append(obj.toString());
//                }
//                return sb.toString();
//            }
//        };
//    }
//    @Bean
//    public JedisConnectionFactory redisConnectionFactory() {
//        JedisConnectionFactory factory = new JedisConnectionFactory();
//        factory.setHostName(host);
//        factory.setPort(port);
//        factory.setTimeout(timeout); //设置连接超时时间
//        return factory;
//    }
////    @Bean
////    public CacheManager cacheManager(RedisTemplate redisTemplate) {
////        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
////        // Number of seconds before expiration. Defaults to unlimited (0)
////        cacheManager.setDefaultExpiration(10); //设置key-value超时时间
////        return cacheManager;
////    }
	@Autowired
	RedisTemplate<String, String> template;
    @Bean("strRedisTemplate")
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
//    private void setSerializer(RedisTemplate template) {
////    	template.setKeySerializer(StringRedisSerializer.class);
////        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
////        ObjectMapper om = new ObjectMapper();
////        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
////        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
////        jackson2JsonRedisSerializer.setObjectMapper(om);
////        template.setValueSerializer(jackson2JsonRedisSerializer);
//    }


}