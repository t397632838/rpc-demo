package com.tangl.rpcserver;

import com.tangl.rpccommon.RpcRequest;
import com.tangl.rpccommon.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Object> serviceBeanMap = new HashMap<>();

    public RpcServerHandler(Map<String, Object> serviceBeanMap) {
        this.serviceBeanMap = serviceBeanMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("RpcServerHandler.channelRead");
        System.out.println(msg);
        RpcRequest request = (RpcRequest) msg;
        RpcResponse response = handler(request);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    private RpcResponse handler(RpcRequest request) {
        // 创建一个响应消息体
        RpcResponse response = new RpcResponse();
        // 设置响应消息id
        response.setRequestId(UUID.randomUUID().toString());
        response.setRequestId(request.getRequestId());
        try {

            // 获取到类名（接口名）
            String className = request.getClassName();
            // 获取方法名
            String methodName = request.getMethodName();
            // 获取到参数类型列表
            Class<?>[] parameterTypes = request.getParameterTypes();
            // 获取到参数列表
            Object[] parameters = request.getParameters();
            // 获取到具体字节码对象
            Class<?> clz = Class.forName(className);
            // 获取到实现类
            Object serviceBean = serviceBeanMap.get(className);
            if (serviceBean == null) {
                throw new RuntimeException(className + "没有找到对应的serviceBean:" + className + ":beanMap:" + serviceBeanMap);
            }
            // 反射调用方法
            Method method = clz.getMethod(methodName, parameterTypes);
            if (method == null) {
                throw new RuntimeException("没有找到对应方法");
            }
            Object result = method.invoke(serviceBean, parameters);
            response.setSuccess(true);
            response.setResult(result);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setThrowable(e);
            e.printStackTrace();
        }
        return response;
    }
}
