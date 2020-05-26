package com.tangl.rpcserver;

import com.tangl.rpccommon.RpcDecoder;
import com.tangl.rpccommon.RpcEncoder;
import com.tangl.rpccommon.RpcRequest;
import com.tangl.rpccommon.RpcResponse;
import com.tangl.rpcregister.RpcRegister;
import com.tangl.rpcserver.annotation.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// RPC服务端启动,实现Spring的感知接口
public class RpcServer implements ApplicationContextAware, InitializingBean {
    // 用于保存所有提供服务的方法, 其中key为类的全路径名, value是所有的实现类
    private final Map<String, Object> serviceBeanMap = new HashMap<>();

    // 用于注册的相关地址信息
    private RpcRegister rpcRegister;
    // 提供的服务地址信息127.0.0.1:2181
    private String serverAddress;

    // 初始化完成后执行
    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建服务端的通信对象
        ServerBootstrap server = new ServerBootstrap();
        // 创建异步通信的事件组,用于建立TCP连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建异步通信的事件组 用于处理Channel(通道)的I/O事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 开始设置server的相关参数
            server.group(bossGroup, workerGroup)
                    // 启动异步serverSocket
                    .channel(NioServerSocketChannel.class)
                    // 初始化通信通道
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    // 1 请求解码参数
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    // 2.编码相应信息
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    // 3 请求处理
                                    .addLast(new RpcServerHandler(serviceBeanMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 主机地址
            String host = serverAddress.split(":")[0];
            // 主机端口
            Integer port = Integer.valueOf(serverAddress.split(":")[1]);
            // 开启异步通信服务
            ChannelFuture future = server.bind(host, port).sync();
            System.out.println("服务器启动成功:" + future.channel().localAddress());
            rpcRegister.createNode(serverAddress);
            System.out.println("向zkServer注册服务地址信息");
            //等待通信完成
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅的关闭socket
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    //在Spring容器启动完成后会执行该方法
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object obj : serviceBeanMap.values()) {
                String serviveName = obj.getClass().getAnnotation(RpcService.class).value().getName();
                this.serviceBeanMap.put(serviveName, obj);
            }
        }
        System.out.println("服务器: " + serverAddress + " 提供的服务列表: " + serviceBeanMap);
    }

    public static void main(String[] args) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)// (3)
                    .channel(NioServerSocketChannel.class) // (4)
                    .handler(new LoggingHandler())    // (5)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (6)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {            //4
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println("server" + msg);
                                }
                            });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (7)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (8)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind("127.0.0.1", 9090).sync(); // (9)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
