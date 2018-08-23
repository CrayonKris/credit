package com.bonc.RabbitMQ;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
*
* @author zhijie.ma
* @date 2017年6月2日
* 
*/
@Configuration
public class RabbitMQConfigration {
	
	@Bean
	public Queue addQueue(){
		return new Queue("addRecord");
	}
	
	@Bean
	public Queue editQueue(){
		return new Queue("editRecord");
	}

	@Bean
	public Queue addRecordTime(){
		return new Queue("addRecordTime");
	}

}
