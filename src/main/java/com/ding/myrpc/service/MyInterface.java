package com.ding.myrpc.service;

public interface MyInterface {

    String success();

    String success(String param);

    void error(String param) throws Exception;
}
