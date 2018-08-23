package com.bonc;

import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.liantong.LiantongServiceByZyx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BoncCreditWorldApplicationTestss {
	
	@Autowired
	LiantongServiceByZyx liantongServiceByZyx;

	@Test
	public void contextLoads() {
		JSONObject bizParams=new JSONObject();
		bizParams.put("mobile","18562310310");
		bizParams.put("certCode","371725199611080013");
		String result=liantongServiceByZyx.getMobileCard(bizParams);
		System.out.println(result);

	}

}
