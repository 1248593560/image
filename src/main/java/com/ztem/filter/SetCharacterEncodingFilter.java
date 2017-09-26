package com.ztem.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by SunYingLu on 2017/09/26.
 */
public class SetCharacterEncodingFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }
        filterChain.doFilter(request, response);
    }

    public void destroy() {

    }
}
