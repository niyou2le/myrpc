package com.ding.myrpc.server;

import com.ding.myrpc.service.MyInterfaceImpl;
import com.ding.myrpc.util.RpcServerUtil;

import java.io.IOException;

public class RpcServer {

    static final int port = 8888;

    public static void main(String[] args) throws IOException {
        RpcServerUtil.buildRpcServer(new MyInterfaceImpl(), port);
    }
}