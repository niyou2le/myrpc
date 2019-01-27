package com.ding.myrpc.service.api;

public interface MyInterface {

    String success();

    String success(String param);

    void error(String param) throws Exception;
}
