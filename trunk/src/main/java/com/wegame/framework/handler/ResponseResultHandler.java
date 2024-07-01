package com.wegame.framework.handler;

import com.wegame.framework.core.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static com.wegame.framework.handler.ResponseResultInterceptor.RESPONSE_RESULT_ANN;


@RestControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {


    @Override
    public boolean supports(MethodParameter methodParameter, Class clazz) {
        // 判断是否有包装标识，并返回结果，如果方法结果为true则执行beforeBodyWrite
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();

        return request.getAttribute(RESPONSE_RESULT_ANN) != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 包装结果
        if (body instanceof Result) {
            return body;
        }

        return Result.success(body);
    }
}
