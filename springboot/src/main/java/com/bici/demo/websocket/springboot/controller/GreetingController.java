package com.bici.demo.websocket.springboot.controller;

import com.bici.demo.websocket.springboot.domain.HelloMessage;
import com.bici.demo.websocket.springboot.domain.Greeting;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

/**
 * @Auther: 加农炮
 * @Date: 2018/10/31 15:02
 * @review:
 * @Description:
 */
@Controller
public class GreetingController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        //Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}
