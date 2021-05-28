package com.bjpowernode.rabbitmq.exchange.topic;

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


            channel.exchangeDeclare("topicExchange","topic",true);


            String message = "Topic Message test.";

            channel.basicPublish("topicExchange","order.success",null,message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Topic message sent.");

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
