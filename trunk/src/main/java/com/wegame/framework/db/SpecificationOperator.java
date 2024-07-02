package com.wegame.framework.db;

import lombok.Getter;

/**
 * 操作符类，这个类中存储了键值对和操作符号，另外存储了连接下一个条件的类型是and还是or
 * 创建时通过 id>=7,其中id就是key,>=就是oper操作符，7就是value
 * 特殊的自定义几个操作符(:表示like %v%，l:表示v%,:l表示%v)
 */
@Getter
public class SpecificationOperator {
    /**
     * 操作符的key，如查询时的name,id之类
     */
    private final String key;
    /**
     * 操作符的value，具体要查询的值
     */
    private final Object value;
    /**
     * 操作符，自己定义的一组操作符，用来方便查询
     */
    private final Operator oper;
    /**
     * 连接的方式：and或者or
     */
    private final Join join;

    SpecificationOperator(String key, Object value, Operator oper, Join join) {

        this.key = key;
        this.value = value;
        this.oper = oper;
        this.join = join;
    }
}