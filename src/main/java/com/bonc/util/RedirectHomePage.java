package com.bonc.util;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
*	本类可以设置默认跳转的首页
* @author zhijie.ma
* @date 2017年8月11日
* 
*/
//@Configuration
public class RedirectHomePage extends WebMvcConfigurerAdapter{
	
	@Override
    public void addViewControllers( ViewControllerRegistry registry ) {
//        registry.addViewController( "/" ).setViewName( "forward:/index.html" );
		
		registry.addViewController( "/" ).setViewName( "redirect:https://www.baidu.com" );
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
        super.addViewControllers( registry );
    } 
	
	
	
	/*@Configuration
	public class MyAdapter extends WebMvcConfigurerAdapter{
	    @Override
	    public void addViewControllers( ViewControllerRegistry registry ) {
	        registry.addViewController( "/" ).setViewName( "forward:/index.html" );
	        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
	        super.addViewControllers( registry );
	    } 
	}*/
	
}
