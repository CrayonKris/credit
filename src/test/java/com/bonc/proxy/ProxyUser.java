package com.bonc.proxy;

/**
*
* @author zhijie.ma
* @date 2017年5月11日
* 
*/
public class ProxyUser implements UserService{

	private UserServiceImpl impl;
	
	
	
	public ProxyUser(UserServiceImpl impl) {
		super();
		this.impl = impl;
	}



	@Override
	public void getUser() {
		
		System.out.println("代理类前面");
		
		impl.getUser();
		
		System.out.println("代理类后面");
	}

}
