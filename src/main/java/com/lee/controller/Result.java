package com.lee.controller;

import java.io.Serializable;

public class Result implements Serializable {

    private Integer stateCode;
    private String msg;

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Result(Integer stateCode, String msg) {
        this.stateCode = stateCode;
        this.msg = msg;
    }
}
