package com.ding.myrpc.client;

import com.ding.myrpc.service.MyInterface;
import com.ding.myrpc.util.RpcBuilder;

public class RpcClient {

    static final String host = "127.0.0.1";
    static final int port = 8888;

    public static void main(String[] args) {
        Object rpcClient = RpcBuilder.buildRpcClient(MyInterface.class, host, port);

        MyInterface myInterface = (MyInterface) rpcClient;

        System.out.println(myInterface.success());
        System.out.println(myInterface.success("参数a"));

        try {
            myInterface.error("异常测试");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
