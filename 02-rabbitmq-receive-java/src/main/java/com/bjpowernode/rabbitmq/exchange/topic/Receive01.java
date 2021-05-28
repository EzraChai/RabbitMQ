package com.bjpowernode.rabbitmq.exchange.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receive01 {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.11.128");
        factory.setPort(5672);
        factory.setUsername("chloegan");
        factory.setPassword("chloegan");

        Connection connection = null;
        Channel channel = null;

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            /**
             * Topic 类型的交换机也是一对多的一种交换机类型，它和fanout都能够一个消息同时发送给多个队列
             *
             *
             * fanout 更适合用于同一个功能，不同的进程来获取数据，例如：APP消息推送，一个App可能会有很多
             * 用户来进行安装，然后他们都会启动一个随机的队列来接受自己的数据
             *
             * topic 更适合不同功能模块来接收同一个消息，例如：商城下单成功后需要发送到消息队列中，例如：Routing Key
             * 为order.success, 物流监听订单order.*，发票监听order.*
             *
             * topic 可以使用明确队列名称，也可以使用随机队列名称
             * 如果应用和订单有关，建议使用有明确队列名称并且要求为持久化队列（Persistence）
             *
             */
            channel.queueDeclare("TopicQueue01", true, false, false, null);

            channel.exchangeDeclare("topicExchange", "topic", true);

            channel.queueBind("TopicQueue01", "topicExchange", "order");

            channel.basicConsume("TopicQueue01", true, "", new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body);
                    System.out.println("Receive01 consumer aa received a message ---- " + message);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
