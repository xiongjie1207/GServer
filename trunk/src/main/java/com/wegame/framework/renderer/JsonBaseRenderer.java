package com.wegame.framework.renderer;

import com.wegame.framework.core.GameCons;
import com.wegame.framework.packet.IPacket;
import io.netty.util.CharsetUtil;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonBaseRenderer implements IRenderer<IPacket> {
    private final HttpServletResponse response;

    private final HttpServletRequest request;

    public JsonBaseRenderer(HttpServletRequest request, HttpServletResponse response) {
        this.response = response;
        this.request = request;
    }

    @Override
    public void render(IPacket packet) {
        response.setContentType("application/plain;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader(GameCons.PID, String.valueOf(packet.getPid()));
        response.setHeader(GameCons.MODULE, String.valueOf(packet.getModule()));
        response.setDateHeader("Expires", 0);

        try {
            String data = new String(packet.getData(), CharsetUtil.UTF_8);
            LoggerFactory.getLogger(JsonBaseRenderer.class)
                .info(String.format("post pid:%s", packet));
            response.getWriter().write(data);
            response.getWriter().flush();
        } catch (Exception e) {
            LoggerFactory.getLogger(JsonBaseRenderer.class).error(e.getMessage(), e);
        } finally {
            try {
                response.getWriter().close();
            } catch (IOException e) {
                LoggerFactory.getLogger(JsonBaseRenderer.class).error(e.getMessage(), e);
            }
        }
    }
}
