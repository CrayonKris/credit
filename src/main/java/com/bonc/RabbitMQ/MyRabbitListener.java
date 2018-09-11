package com.bonc.RabbitMQ;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bonc.credit.mapper.CreditMapper;

/**
*
* @author zhijie.ma
* @date 2017年6月2日
* 
*/
@Component
public class MyRabbitListener {
	
	private final static Logger logger = LoggerFactory.getLogger(MyRabbitListener.class);
	
	@Autowired
	private CreditMapper creditMapper;
	
	/**
	 * RabbitMQ监听接口访问记录
	 * @param map
	 */
	@RabbitListener(queues = "addRecord")
	public void addMessage(Map<String,Object> map){
		logger.info("RabbitMQ队列取数据中addRecord。。。");
		try{
            creditMapper.addVisitRecord(map);
        }catch (Exception e){
            logger.info("插入访问记录数据异常" + e);
        }
		logger.info("RabbitMQ插入appKey:bonc的访问记录  >> " + map);
	}
	
	/**
	 * RabbitMQ监听接口访问次数
	 * @param map
	 */
	@RabbitListener(queues = "editRecord")
	public void editMessage(Map<String,Object> map){
		if(map.isEmpty() || map == null) return;
		logger.info("RabbitMQ队列取数据中editRecord。。。");
		Integer visitCount = creditMapper.selectVisitCount(map);
		map.put("access_count", visitCount - 1);
		creditMapper.updateVisitCount(map);
		logger.info("RabbitMQ更新用户使用次数记录："+map);
	}

	/**
	 * 添加总响应时长
	 * @param map
	 */
	@RabbitListener(queues = "addRecordTime")
	public void addRecordTime(Map<String,Object> map){
		if(map.isEmpty() || map == null) return;
		logger.info("RabbitMQ队列取数据中addRecordTime。。。");
		String timeType= (String) map.get("time_type");
		creditMapper.insertRecordTime(map);
		logger.info("" + timeType + "响应时长信息为  >>  " + map);
	}

}
