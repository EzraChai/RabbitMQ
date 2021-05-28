package com.bjpowernode.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Receive {
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

            channel.queueDeclare("myQueue", true, false, false, null);

            /**
             * 接受消息
             * 参数 1：当前消费者需要监听的队列名，队列名必须以发送时队列名完全一致，否者接收不到消息
             * 参数 2：消息是否自动确认,true == 自动确认，接收完消息以后会自动将消息从队列中移除
             * 参数 3：消息接收者的标签，用于当多个消费者同时监听一个队列时用于确认不同的消费者，通常为空字符串
             * 参数 4：消息接收的回调方法
             * Note:
             *      使用了basicConsume方法以后，会启动一个线程持续的监听列队，如果列队中有新的数据进入，则会自动接收消息，
             *      Therefore 不可以关闭连接和通道
             */
            channel.basicConsume("myQueue",true,"",new DefaultConsumer(channel){

                /**
                 * 消息的具体接收和处理方法
                 * @param consumerTag
                 * @param envelope
                 * @param properties
                 * @param body
                 * @throws IOException
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("message = " + message);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
