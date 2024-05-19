package org.mq.optculture.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ExecuteTimeInterceptor extends HandlerInterceptorAdapter{

	private static final Logger logger = LogManager.getLogger("performance");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		//logger.debug("preHandler invoked");
		//logger.info("JSON Request: = "+OptCultureUtils.getParameterJsonValue(request));
		//logger.debug("json request :"+request.getReader().lines().collect(Collectors.joining()));
		//logger.info("JSON Request using gson: = "+new Gson().toJson(request));

		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		long startTime = (Long)request.getAttribute("startTime");
		long endTime = System.currentTimeMillis();
		//logger.debug("endTime="+endTime);
		long executeTime = endTime - startTime;

		//ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
		//logger.info(responseWrapper.getContentAsByteArray());
		logger.debug(request.getRemoteAddr()+" "+request.getRequestURI() + " " + executeTime + " ms" + " [" + handler + "]" );

	}

}
