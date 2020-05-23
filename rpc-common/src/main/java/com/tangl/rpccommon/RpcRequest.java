package com.tangl.rpccommon;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RpcRequest {
    // 消息id
    private String requestId;
    // 请求的具体类名
    private String className;
    // 请求的具体方法名称
    private String methodName;
    // 请求的方法参数类型列表
    private Class<?>[] parameterTypes;
    // 请求的方法参数列表
    private Object[] parameters;
}
