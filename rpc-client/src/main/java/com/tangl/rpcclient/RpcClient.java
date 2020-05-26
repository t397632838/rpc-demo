package com.tangl.rpcclient;

import com.tangl.rpccommon.RpcDecoder;
import com.tangl.rpccommon.RpcEncoder;
import com.tangl.rpccommon.RpcRequest;
import com.tangl.rpccommon.RpcResponse;
import com.tangl.rpcregister.RpcDiscover;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

// rpc通信客户端，往服务端发送请求并接受响应
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    // 消息响应参数
    private RpcResponse response;
    // 消息请求参数
    private RpcRequest request;
    // 同步锁 资源对象
    private Object object = new Object();
    // 用于获取服务地址信息
    private RpcDiscover discover;

    public RpcClient(RpcRequest request, RpcDiscover discover) {
        this.request = request;
        this.discover = discover;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        this.response = msg;
        synchronized (object) {
            ctx.flush();
            object.notifyAll();
        }
    }

    public RpcResponse send() throws Exception {
        // 创建socket通信对象
        Bootstrap client = new Bootstrap();
        // 创建一个通信组，负责channel通道的IO事件处理
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            client.group(loopGroup)
                    // 使用异步通信
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // 编码请求对象
                                    .addLast(new RpcEncoder(RpcRequest.class))
                                    // 节码响应参数
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    .addLast(RpcClient.this);
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);
            // 获取一个服务器地址
            String serverAddress = this.discover.discover();
            String host = serverAddress.split(":")[0];
            int port = Integer.valueOf(serverAddress.split(":")[1]);
            ChannelFuture future = client.connect(host, port).sync();
            System.out.println("客户端准备发送数据:" + request);
            future.channel().writeAndFlush(request).sync();
            synchronized (object) {
                // 线程等待，等待客户端响应
                object.wait();
            }
            if (response != null) {
                // 等待服务端关闭
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            // 关闭socket
            loopGroup.shutdownGracefully();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    public static void main(String[] args) throws Exception {

        EventLoopGroup workerGroup = new NioEventLoopGroup(); // (1)

        try {
            Bootstrap b = new Bootstrap(); // (2)
            b.group(workerGroup); // (3)
            b.channel(NioSocketChannel.class); // (4)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (5)
            b.handler(new ChannelInitializer<SocketChannel>() { // (6)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {            //4
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//                            ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);//5
                            System.out.println("client"+ctx.read());
                        }
                    });
                }
            });

            // Start the client.
            ChannelFuture f = b.connect("127.0.0.1", 9090).sync(); // (7)

            // Wait until the connection is closed.
//            f.channel().closeFuture().sync();

            f.channel().writeAndFlush("netty").channel();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
