package com.ding.myrpc.service;

public class MyInterfaceImpl implements MyInterface{

    @Override
    public String success() {
        return "无参又返回值success";
    }

    @Override
    public String success(String param) {
        return "有参有返回值success，param:" + param;
    }

    @Override
    public void error(String param) throws Exception {
        throw new Exception(param + ",有参，报异常");
    }
}
