package com.tangl.rpccommon;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RpcResponse {
    // 响应的消息id
    private String responseId;
    // 请求的消息id
    private String requestId;
    // 响应的消息是否成功
    private boolean success;
    // 响应的数据结果
    private Object result;
    // 异常信息
    private Throwable throwable;
}
