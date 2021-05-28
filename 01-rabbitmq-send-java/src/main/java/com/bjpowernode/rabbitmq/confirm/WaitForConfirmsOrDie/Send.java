package com.bjpowernode.rabbitmq.confirm.WaitForConfirmsOrDie;

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
            channel.basicPublish("directConfirmExchange", "confirmRoutingKey", null, message.getBytes(StandardCharsets.UTF_8));

            /**
             * 批量消息确认，他会同时向服务器中确认之前通道中发送的消息是否已经全部成功写入
             * 这个方法没有任何返回值，如果服务器有一条消息没有成功或向服务器发送确认时，服务器不可访问都被认定为
             * 消息确认失败，可能消息发送失败，需要补发
             * 如果无法向服务器获取确认，会抛出异常（InterruptedException），这时需要补发队列
             *
             * waitForConfirmsOrDie（）可以指定一个时间timeout，超过这个时间也会抛出异常，补发消息
             *
             * Note:
             *      批量消息确认的速度比普通的消息确认要快，但是如果出现需要消息补发的情况，我们不能确认哪条消息失败。
             *      需要全部重新补发
             */
            channel.waitForConfirmsOrDie();

            System.out.println("Transaction message sent. status: ");


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
