package com.ding.myrpc.common.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInfoClass implements Serializable {

    private String requestId;
    private Object data;
    private Throwable cause;

    public boolean isError() {
        return cause != null;
    }
}
