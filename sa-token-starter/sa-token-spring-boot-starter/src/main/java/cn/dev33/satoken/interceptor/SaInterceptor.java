package cn.dev33.satoken.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.strategy.SaStrategy;

/**
 * Sa-Token 综合拦截器，提供注解鉴权和路由拦截鉴权能力 
 * 
 * @author click33
 * @since: 2022-8-21
 */
public class SaInterceptor implements HandlerInterceptor {

	/**
	 * 是否打开注解鉴权 
	 */
	public boolean isAnnotation = true;
	
	/**
	 * 认证函数：每次请求执行 
	 * <p> 参数：路由处理函数指针 
	 */
	public SaParamFunction<Object> auth = handler -> {};

	/**
	 * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力 
	 */
	public SaInterceptor() {
	}

	/**
	 * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力 
	 * @param auth 认证函数，每次请求执行 
	 */
	public SaInterceptor(SaParamFunction<Object> auth) {
		this.auth = auth;
	}

	/**
	 * 设置是否打开注解鉴权 
	 * @param isAnnotation /
	 * @return 对象自身 
	 */
	public SaInterceptor isAnnotation(boolean isAnnotation) {
		this.isAnnotation = isAnnotation;
		return this;
	}
	
	/**
	 * 写入[认证函数]: 每次请求执行 
	 * @param auth / 
	 * @return 对象自身 
	 */
	public SaInterceptor setAuth(SaParamFunction<Object> auth) {
		this.auth = auth;
		return this;
	}
	
	
	// ----------------- 验证方法 ----------------- 

	/**
	 * 每次请求之前触发的方法 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		try {

			if(isAnnotation && handler instanceof HandlerMethod) {
				
				// 获取此请求对应的 Method 处理函数 
				Method method = ((HandlerMethod) handler).getMethod();

				// 如果此 Method 或其所属 Class 标注了 @SaIgnore，则忽略掉鉴权 
				if(SaStrategy.me.isAnnotationPresent.apply(method, SaIgnore.class)) {
					return true;
				}

				// 注解校验 
				SaStrategy.me.checkMethodAnnotation.accept(method);
			}
			
			// Auth 校验  
			auth.run(handler);
			
		} catch (StopMatchException e) {
			// 停止匹配，进入Controller 
		} catch (BackResultException e) {
			// 停止匹配，向前端输出结果 
			if(response.getContentType() == null) {
				response.setContentType("text/plain; charset=utf-8"); 
			}
			response.getWriter().print(e.getMessage());
			return false;
		}
		
		// 通过验证 
		return true;
	}

}
