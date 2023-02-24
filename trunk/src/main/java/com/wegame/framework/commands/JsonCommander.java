package com.wegame.framework.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegame.framework.renderer.IRenderer;
import com.wegame.framework.renderer.JsonBaseRenderer;
import com.wegame.framework.exception.FormatException;
import io.netty.util.CharsetUtil;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class JsonCommander extends Commander {


    /**
     * @param cookie 添加cookies信息
     */
    protected void addCookie(Cookie cookie) {
        this.getHttpServletResponse().addCookie(cookie);
    }


    protected IRenderer createJsonRenderer() {
        return new JsonBaseRenderer(this.getHttpAction().getRequest(),
            this.getHttpAction().getResponse());
    }

    protected <T extends Object> T getData(String name) {
        Map<String, Object> jsonObject = getObject(Map.class);
        if (jsonObject == null) {
            return null;
        }
        return (T) jsonObject.get(name);
    }

    protected <T> T getObject(Class<T> clazz) {
        if (this.getData() == null) {
            return null;
        }
        String data = new String(this.getData(), CharsetUtil.UTF_8);
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        T jsonObject = null;
        try {
            jsonObject = objectMapper.readValue(data, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    protected int getDataToInt(String name) {
        return toInt(getData(name), null);
    }

    protected String getDataToString(String name, String defalutString) {
        if (getData(name) == null) {
            return defalutString;
        } else {
            return getData(name).toString();
        }
    }

    protected String getDataToString(String name) {
        return getDataToString(name, null);
    }

    protected byte getDataToByte(String name) {
        return toByte(getData(name), null);
    }

    protected byte getDataToByte(String name, byte defaultValue) {
        return toByte(getData(name), defaultValue);
    }

    protected long getDataToLong(String name) {
        return toLong(getData(name), null);
    }

    protected boolean getDataToBoolean(String name) {
        return toBoolean(getData(name), null);
    }

    protected short getDataToShort(String name) {
        return toShort(getData(name), null);
    }

    protected float getDataToFloat(String name) {
        return toFloat(getData(name), null);
    }

    protected Date getDataToDate(String name) {
        return toDate(getData(name), null, null);
    }

    protected Date getDataToDate(String name, String format) {
        return toDate(getData(name), format, null);
    }

    protected Date getDataToDate(String name, String format, Date defaultValue) {
        return toDate(getData(name), format, defaultValue);
    }

    protected Date getDataToDate(String name, Date defaultValue) {
        return toDate(getData(name), null, defaultValue);
    }

    protected double getDataToDouble(String name) {
        return toDouble(getData(name), null);
    }

    protected int getDataToInt(String name, int defaultValue) {
        return toInt(getData(name), defaultValue);
    }


    protected long getDataToLong(String name, long defaultValue) {
        return toLong(getData(name), defaultValue);
    }

    protected boolean getDataToBoolean(String name, boolean defaultValue) {
        return toBoolean(getData(name), defaultValue);
    }

    protected short getDataToShort(String name, short defaultValue) {
        return toShort(getData(name), defaultValue);
    }

    protected float getDataToFloat(String name, float defaultValue) {
        return toFloat(getData(name), defaultValue);
    }


    protected double getDataToDouble(String name, double defaultValue) {
        return toDouble(getData(name), defaultValue);
    }

    private int toInt(Object value, Integer defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            throw new FormatException(
                "Can not parse the parameter \"" + value + "\" to Integer value.");
        }
    }

    private long toLong(Object value, Long defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            throw new FormatException(
                "Can not parse the parameter \"" + value + "\" to Long value.");
        }
    }

    private boolean toBoolean(Object value, Boolean defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Boolean.parseBoolean(value.toString());
        } catch (Exception e) {
            throw new FormatException(
                "Can not parse the parameter \"" + value + "\" to Boolean value.");
        }
    }

    private short toShort(Object value, Short defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Short.parseShort(value.toString());
        } catch (Exception e) {
            throw new FormatException(
                "Can not parse the parameter \"" + value + "\" to Short value.");
        }
    }

    private byte toByte(Object value, Byte defaultValue) {
        try {
            if (value == null) {
                return defaultValue;
            }
            return Byte.parseByte(value.toString());
        } catch (Exception e) {
            throw new FormatException(
                "Can not parse the parameter \"" + value + "\" to Byte value");
        }
    }

    private double toDouble(Object value, Double defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            throw new FormatException(
                "Can not parse the parameter \"" + value + "\" to Double value.");
        }
    }

    private float toFloat(Object value, Float defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Float.parseFloat(value.toString());
        } catch (Exception e) {
            throw new FormatException(
                "Can not parse the parameter \"" + value + "\" to Float value.");
        }
    }

    private Date toDate(Object value, String formatString, Date defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            if (formatString == null) {
                return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(value.toString().trim());
            } else {
                return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    value.toString().trim());
            }

        } catch (Exception e) {
            throw new FormatException(
                "Can not parse the parameter \"" + value + "\" to Date value.");
        }
    }
}
