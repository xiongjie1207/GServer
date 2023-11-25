package com.wegame.framework.grpc;

/**
 * @Program: grpc-demo
 * @Description: 函数式接口
 * @Author: ccm
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
