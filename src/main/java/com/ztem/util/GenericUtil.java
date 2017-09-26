package com.ztem.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by LiMeiyuan on 2016/9/26 15:56.
 * description:
 */
public class GenericUtil {

    public static Type[] getGenericTypes(Class clazz) {
        ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
        if (type == null) {
            return null;
        }
        return type.getActualTypeArguments();
    }
}
