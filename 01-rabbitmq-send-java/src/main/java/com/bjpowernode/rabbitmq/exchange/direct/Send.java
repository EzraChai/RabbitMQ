package com.bjpowernode.rabbitmq.exchange.direct;

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

            channel.queueDeclare("MyDirectQueue", true, false, false, null);

            /**
             * 声明一个交换机
             * 参数 1：交换机名称
             * 参数 2：交换机类型 取值 direct,fanout,topic, headers
             * 参数 3：是否持久化交换机
             * Note:
             *      1.声明交换机时，如果交换机已经存在了，如果存在则会放弃声明
             *      2.这行代码时可有可无的，但是需确定交换机已存在
             */
            channel.exchangeDeclare("directExchange", "direct", true);

            /**
             * 将队列绑定到交换机
             * 参数 1：队列名称
             * 参数 2：交换机名称
             * 参数 3：消息的 RoutingKey (BindingKey)
             * Note:
             *      在进行交换机绑定时，要确保交换机和队列已成功声明
             */
            channel.queueBind("MyDirectQueue", "directExchange", "directRoutingKey");

            String message = "Direct Message test.";

            /**
             * 发送消息到队列
             * 参数 1：交换机名称
             * 参数 2：消息的RoutingKey,
             * Note:
             *      必须确保交换机和队列绑定已创建
             */
            channel.basicPublish("directExchange", "directRoutingKey", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Direct message sent.");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
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
