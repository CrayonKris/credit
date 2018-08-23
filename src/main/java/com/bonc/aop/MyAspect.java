package com.bonc.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bonc.util.ProjectErrorInformation;
import com.bonc.util.SendMailUtils;

/**
 * spring AOP
 * @author zhijie.ma
 * @date 2017年7月20日
 *
 */
@Aspect
@Component
public class MyAspect {

	private static final Logger logger = LoggerFactory.getLogger(MyAspect.class); 
	
//	@SuppressWarnings("unchecked")
//	@Around("execution(* com.bonc.credit.*.*.*.*(..)) or execution(* com.bonc.util.*.*(..)) or execution(* com.bonc.credit.*.*.*(..))")
	@Around("execution(* com.bonc.credit.controller.*.*(..)) or execution(* com.bonc.util.HttpRequest.*(..))")
	public String interceptor(ProceedingJoinPoint pjp){  
		Object obj = null;
		try {
			obj = pjp.proceed();
		} catch (Exception e) {
//			return new JsonResult<>(e); 
			logger.info("【系统出现异常】"+e.toString());
//			e.printStackTrace();
			SendMailUtils.sendEmailConfigure(e.toString());
			return ProjectErrorInformation.businessError1("");
		} catch (Throwable e) {
//			return new JsonResult<>(e); 
			logger.info("【系统出现严重的异常】"+e.getMessage());
			SendMailUtils.sendEmailConfigure(e.toString());
			return ProjectErrorInformation.businessError1("");
		}
		return obj.toString();
	}
	
}
