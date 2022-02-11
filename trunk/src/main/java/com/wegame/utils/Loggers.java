package com.wegame.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一定义系统使用的slf4j的Logger
 *
 *
 */
public final class Loggers {
    /** Server相关的日志 */
    public static final Logger ServerLogger = LoggerFactory.getLogger("server");
    /** Server状态相关的日志 */
    public static final Logger ServerStatusLogger = LoggerFactory.getLogger("server_status");
    /*** 协议日志 **/
    public static final Logger PacketLogger = LoggerFactory.getLogger("packet");
    /*** 游戏逻辑日志 **/
    public static final Logger GameLogger = LoggerFactory.getLogger("game");
    /** Server错误日志 */
    public static final Logger ErrorLogger = LoggerFactory.getLogger("error");
    /*** rpc日志 **/
    public static final Logger RpcLogger = LoggerFactory.getLogger("rpc");
    /*** 用户会话创建销毁日志 **/
    public static final Logger SessionLogger = LoggerFactory.getLogger("session");

    /*** 用户会话创建销毁日志 **/
    public static final Logger ThreadLogger = LoggerFactory.getLogger("thread");
    private Loggers() {
    }
}
