package com.ztem.util;

import org.springframework.util.Assert;

import javax.servlet.ServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by LiMeiyuan on 2017/1/13.
 * Description:
 */
public class ServletUtils {
    /**
     * 取得带相同前缀的Request Parameters. 返回的结果的Parameter名已去除前缀.
     *
     * @param request HttpServletRequest
     * @param prefix  String
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getParametersStartingWith(
            ServletRequest request, String prefix) {
        Assert.notNull(request, "Request must not be null");

        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();

        String thePrefix = (prefix == null) ? "" : prefix;

        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();

            if ("".equals(thePrefix) || paramName.startsWith(thePrefix)) {
                String unprefixed = paramName.substring(thePrefix.length());
                String[] values = request.getParameterValues(paramName);

                if ((values == null) || (values.length == 0)) {
                    // Do nothing, no values found at all.
                    continue;
                }

                if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }

        return params;
    }
}
