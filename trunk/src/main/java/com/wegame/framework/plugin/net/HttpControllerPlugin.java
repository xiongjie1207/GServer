package com.wegame.framework.plugin.net;

import com.wegame.framework.core.GameCons;
import com.wegame.framework.core.GameEventLoop;
import com.wegame.framework.packet.IPacket;
import com.wegame.framework.packet.Packet;
import com.wegame.framework.plugin.IPlugin;
import com.wegame.util.AppStatus;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

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
public abstract class HttpControllerPlugin implements IPlugin {
    private static final String CHAR_SET_UTF_8 = "utf-8";
    private static final String HTTP_SERVLET_REQUEST = "HTTP_SERVLET_REQUEST";
    private static final String HTTP_SERVLET_RESPONSE = "HTTP_SERVLET_RESPONSE";
    private final ThreadLocal<Map<String, Object>> tl = new ThreadLocal<Map<String, Object>>();

    @Override
    public boolean start() {
        AppStatus.Status = AppStatus.Running;
        return true;
    }

    @Override
    public boolean stop() {
        AppStatus.Status = AppStatus.Stoped;
        return true;
    }

    @Override
    public String getName() {
        return "ComponentWebController";
    }

    @ModelAttribute
    private void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding(CHAR_SET_UTF_8);
            response.setCharacterEncoding(CHAR_SET_UTF_8);
        } catch (UnsupportedEncodingException e) {
            LoggerFactory.getLogger(this.getClass()).error("setReqAndRes", e);
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(HTTP_SERVLET_REQUEST, request);
        values.put(HTTP_SERVLET_RESPONSE, response);
        tl.set(values);

    }

    protected final void handle() {
        try {
            if (AppStatus.Status == AppStatus.Running) {
                StringBuilder stringBuilder = new StringBuilder();
                String dataStr;
                while ((dataStr = getRequest().getReader().readLine()) != null) {
                    stringBuilder.append(dataStr);
                }
                String data = stringBuilder.toString();
                String pid = getRequest().getHeader(GameCons.PID);
                String module = getRequest().getHeader(GameCons.MODULE);
                IPacket packet;
                if (StringUtils.isBlank(data)) {
                    packet = Packet.newNetBuilder(Short.parseShort(module),Short.parseShort(pid)).setData(null).build();
                } else {
                    packet = Packet.newNetBuilder(Short.parseShort(module),Short.parseShort(pid)).setData(data.getBytes())
                        .build();
                }

                LoggerFactory.getLogger(this.getClass())
                    .info(String.format("http receive %s", packet.toString()));
                GameEventLoop.getInstance().dispatch(packet, getRequest(), getResponse());
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(this.getClass()).error("", e);
        }
    }


    protected HttpServletResponse getResponse() {
        return (HttpServletResponse) tl.get().get(HTTP_SERVLET_RESPONSE);
    }

    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) tl.get().get(HTTP_SERVLET_REQUEST);
    }

    public HttpControllerPlugin setAttr(String name, Object value) {
        getRequest().setAttribute(name, value);
        return this;
    }

    public HttpControllerPlugin removeAttr(String name) {
        getRequest().removeAttribute(name);
        return this;
    }

    public HttpControllerPlugin setAttrs(Map<String, Object> attrMap) {
        for (Map.Entry<String, Object> entry : attrMap.entrySet()) {
            getRequest().setAttribute(entry.getKey(), entry.getValue());
        }
        return this;
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


    public HttpControllerPlugin setSessionAttr(String key, Object value) {
        getRequest().getSession(true).setAttribute(key, value);
        return this;
    }


    public HttpControllerPlugin removeSessionAttr(String key) {
        HttpSession session = getRequest().getSession(false);
        if (session != null) {
            session.removeAttribute(key);
        }
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
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }


    public Cookie[] getCookieObjects() {
        Cookie[] result = getRequest().getCookies();
        return result != null ? result : new Cookie[0];
    }

    public HttpControllerPlugin setCookie(String name, String value, int maxAgeInSeconds,
                                          boolean isHttpOnly) {
        return doSetCookie(name, value, maxAgeInSeconds, null, null, isHttpOnly);
    }


    public HttpControllerPlugin setCookie(String name, String value, int maxAgeInSeconds) {
        return doSetCookie(name, value, maxAgeInSeconds, null, null, null);
    }


    public HttpControllerPlugin setCookie(Cookie cookie) {
        getResponse().addCookie(cookie);
        return this;
    }


    public HttpControllerPlugin setCookie(String name, String value, int maxAgeInSeconds,
                                          String path, boolean isHttpOnly) {
        return doSetCookie(name, value, maxAgeInSeconds, path, null, isHttpOnly);
    }


    public HttpControllerPlugin setCookie(String name, String value, int maxAgeInSeconds,
                                          String path) {
        return doSetCookie(name, value, maxAgeInSeconds, path, null, null);
    }


    public HttpControllerPlugin setCookie(String name, String value, int maxAgeInSeconds,
                                          String path, String domain, boolean isHttpOnly) {
        return doSetCookie(name, value, maxAgeInSeconds, path, domain, isHttpOnly);
    }


    public HttpControllerPlugin removeCookie(String name) {
        return doSetCookie(name, null, 0, null, null, null);
    }


    public HttpControllerPlugin removeCookie(String name, String path) {
        return doSetCookie(name, null, 0, path, null, null);
    }

    public HttpControllerPlugin removeCookie(String name, String path, String domain) {
        return doSetCookie(name, null, 0, path, domain, null);
    }

    private HttpControllerPlugin doSetCookie(String name, String value, int maxAgeInSeconds,
                                             String path, String domain, Boolean isHttpOnly) {
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