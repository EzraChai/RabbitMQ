package com.bjpowernode.rabbitmq.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Send {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        /**
         * 配置RabbitMq连接信息
         */
        factory.setHost("192.168.11.128");
        factory.setPort(5672);
        factory.setUsername("chloegan");
        factory.setPassword("chloegan");

        //定义连接
        Connection connection = null;
        //定义通道
        Channel channel = null;


        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            /**
             * 由于使用fanout类型交换机，因此交换机可能会有多个，因此不建议在消息发送时创建队列
             * 以及绑定交换机，建议在消费者中创建队列并绑定交换机
             * 但是发送消息至少确认交换机是存在的
             */
//            channel.queueDeclare("MyDirectQueue",true,false,false, null);
//
            channel.exchangeDeclare("fanoutExchange","fanout",true);
//
//            channel.queueBind("MyDirectQueue","directExchange","directRoutingKey");

            String message = "Fanout Message test.";

            channel.basicPublish("fanoutExchange","",null,message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Fanout message sent.");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            try {
                if (channel.isOpen()) {
                    channel.close();
                }
                if (connection.isOpen()) {
                    connection.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}
