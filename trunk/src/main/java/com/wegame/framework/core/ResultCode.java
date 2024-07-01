package com.wegame.framework.core;

import lombok.Getter;

//导入了Lombok的依赖
@Getter
public enum ResultCode {
    SUCCESS(20000, "成功"),
    UNKNOWN_REASON(20001, "未知错误"),
    BAD_SQL_GRAMMAR(21001, "sql语法错误"),
    JSON_PARSE_ERROR(21002, "json解析异常"),
    PARAM_ERROR(21003, "参数不正确"),
    FILE_UPLOAD_ERROR(21004, "文件上传错误"),
    EXCEL_DATA_IMPORT_ERROR(21005, "Excel数据导入错误");

    private final Integer code;
    private final String message;


    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
