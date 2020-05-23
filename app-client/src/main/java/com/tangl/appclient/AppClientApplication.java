package com.tangl.appclient;

import com.tangl.rpcclient.RpcProxy;
import com.tangl.rpcregister.RpcDiscover;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppClientApplication.class, args);
    }


    @Bean
    public RpcProxy getProxy() {
        return new RpcProxy();
    }

    public RpcDiscover getDiscover() throws Exception {
        RpcDiscover discover = new RpcDiscover("127.0.0.1:2181");
        return discover;
    }

}
