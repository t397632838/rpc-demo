package com.tangl.appserver;

import com.tangl.rpcregister.RpcRegister;
import com.tangl.rpcserver.RpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//@SpringBootApplication
public class AppServerApplication {

    public static void main(String[] args) {
//        SpringApplication.run(AppServerApplication.class, args);
        new ClassPathXmlApplicationContext("classpath:application.xml");
    }


//    @Bean
//    public RpcRegister getRegister() {
//        RpcRegister register = new RpcRegister();
//        register.setRegisterAddress("127.0.0.1:2181");
//        return register;
//    }
//
//    @Bean
//    public RpcServer getServer() {
//        RpcServer server = new RpcServer();
//        server.setServerAddress("127.0.0.1:8090");
//        server.setRpcRegister(getRegister());
//        return server;
//    }
}
