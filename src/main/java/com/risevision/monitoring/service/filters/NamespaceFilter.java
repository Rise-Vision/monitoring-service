package com.risevision.monitoring.service.filters;

import com.google.appengine.api.NamespaceManager;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by rodrigopavezi on 1/27/15.
 */
public class NamespaceFilter implements Filter {

    private final String MONITORING_NAMESPACE = "monitoring";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (NamespaceManager.get() == null) {
            NamespaceManager.set(MONITORING_NAMESPACE);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
