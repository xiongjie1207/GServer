package com.gserver.utils;
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.BeanMap;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class JsonUtil {

    /**
     * 格式化时间的string
     */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    private static ObjectMapper MAPPER = generateMapper(Inclusion.ALWAYS);
    static {
        //序列化的时候序列对象的所有属性
        MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        //反序列化的时候如果多了其他属性,不抛出异常
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        //如果是空对象的时候,不抛异常
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);

        //取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    }

    private JsonUtil() {
    }

    /**
     * fromJsonToObject<br>
     * jackjson把json字符串转换为Java对象的实现方法
     * <p>
     * <pre>
     * return Jackson.jsonToObj(this.answersJson, new TypeReference&lt;List&lt;StanzaAnswer&gt;&gt;() {
     * });
     * </pre>
     *
     * @param <T>           转换为的java对象
     * @param json          json字符串
     * @param typeReference jackjson自定义的类型
     * @return 返回Java对象
     */
    public static <T> T jsonToObj(String json, TypeReference<T> typeReference) {
        return jsonToObj(json, typeReference);
    }

    public static <T> List<T> jsonToList(String json, Class<T> valueType) {
        if (StringUtils.isNotEmpty(json) && !"null".equals(json)) {
            try {
                List<T> resultList = new ArrayList<T>();
                List<Map<?, ?>> list = MAPPER.readValue(json, List.class);
                if (list.isEmpty()) {
                    return null;
                }
                for (Map<?, ?> obj: list) {
                    resultList.add(MAPPER.convertValue(obj, valueType));
                }
                return resultList;
            } catch (JsonParseException e) {
                Loggers.ErrorLogger.error("JsonParseException: ", e);
            } catch (JsonMappingException e) {
                Loggers.ErrorLogger.error("JsonMappingException: ", e);
            } catch (IOException e) {
                Loggers.ErrorLogger.error("IOException: ", e);
            }
        }
        return null;
    }


    public static Map<?, ?> objToMap(Object obj) {
        Map<String, Object> map = MAPPER.convertValue(obj, Map.class);
        Map<String, Object> result = new HashMap<String, Object>();
        for (Entry<String, Object> item: map.entrySet()) {
            if (null != item.getValue()) {
                result.put(item.getKey(), item.getValue());
            }
        }
        return result;
    }

    /**
     * 将对象列表转换为Map列表
     */
    public static List<Map<String, Object>> objListToMapList(List<?> list) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Object obj: list) {
            result.add((Map<String, Object>) objToMap(obj));
        }
        return result;
    }

    public static <T> T mapToObj(Map<String, Object> map, Class<T> valueType) {
        if (null != map) {
            Map<String, Object> keyMap = new HashMap<String, Object>();
            for (Object key: map.keySet()) {
                keyMap.put(String.valueOf(key), key);
            }
            Object bean = null;
            try {
                bean = valueType.newInstance();
                BeanMap beanMap = new BeanMap(bean);
                for (Object key: beanMap.keySet()) {
                    String keyName = String.valueOf(key);
                    if (keyMap.containsKey(keyName)) {
                        Object value = map.get(keyName);
                        if (value instanceof Map) {
                            Map<String, Object> valueMap = (Map<String, Object>) value;
                            if (valueMap.containsKey("$numberLong")) {
                                value = valueMap.get("$numberLong");
                            }
                        }
                        BeanUtils.copyProperty(bean, String.valueOf(key), value);
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return valueType.cast(bean);
        }
        return null;
    }

    public static <T> T toObj(Object obj, Class<T> typeReference) {
        T result = MAPPER.convertValue(obj, typeReference);
        return result;
    }

    public static <T> List<T> mapListToObjList(List<Map<String, Object>> resultList, Class<T> beanClass) {
        if (null == resultList || resultList.size() == 0) {
            return new ArrayList<T>();
        }
        BeanMap beanMap = null;
        List<T> rtList = new ArrayList<T>();
        try {
            for (Map<String, Object> item: resultList) {
                Object bean = beanClass.newInstance();
                beanMap = new BeanMap(bean);
                for (Object key: beanMap.keySet()) {
                    String keyName = String.valueOf(key);
                    if (item.containsKey(keyName)) {
                        try {
                            Object value = item.get(keyName);
                            if (value instanceof Map) {
                                Map<String, Object> valueMap = (Map<String, Object>) value;
                                if (valueMap.containsKey("$numberLong")) {
                                    value = valueMap.get("$numberLong");
                                } else {

                                }
                            }
                            BeanUtils.copyProperty(bean, String.valueOf(key), value);
                        } catch (InvocationTargetException e) {
                            Loggers.ErrorLogger.error("Bean copy property error.", e);
                        }
                    }
                }
                rtList.add(beanClass.cast(bean));
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return rtList;
    }

    /**
     * fromJsonToObject<br>
     * json转换为java对象
     * <p>
     * <pre>
     * return Jackson.jsonToObj(this.answersJson, Jackson.class);
     * </pre>
     *
     * @param <T>       要转换的对象
     * @param json      字符串
     * @param valueType 对象的class
     * @return 返回对象
     */

    public static <T> T jsonToObj(String json, Class<T> valueType) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return MAPPER.readValue(json, valueType);
            } catch (JsonParseException e) {
                Loggers.ErrorLogger.error("JsonParseException: ", e);
            } catch (JsonMappingException e) {
                Loggers.ErrorLogger.error("JsonMappingException: ", e);
            } catch (IOException e) {
                Loggers.ErrorLogger.error("IOException: ", e);
            }
        }
        return null;
    }

    /**
     * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
     * <ul>
     * <li>该方法不会转换 {@code null} 值字段；</li>
     * </ul>
     *
     * @param object 要转换成 {@code JSON} 的目标对象。
     * @return 目标对象的 {@code JSON} 格式的字符串。
     * @since 1.0
     */
    public static String objToJson(Object object) {
        if (object != null) {
            try {
                return MAPPER.writeValueAsString(object);
            } catch (JsonGenerationException e) {
                Loggers.ErrorLogger.error("JsonGenerationException: ", e);
            } catch (JsonMappingException e) {
                Loggers.ErrorLogger.error("JsonMappingException: ", e);
            } catch (IOException e) {
                Loggers.ErrorLogger.error("IOException: ", e);
            }
        }
        return null;
    }


    /**
     * 将json通过类型转换成对象
     *
     * @param json  json字符串
     * @param clazz 泛型类型
     * @return 返回对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return clazz.equals(String.class) ? (T) json : MAPPER.readValue(json, clazz);
            } catch (JsonParseException e) {
                Loggers.ErrorLogger.error("JsonParseException: ", e);
            } catch (JsonMappingException e) {
                Loggers.ErrorLogger.error("JsonMappingException: ", e);
            } catch (IOException e) {
                Loggers.ErrorLogger.error("IOException: ", e);
            }
        }
        return null;
    }

    /**
     * 将json通过类型转换成对象
     *
     * @param json          json字符串
     * @param typeReference 引用类型
     * @return 返回对象
     */
    public static <T> T fromJson(String json, TypeReference<?> typeReference) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return (T) (typeReference.getType().equals(String.class) ? json : MAPPER.readValue(json, typeReference));
            } catch (JsonParseException e) {
                Loggers.ErrorLogger.error("JsonParseException: ", e);
            } catch (JsonMappingException e) {
                Loggers.ErrorLogger.error("JsonMappingException: ", e);
            } catch (IOException e) {
                Loggers.ErrorLogger.error("IOException: ", e);
            }
        }
        return null;
    }

    /**
     * 将对象转换成json
     *
     * @param src 对象
     * @return 返回json字符串
     */
    public static <T> String toJson(T src) {
        try {
            return src instanceof String ? (String) src : MAPPER.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            Loggers.ErrorLogger.error("JsonParseException: ", e);
        }
        return null;
    }

    /**
     * 将对象转换成json, 可以设置输出属性
     *
     * @param src       对象
     * @param inclusion 传入一个枚举值, 设置输出属性
     * @return 返回json字符串
     */
    public static <T> String toJson(T src, Inclusion inclusion) {
        if (src instanceof String) {
            return (String) src;
        } else {
            ObjectMapper customMapper = generateMapper(inclusion);
            try {
                return customMapper.writeValueAsString(src);
            } catch (JsonProcessingException e) {
                Loggers.ErrorLogger.error("JsonProcessingException: ", e);
            }
        }
        return null;
    }

    /**
     * 将对象转换成json, 传入配置对象
     *
     * @param src    对象
     * @param mapper 配置对象
     * @return 返回json字符串
     * @see ObjectMapper
     */
    public static <T> String toJson(T src, ObjectMapper mapper) {
        if (null != mapper) {
            if (src instanceof String) {
                return (String) src;
            } else {
                try {
                    return mapper.writeValueAsString(src);
                } catch (JsonProcessingException e) {
                    Loggers.ErrorLogger.error("JsonProcessingException: ", e);
                }
            }
        } else {
            return null;
        }
        return null;
    }

    /**
     * 返回{@link ObjectMapper ObjectMapper}对象, 用于定制性的操作
     *
     * @return {@link ObjectMapper ObjectMapper}对象
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }

    /**
     * 通过Inclusion创建ObjectMapper对象
     *
     * @param inclusion 传入一个枚举值, 设置输出属性
     * @return 返回ObjectMapper对象
     */
    private static ObjectMapper generateMapper(Inclusion inclusion) {

        ObjectMapper customMapper = new ObjectMapper();

        return customMapper;
    }

    public static <T extends Object> T getValue(Map<String, Object> jsonData, String property) {
        String[] names = property.split("\\.");

        Map<String, Object> result = jsonData;
        for (String n: names) {
            Object r = result.get(n);
            if (r == null) {
                return null;
            } else {
                return (T) r;
            }
        }
        return (T) result;
    }
}
