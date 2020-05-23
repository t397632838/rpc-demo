package com.tangl.rpcclient;

import com.tangl.rpccommon.RpcRequest;
import com.tangl.rpccommon.RpcResponse;
import com.tangl.rpcregister.RpcDiscover;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

@Getter
@Setter
//动态代理类,用于获取到每个类的代理对象
//对于被代理对象的所有的方法调用都会执行invoke方法
public class RpcProxy {

    // 用于获取rpc-srever的地址
    private RpcDiscover discover;


    public <T> T getInstance(Class<T> interfaceClass) {
        T instance = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 创建请求对象
                RpcRequest request = new RpcRequest();
                // 获取被调的类名，和rpc-server中的serviceMap中的key进行匹配
                String className = method.getDeclaringClass().getName();
                // 获取到方法的参数列表
                Class<?>[] parameterTypes = method.getParameterTypes();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(className);
                request.setParameterTypes(parameterTypes);
                request.setParameters(args);
                request.setMethodName(method.getName());
                RpcResponse response = new RpcClient(request, discover).send();
                return response.getResult();
            }
        });

        return instance;
    }
}
