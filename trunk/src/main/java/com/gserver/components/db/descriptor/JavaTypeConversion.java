package com.gserver.components.db.descriptor;
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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JavaTypeConversion {


    private static Logger logger = Logger.getLogger(JavaTypeConversion.class);
    private static final SimpleDateFormat[] formats = new SimpleDateFormat[]{
            new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
            new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.US),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ss"),
            new SimpleDateFormat("yyyy年MM月dd日"),
            new SimpleDateFormat("yyyy-MM-dd")};


    public static <T> T convert(JavaType type, Object valueObj, Class<T> beanClass) {
        return beanClass.cast(convert(type, valueObj));
    }

    public static Object convert(JavaType type, Object valueObj) {
        if (null == type) {
            return valueObj;
        }

        Object result = null;
        if (JavaType.DATE == type) {
            if (valueObj instanceof String) {
                boolean fmtFlag = false;
                logger.debug("时间格式转换开始value:" + valueObj);
                String value = String.valueOf(valueObj);
                for (SimpleDateFormat format : formats) {
                    try {
                        Date date = format.parse(value);
                        logger.debug("时间格式转换成功format:" + format.toPattern());
                        result = new Timestamp(date.getTime());
                        fmtFlag = true;
                        break;
                    } catch (ParseException e) {
                        logger.error("时间格式转换失败format:" + format.toPattern());
                    }
                }
                if (!fmtFlag) {// 如果格式没转换成功 尝试毫秒值转换
                    if (NumberUtils.isNumber(value)) {
                        Long lv = NumberUtils.toLong(value);
                        result = new Timestamp(lv);
                        logger.debug("时间格式转换成功:" + new Timestamp(lv));
                    }
                }
            } else if (valueObj instanceof Long) {
                result = new Timestamp((Long) valueObj);
            } else {
                result = valueObj;
            }
        } else if (JavaType.INTEGER == type) {
            if (valueObj instanceof String) {
                if (((String) valueObj).indexOf("=") == -1) {
                    String[] vals = valueObj.toString().split("\\.");
                    result = Integer.valueOf(vals[0]);
                } else {
                    result = valueObj;
                }
            } else if (valueObj instanceof Float || valueObj instanceof Double) {
                result = (Integer) valueObj;
            } else {
                result = valueObj;
            }
        } else {
            result = valueObj;
        }
        return result;
    }

}
