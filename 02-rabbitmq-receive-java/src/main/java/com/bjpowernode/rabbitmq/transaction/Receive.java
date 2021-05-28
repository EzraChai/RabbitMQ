package com.bjpowernode.rabbitmq.transaction;

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

            channel.queueDeclare("transactionQueue",true,false,false,null);

            channel.exchangeDeclare("directTransactionExchange","direct",true);

            channel.queueBind("transactionQueue","directTransactionExchange","transactionRoutingKey");


            /**
             * 开启事务
             * 当消费者开启事务，即使不作为事务的提交 txCommit() ，那么依然可以获取队列中的消息,并且将消息从队列中移除
             * Note:
             *      暂时事务队列接收者没有任何影响
             */
            channel.txSelect();
            channel.basicConsume("transactionQueue",true,"",new DefaultConsumer(channel){
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
