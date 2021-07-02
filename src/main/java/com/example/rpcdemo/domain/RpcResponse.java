package com.example.rpcdemo.domain;

import lombok.Data;

/**
 * rpc的回复
 *
 * @author zhangjiawen
 * @date 2021/06/25
 */
@Data
public class RpcResponse {
    /**
     * 响应ID
     */
    private String requestId;
    /**
     * 错误信息
     */
    private String error;
    /**
     * 返回的结果
     */
    private Object result;
}
