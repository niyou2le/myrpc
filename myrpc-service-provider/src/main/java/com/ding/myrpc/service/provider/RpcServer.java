package com.ding.myrpc.service.provider;

import com.ding.myrpc.server.util.RpcServerUtil;

import java.io.IOException;

public class RpcServer {

    static final int port = 8888;

    public static void main(String[] args) throws IOException {
        RpcServerUtil.buildRpcServer(new MyInterfaceImpl(), port);
    }
}