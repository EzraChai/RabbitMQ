package com.bjpowernode.rabbitmq.exchange.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receive02 {
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
             * 由于Fanout 类型交换机类似于广播模式，它不需要绑定RoutingKey
             * 而又可能会有很多消费者来接收数据，因此我们创建队列时要创建一个随机的队列名称
             *
             * 没有参数的queueDeclare会创建一个随机的队列
             * 队列数据非持久，
             * 排外（同时只允许一个消费者监听当前队列）
             * ，自动删除
             * ，当没有任何消费者监听时，这个队列会自动删除
             *
             * getQueue()
             * 用于获取随机的队列名称
             */
            String queueName = channel.queueDeclare().getQueue();

            channel.exchangeDeclare("fanoutExchange","fanout",true);

            //将这个随机队列绑定到交换机中，由于是fanout类型的交换机，所以不需要RoutingKey进行绑定
            channel.queueBind(queueName,"fanoutExchange","");

            /**
             * 监听某个队列并获取队列中的数据
             * NOte:
             *      当前监听队列必须已经存在，并正确的绑定到了某个交换机中
             */
            channel.basicConsume(queueName,true,"",new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body);
                    System.out.println(message);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
