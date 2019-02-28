package com.bici.demo.websocket.springboot.domain;

/**
 * @Auther: 加农炮
 * @Date: 2018/10/31 15:01
 * @review:
 * @Description:
 */
public class HelloMessage {
    private String name;

    public HelloMessage() {
    }

    public HelloMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
