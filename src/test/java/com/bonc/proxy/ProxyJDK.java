package com.bonc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
*	事务处理器
* @author zhijie.ma
* @date 2017年5月11日
* 
*/
public class ProxyJDK implements InvocationHandler{

	private Object target;
	
	public ProxyJDK(Object target) {
		super();
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		System.out.println("jdk代理   -- 先锋军");
		
		method.invoke(target, args);
		
		System.out.println("jdk代理  -- 后勤部队");
		
		return null;
	}
	
	
	
}
