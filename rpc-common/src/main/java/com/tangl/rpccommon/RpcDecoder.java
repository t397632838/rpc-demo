package com.tangl.rpccommon;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 消息长度
        int size = in.readableBytes();
        if (size < 4) {//保证所有的消息都完全接受完成
            return;
        }
        byte[] bytes = new byte[size];
        // 把传递的字节数组读取到byte是中
        in.readBytes(bytes);
        // 反序列化为对象(RPCRequest/RPCResponse对象)
        Object object = SerializationUtil.descrialize(bytes, genericClass);
        //输出对象
        out.add(object);
        // 刷新缓存
        ctx.flush();
    }
}
