package com.bjpowernode.springboot.service.impl;

import com.bjpowernode.springboot.service.TestService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendServiceImpl implements TestService {

    @Value("${product.key}")
    private String productKey;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public void sendMessage(String message) {
        /**
         * 发送消息
         * 参数 1：交换机名称
         * 参数 2：RoutingKey
         * 参数 3：具体发送消息数据
         */
        amqpTemplate.convertAndSend("bootDirectExchange","bootDirectRoutingKey",message);
    }


    @Override
    public void sendFanoutMessage(String message) {
        amqpTemplate.convertAndSend("fanoutBootExchange","",message);
    }

    @Override
    public void sendTopicMessage(String message) {
        System.out.println(productKey);
        amqpTemplate.convertAndSend("topicBootExchange",productKey,message);
    }

}
