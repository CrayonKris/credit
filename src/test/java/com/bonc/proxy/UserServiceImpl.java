package com.bonc.proxy;

import java.io.Serializable;

/**
*
* @author zhijie.ma
* @date 2017年5月11日
* 
*/
public class UserServiceImpl implements UserService,Serializable{

	@Override
	public void getUser() {
			
		System.out.println("我是主类");
		
	}
	
}
