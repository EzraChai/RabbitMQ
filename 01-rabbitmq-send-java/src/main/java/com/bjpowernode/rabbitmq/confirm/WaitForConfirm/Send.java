package com.bjpowernode.rabbitmq.confirm.WaitForConfirm;

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

            channel.queueDeclare("confirmQueue", true, false, false, null);

            channel.exchangeDeclare("directConfirmExchange", "direct", true);

            channel.queueBind("confirmQueue", "directConfirmExchange", "confirmRoutingKey");

            String message = "Confirm Mode Message test.";

            /**
             * 启动发送者确认模式
             */
            channel.confirmSelect();

            channel.basicPublish("directConfirmExchange", "confirmRoutingKey", null, message.getBytes(StandardCharsets.UTF_8));
//            System.out.println(10/0);
            channel.basicPublish("directConfirmExchange", "confirmRoutingKey", null, message.getBytes(StandardCharsets.UTF_8));

            /**
             * 阻塞线程等待服务器返回响应，用于是否消费者发送成功，如果服务确认消费已经发送完则返回true
             * 可以为这个方法指定一个毫秒用于确认我们的需要等待服务确认的超时时间，如果超过了指定将会抛出异常（InterruptedException）表示服务器出现问题，
             * 需要补发消息或者将消息缓存到redis中后利用定时任务补发
             * 无论是返回false还是抛出异常，消息有可能发送成功或没有成功
             * 如果我们要求这个消息一定要发送到队列例如订单数据，我们可以使用消息补发
             * {消息补发}重新发送一次消息，可以使用递归，或利用redis加定时任务完成补发
             */
            boolean flag = channel.waitForConfirms();

            System.out.println("Transaction message sent. status: " + flag);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
