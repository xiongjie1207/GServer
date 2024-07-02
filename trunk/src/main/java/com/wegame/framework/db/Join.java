package com.wegame.framework.db;

import lombok.Getter;

/**
 * @Author xiongjie
 * @Date 2024/04/29 15:25
 **/
@Getter
public enum Join {
    And("and"), Or("or"), GroupBy("groupBy");
    private final String value;

    Join(String value) {
        this.value = value;
    }
}
