package com.gserver.renderer;

import com.gserver.core.Packet;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonRenderer implements IRenderer<Packet> {
    private static final Logger logger = LoggerFactory.getLogger(JsonRenderer.class);
    private HttpServletResponse response;

    private HttpServletRequest request;


    public JsonRenderer(HttpServletRequest request,HttpServletResponse response) {
        this.response = response;
        this.request = request;
    }


    @Override
    public void render(Packet packet) {
        response.setContentType("application/plain;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            response.getWriter().write(packet.toJSONString());
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
