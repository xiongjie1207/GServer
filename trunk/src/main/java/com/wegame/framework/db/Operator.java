package com.wegame.framework.db;

/**
 * @Author xiongjie
 * @Date 2024/04/29 15:45
 **/
public enum Operator {
    Equal("="), LessThan("<"), GreaterThan(">"), LessEqualThan("<="),
    GreaterEqualThan(">="), Like("%like%"), LikeEnd("like%"), LikeStart("%like"),
    Null("null"), NotNull("!null"), NotEqual("!="), In("in");
    private final String value;

    Operator(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
