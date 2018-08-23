package com.bonc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestProxy {
	
	/**
	 * 测试静态代理
	 */
	@Test
	public void testProperties(){
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		ProxyUser proxyUser = new ProxyUser(userServiceImpl);
		proxyUser.getUser();
	}
	
	/**
	 * 测试jdk代理     并非动态
	 */
	@Test
	public void testJDKProxy(){
		
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		InvocationHandler proxyJDK = new ProxyJDK(userServiceImpl);
		
		Class<? extends UserServiceImpl> userClass = userServiceImpl.getClass();
		
		//getClassLoader()  类加载器，存储信息               getInterfaces  获得此类的所有接口
		UserService u = (UserService) Proxy.newProxyInstance(userClass.getClassLoader(), userClass.getInterfaces(), proxyJDK);
		Class<?>[] interfaces = userClass.getInterfaces();
		for (Class<?> class1 : interfaces) {
			System.out.println(class1);
		}
		
		
		u.getUser();
		
		System.out.println("88888888888");
	}

}


