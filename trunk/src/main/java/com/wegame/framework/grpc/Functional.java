package com.wegame.framework.grpc;

/**
 * @Program: wegame
 * @Description: 函数式接口
 * @Author: xiongjie.cn@gmail.com
 * @CreateTime: 2021-11-10 09:58
 */
public interface Functional<Arg, Result> {

    /**
     * 测试
     * @param arg
     * @return
     */
    Result run(Arg arg);

}
