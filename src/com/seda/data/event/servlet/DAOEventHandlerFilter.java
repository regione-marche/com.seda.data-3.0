package com.seda.data.event.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class DAOEventHandlerFilter implements Filter {


	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		DAOEventContext eventContext = new DAOEventContext();
		try {
			DAOEventLocal.set(eventContext);
			chain.doFilter(request, response);
		} finally {
			DAOEventLocal.unset();
		}
		
	}

	public void init(FilterConfig config) throws ServletException {

	}

	public void destroy() {
		
	}

}
