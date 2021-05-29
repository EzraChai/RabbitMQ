package com.bjpowernode.rabbitmq;

import com.bjpowernode.rabbitmq.service.ReceiveService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        ReceiveService receiveServiceImpl = (ReceiveService) applicationContext.getBean("receiveServiceImpl");
        //使用了消息监听器，那么不需要调用接收方法
//        receiveServiceImpl.receiveMessage();

    }

}
