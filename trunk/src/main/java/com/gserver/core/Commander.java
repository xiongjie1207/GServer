package com.gserver.core;
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
 * Created by xiongjie on 2016/12/22.
 */

import com.gserver.exception.FormatException;
import com.gserver.utils.JsonUtils;

import java.util.Date;
import java.util.Map;
public abstract class Commander {
    protected <T extends Action> T getAction() {
        return (T) ServerContext.getContext().getAction();
    }

    protected <T extends Object> T getData(String name) {
        return JsonUtils.getValue(getAction().getPacket().getRoot(), name);

    }

    protected boolean hasNode(String name) {
        String[] names = name.split("\\.");

        Map<String, Object> result = getAction().getPacket().getRoot();
        for (String n : names) {
            Object r = result.get(n);
            if (r == null) {
                return false;
            }
            if (r instanceof Map) {
                result = (Map<String, Object>) r;
            } else {
                return true;
            }
        }
        return true;
    }

    protected int getDataToInt(String name) {
        return toInt(getData(name), null);
    }

    protected String getDataToString(String name, String defalutString) {
        Object value = getData(name);
        if (value == null) {

            return defalutString;
        }
        return value.toString();
    }

    protected byte getDataToByte(String name) {
        return toByte(name, null);
    }

    protected byte getDataToByte(String name, byte defaultValue) {
        return toByte(name, defaultValue);
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

    protected String getDataToString(String name) {
        return getDataToString(name, null);
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
            throw new FormatException("Can not parse the parameter \"" + value + "\" to Integer value.");
        }
    }

    private long toLong(Object value, Long defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            throw new FormatException("Can not parse the parameter \"" + value + "\" to Long value.");
        }
    }

    private boolean toBoolean(Object value, Boolean defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Boolean.parseBoolean(value.toString());
        } catch (Exception e) {
            throw new FormatException("Can not parse the parameter \"" + value + "\" to Boolean value.");
        }
    }

    private short toShort(Object value, Short defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Short.parseShort(value.toString());
        } catch (Exception e) {
            throw new FormatException("Can not parse the parameter \"" + value + "\" to Short value.");
        }
    }

    private byte toByte(Object value, Byte defaultValue) {
        try {
            if (value == null) {
                return defaultValue;
            }
            return Byte.parseByte(value.toString());
        } catch (Exception e) {
            throw new FormatException("Can not parse the parameter \"" + value + "\" to Byte value");
        }
    }

    private double toDouble(Object value, Double defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            throw new FormatException("Can not parse the parameter \"" + value + "\" to Double value.");
        }
    }

    private float toFloat(Object value, Float defaultValue) {
        try {
            if (value == null) {

                return defaultValue;
            }
            return Float.parseFloat(value.toString());
        } catch (Exception e) {
            throw new FormatException("Can not parse the parameter \"" + value + "\" to Float value.");
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
                return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.toString().trim());
            }

        } catch (Exception e) {
            throw new FormatException("Can not parse the parameter \"" + value + "\" to Date value.");
        }
    }
}
