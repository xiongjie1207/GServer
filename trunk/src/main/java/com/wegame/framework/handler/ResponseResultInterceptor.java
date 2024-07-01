package com.wegame.framework.handler;

import com.wegame.framework.aop.annotation.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class ResponseResultInterceptor implements HandlerInterceptor {

    public static final String RESPONSE_RESULT_ANN = "RESPONSE_RESULT_ANN";
    // 用于缓存被ResponseResult注解标识的方法
    static Map<String, Boolean> cache = new HashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 请求的方法
        if (handler instanceof HandlerMethod handlerMethod) {

            // 判断方法是否已缓存，若缓存则直接添加包装标识
            Boolean cacheResult = cache.get(handlerMethod.toString());
            if (cacheResult != null) {
                if (cacheResult) {
                    request.setAttribute(RESPONSE_RESULT_ANN, true);
                }
            } else {
                // 通过反射拿到方法
                final Class<?> clazz = handlerMethod.getBeanType();
                final Method method = handlerMethod.getMethod();

                if (clazz.isAnnotationPresent(ResponseResult.class)) {
                    // 判断类上是否有ResponseResult注解
                    // 若存在ResponseResult注解，添加进缓存并且设置包装标识至请求
                    cache.put(handlerMethod.toString(), true);
                    request.setAttribute(RESPONSE_RESULT_ANN, true);
                } else if (method.isAnnotationPresent(ResponseResult.class)) {
                    // 判断方法上是否有ResponseResult注解
                    // 若存在ResponseResult注解，添加进缓存并且设置包装标识至请求
                    cache.put(handlerMethod.toString(), true);
                    request.setAttribute(RESPONSE_RESULT_ANN, true);
                } else {
                    cache.put(handlerMethod.toString(), false);
                }
            }
        }

        return true;
    }
}
