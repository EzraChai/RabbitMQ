package com.bjpowernode.rabbitmq.confirm.addConfirmListener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
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

            /**
             * 异步消息确认监听器，需要在发送消息前启动
             */
            channel.addConfirmListener(new ConfirmListener() {
                /**
                 * 消息确认后的回调方法
                 * @param l 确认消息的编号，从 1开始，用于标记当前的第几个消息
                 * @param b 当前消息是否同时确认了多个消息
                 * @throws IOException
                 *
                 * Note:
                 *         如果参数2==true , 表示本次确认了多条信息 ，<= 当前参数 1 （消息编号）
                 *
                 */
                @Override
                public void handleAck(long l, boolean b) throws IOException {
                    System.out.println("Message has been handle ---- Message ID : " + l + " Had already been handle: " + b);
                }

                /**
                 * 消息没有确认后的回调方法
                 * 如果这个方法被执行表示当前方法没有被确认，需要进行消息补发
                 * @param l 确认消息的编号，从 1开始，用于标记当前的第几个消息
                 * @param b 当前消息是否同时没有确认了多个消息
                 *
                 * @throws IOException
                 */
                @Override
                public void handleNack(long l, boolean b) throws IOException {
                    System.out.println("Message Failed to handle ---- Message ID : " + l + " Had already been handle: " + b);
                }
            });

            for (int i = 0; i < 1000 ; i++){
                channel.basicPublish("directConfirmExchange", "confirmRoutingKey", null, message.getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("Message sent.");

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
