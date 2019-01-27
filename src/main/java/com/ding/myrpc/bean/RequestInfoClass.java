package com.ding.myrpc.bean;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/*
* 通过这些参数，反射生成对象
* */
@Data
@Builder
public class RequestInfoClass implements Serializable {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
