package com.tangl.rpcregister;

import com.tangl.rpcregister.common.Constant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@Slf4j
public class RpcDiscover {


    private String registerAddress;

    private volatile List<String> dataList = new ArrayList<>();

    private ZooKeeper zooKeeper;

    public RpcDiscover(String registerAddress) throws Exception {
        this.registerAddress = registerAddress;
        zooKeeper = new ZooKeeper(registerAddress, Constant.SESSION_TIEMOUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                    watchNode();
                }
            }
        });

        watchNode();
    }

    public String discover() {

        int size = dataList.size();
        if (size > 0) {
            int index = new Random().nextInt(size);
            return dataList.get(index);
        }
        throw new RuntimeException("没有找到对应的服务器");
    }

    private void watchNode() {
        try {
            List<String> nodeList = zooKeeper.getChildren(Constant.REGISTRY_PATH, true);
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zooKeeper.getData(Constant.REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            this.dataList = dataList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new RpcDiscover("127.0.0.1:2181").discover());
        System.in.read();
    }
}
