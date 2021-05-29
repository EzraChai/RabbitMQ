package com.bjpowernode.springboot;

import com.bjpowernode.springboot.service.impl.SendServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        SendServiceImpl service = (SendServiceImpl) applicationContext.getBean("sendServiceImpl");

        service.sendMessage("I love Chloe Gan.");
    }

}
