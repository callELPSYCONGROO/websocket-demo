package com.bici.demo.websocket.springboot.domain;

/**
 * @Auther: 加农炮
 * @Date: 2018/10/31 15:01
 * @review:
 * @Description:
 */
public class Greeting {
    private String content;

    public Greeting() {
    }

    public Greeting(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
