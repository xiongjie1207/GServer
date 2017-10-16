package com.gserver.plugins.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gserver.core.Commanders;
import com.gserver.core.Packet;
import com.gserver.plugins.IPlugin;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Copyright (c) 2015-2016, James Xiong 熊杰 (xiongjie.cn@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by xiongjie on 2017/1/11.
 */

/**
 * 基于http的数据传输
 * 数据流发送格式{"pid":1,"name":"guest","password":"111111","id":1,"clientType":0 }
 * 数据流接收格式{"pid":1,"name":"guest","password":"111111","id":1,"clientType":0 }
 */

public abstract class PluginWebController implements IPlugin {
    private static final String CHAR_SET_UTF_8 = "utf-8";
    private ThreadLocal<Map<String, Object>> tl = new ThreadLocal<Map<String, Object>>();
    private static final String HTTP_SERVLET_REQUEST = "HTTP_SERVLET_REQUEST";
    private static final String HTTP_SERVLET_RESPONSE = "HTTP_SERVLET_RESPONSE";
    private boolean isRunning = false;
    private Logger logger = Logger.getLogger(this.getClass());


    @Override
    public boolean start() {
        isRunning = true;
        return true;
    }

    @Override
    public boolean stop() {
        isRunning = false;
        return true;
    }

    @ModelAttribute
    private void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding(CHAR_SET_UTF_8);
            response.setCharacterEncoding(CHAR_SET_UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.error("setReqAndRes", e);
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(HTTP_SERVLET_REQUEST, request);
        values.put(HTTP_SERVLET_RESPONSE, response);
        tl.set(values);

    }

    protected final void handle() {
        try {
            if (isRunning) {
                String dataStr = getRequest().getReader().readLine();
                if (dataStr == null) {
                    return;
                }
                logger.info("receive data:" + dataStr);
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> msg = objectMapper.readValue(dataStr, Map.class);
                Packet packet = new Packet(msg);
                Commanders.getInstance().dispatch(packet, getRequest(), getResponse());
            }
        } catch (IOException e) {
            logger.error("", e);
        }
    }


    protected HttpServletResponse getResponse() {
        return (HttpServletResponse) tl.get().get(HTTP_SERVLET_RESPONSE);
    }

    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) tl.get().get(HTTP_SERVLET_REQUEST);
    }

    public PluginWebController setAttr(String name, Object value) {
        getRequest().setAttribute(name, value);
        return this;
    }

    public PluginWebController removeAttr(String name) {
        getRequest().removeAttribute(name);
        return this;
    }

    public PluginWebController setAttrs(Map<String, Object> attrMap) {
        for (Map.Entry<String, Object> entry : attrMap.entrySet())
            getRequest().setAttribute(entry.getKey(), entry.getValue());
        return this;
    }


    public String getPara(String name) {
        return getRequest().getParameter(name);
    }


    public String getPara(String name, String defaultValue) {
        String result = getRequest().getParameter(name);
        return result != null && !"".equals(result) ? result : defaultValue;
    }


    public Map<String, String[]> getParaMap() {
        return getRequest().getParameterMap();
    }

    public Enumeration<String> getParaNames() {
        return getRequest().getParameterNames();
    }

    public String[] getParaValues(String name) {
        return getRequest().getParameterValues(name);
    }

    public Integer[] getParaValuesToInt(String name) {
        String[] values = getRequest().getParameterValues(name);
        if (values == null)
            return null;
        Integer[] result = new Integer[values.length];
        for (int i = 0; i < result.length; i++)
            result[i] = Integer.parseInt(values[i]);
        return result;
    }

    public Long[] getParaValuesToLong(String name) {
        String[] values = getRequest().getParameterValues(name);
        if (values == null)
            return null;
        Long[] result = new Long[values.length];
        for (int i = 0; i < result.length; i++)
            result[i] = Long.parseLong(values[i]);
        return result;
    }


    public Enumeration<String> getAttrNames() {
        return getRequest().getAttributeNames();
    }

    public <T> T getAttr(String name) {
        return (T) getRequest().getAttribute(name);
    }


    public String getAttrForStr(String name) {
        return (String) getRequest().getAttribute(name);
    }


    public Integer getAttrForInt(String name) {
        return (Integer) getRequest().getAttribute(name);
    }

    private Integer toInt(String value, Integer defaultValue) {
        try {
            if (value == null || "".equals(value.trim()))
                return defaultValue;
            value = value.trim();
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw null;
        }
    }


    public Integer getParaToInt(String name) {
        return toInt(getRequest().getParameter(name), null);
    }


    public Integer getParaToInt(String name, Integer defaultValue) {
        return toInt(getRequest().getParameter(name), defaultValue);
    }

    private Long toLong(String value, Long defaultValue) {
        try {
            if (value == null || "".equals(value.trim()))
                return defaultValue;
            value = value.trim();
            return Long.parseLong(value);
        } catch (Exception e) {
            throw e;
        }
    }


    public Long getParaToLong(String name) {
        return toLong(getRequest().getParameter(name), null);
    }


    public Long getParaToLong(String name, Long defaultValue) {
        return toLong(getRequest().getParameter(name), defaultValue);
    }

    private Boolean toBoolean(String value, Boolean defaultValue) {
        if (value == null || "".equals(value.trim()))
            return defaultValue;
        value = value.trim().toLowerCase();
        if ("1".equals(value) || "true".equals(value))
            return Boolean.TRUE;
        else if ("0".equals(value) || "false".equals(value))
            return Boolean.FALSE;
        return Boolean.FALSE;
    }

    public Boolean getParaToBoolean(String name) {
        return toBoolean(getRequest().getParameter(name), null);
    }


    public Boolean getParaToBoolean(String name, Boolean defaultValue) {
        return toBoolean(getRequest().getParameter(name), defaultValue);
    }


    private Date toDate(String value, Date defaultValue) {
        try {
            if (value == null || "".equals(value.trim()))
                return defaultValue;
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public Date getParaToDate(String name) {
        return toDate(getRequest().getParameter(name), null);
    }


    public Date getParaToDate(String name, Date defaultValue) {
        return toDate(getRequest().getParameter(name), defaultValue);
    }


    public HttpSession getSession() {
        return getRequest().getSession();
    }


    public HttpSession getSession(boolean create) {
        return getRequest().getSession(create);
    }


    public <T> T getSessionAttr(String key) {
        HttpSession session = getRequest().getSession(false);
        return session != null ? (T) session.getAttribute(key) : null;
    }


    public PluginWebController setSessionAttr(String key, Object value) {
        getRequest().getSession(true).setAttribute(key, value);
        return this;
    }


    public PluginWebController removeSessionAttr(String key) {
        HttpSession session = getRequest().getSession(false);
        if (session != null)
            session.removeAttribute(key);
        return this;
    }


    public String getCookie(String name, String defaultValue) {
        Cookie cookie = getCookieObject(name);
        return cookie != null ? cookie.getValue() : defaultValue;
    }

    public String getCookie(String name) {
        return getCookie(name, null);
    }


    public Integer getCookieToInt(String name) {
        String result = getCookie(name);
        return result != null ? Integer.parseInt(result) : null;
    }

    public Integer getCookieToInt(String name, Integer defaultValue) {
        String result = getCookie(name);
        return result != null ? Integer.parseInt(result) : defaultValue;
    }


    public Long getCookieToLong(String name) {
        String result = getCookie(name);
        return result != null ? Long.parseLong(result) : null;
    }


    public Long getCookieToLong(String name, Long defaultValue) {
        String result = getCookie(name);
        return result != null ? Long.parseLong(result) : defaultValue;
    }


    public Cookie getCookieObject(String name) {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies)
                if (cookie.getName().equals(name))
                    return cookie;
        return null;
    }


    public Cookie[] getCookieObjects() {
        Cookie[] result = getRequest().getCookies();
        return result != null ? result : new Cookie[0];
    }

    public PluginWebController setCookie(String name, String value, int maxAgeInSeconds, boolean isHttpOnly) {
        return doSetCookie(name, value, maxAgeInSeconds, null, null, isHttpOnly);
    }


    public PluginWebController setCookie(String name, String value, int maxAgeInSeconds) {
        return doSetCookie(name, value, maxAgeInSeconds, null, null, null);
    }


    public PluginWebController setCookie(Cookie cookie) {
        getResponse().addCookie(cookie);
        return this;
    }


    public PluginWebController setCookie(String name, String value, int maxAgeInSeconds, String path, boolean isHttpOnly) {
        return doSetCookie(name, value, maxAgeInSeconds, path, null, isHttpOnly);
    }


    public PluginWebController setCookie(String name, String value, int maxAgeInSeconds, String path) {
        return doSetCookie(name, value, maxAgeInSeconds, path, null, null);
    }


    public PluginWebController setCookie(String name, String value, int maxAgeInSeconds, String path, String domain, boolean isHttpOnly) {
        return doSetCookie(name, value, maxAgeInSeconds, path, domain, isHttpOnly);
    }


    public PluginWebController removeCookie(String name) {
        return doSetCookie(name, null, 0, null, null, null);
    }


    public PluginWebController removeCookie(String name, String path) {
        return doSetCookie(name, null, 0, path, null, null);
    }

    public PluginWebController removeCookie(String name, String path, String domain) {
        return doSetCookie(name, null, 0, path, domain, null);
    }

    private PluginWebController doSetCookie(String name, String value, int maxAgeInSeconds, String path, String domain, Boolean isHttpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInSeconds);
        // set the default path value to "/"
        if (path == null) {
            path = "/";
        }
        cookie.setPath(path);

        if (domain != null) {
            cookie.setDomain(domain);
        }
        if (isHttpOnly != null) {
            cookie.setHttpOnly(isHttpOnly);
        }
        getResponse().addCookie(cookie);
        return this;
    }
}