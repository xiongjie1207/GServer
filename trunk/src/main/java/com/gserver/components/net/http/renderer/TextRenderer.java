package com.gserver.components.net.http.renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TextRenderer implements IRenderer<String> {
    private static final Logger logger = LoggerFactory.getLogger(TextRenderer.class);
    private HttpServletResponse response;
    private HttpServletRequest request;

    public TextRenderer(HttpServletRequest request,HttpServletResponse response) {
        this.response = response;
        this.request = request;
    }

    @Override
    public void render(String data) {
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            response.getWriter().write(data);
            response.getWriter().flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                response.getWriter().close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
