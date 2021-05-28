package com.bjpowernode.rabbitmq.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
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

            channel.queueDeclare("MyDirectQueue",true,false,false,null);

            channel.exchangeDeclare("directExchange","direct",true);

            channel.queueBind("MyDirectQueue","directExchange","directRoutingKey");

            /**
             * 监听某个队列并获取队列中的数据
             * NOte:
             *      当前监听队列必须已经存在，并正确的绑定到了某个交换机中
             */
            channel.basicConsume("MyDirectQueue",true,"",new DefaultConsumer(channel){
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
