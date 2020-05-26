package com.tangl.rpcregister;

import com.tangl.rpcregister.common.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class RpcRegister {


    private String registerAddress;

    private ZooKeeper zooKeeper;


    public void createNode(String data) throws Exception {
        // 创建一个客户端程序, 对于注册可以不用监听事件
        zooKeeper = new ZooKeeper(registerAddress, Constant.SESSION_TIEMOUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
            }
        });
        if (zooKeeper != null) {
            try {
                Stat stat = zooKeeper.exists(Constant.REGISTRY_PATH, false);
                if (stat == null) {
                    // 如果不存在, 创建一个持久的节点目录
                    zooKeeper.create(Constant.REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                String dataPath = zooKeeper.create(Constant.DATA_PATH, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                System.out.println("数据地址:"+dataPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.warn("Zookeeper connect is null");
        }
    }


    public static void main(String[] args) throws Exception {
        RpcRegister register = new RpcRegister();
        register.setRegisterAddress("127.0.0.1:2181");
        register.createNode("testNode");
        System.in.read();
    }


}
