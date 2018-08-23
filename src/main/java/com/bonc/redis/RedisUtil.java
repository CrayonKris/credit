package com.bonc.redis;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
* redis 的增删改查
* @author zhijie.ma
* @date 2017年5月5日
* 
*/
@Repository
public class RedisUtil {
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Resource(name="stringRedisTemplate")
	ValueOperations<String, String> valOpsStr;
	
	@Autowired
	RedisTemplate<Object, Object> redisTemplate;
	
	@Resource(name="redisTemplate")
	ValueOperations<Object, Object> valOps;
	
	@Autowired
	JedisPool jedisPool;
	
	/**
	 * 存储字符串类型的数据
	 * @param key
	 * @param value
	 */
	public void setString(String key,String value) {
		valOpsStr.set(key, value);
	}
	
	/**
	 * 获取字符串类型的数据
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		String value = valOpsStr.get(key);
		return value;
		
	}
	
	/**
	 * 删除Object类型的数据
	 * @param key
	 */
	public void deleteObject(String key) {
		redisTemplate.delete(key);
	}
	
	/**
	 * 存储Object类型的数据
	 * @param key
	 * @param value
	 */
	public void setObject(Object key,Object value) {
		valOps.set(key, value);
	}
	
	/**
	 * 获取Object类型的数据
	 * @param key
	 * @return
	 */
	public Object getObject(Object key) {
		Object object = valOps.get(key);
		return object;
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param time 过期时间     单位：秒
	 */
	public void setStringTime(String key,String value,int time) {
		//从redis连接池中获取一个连接
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			
			jedis.setex(key, time, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			jedis.close();
		}
		
	}
	
	/**
	 * 此方法可以判断redis中key的value是否存在      存在为true   不存在为false
	 * @param key
	 * @param ip
	 * @return
	 */
	public boolean aa(String key,String ip){
		Jedis jedis = null;
		boolean a = true;
		try {
			jedis = jedisPool.getResource();
			
            a = jedis.sismember(key, ip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			jedis.close();
		}
		return a;
	}
}
