package com.bjpowernode.rabbitmq.transaction;

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

            channel.queueDeclare("transactionQueue", true, false, false, null);

            channel.exchangeDeclare("directTransactionExchange", "direct", true);

            channel.queueBind("transactionQueue", "directTransactionExchange", "transactionRoutingKey");

            String message = "Transaction Message test.";

            /**
             * 启动事务（Transaction），启动事务以后所有写入到队列的消息必须显示的调用 txCommit() 提交事务或  txRollback() 回滚事务
             */
            channel.txSelect();
            channel.basicPublish("directTransactionExchange", "transactionRoutingKey", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(10/0);
            channel.basicPublish("directTransactionExchange", "transactionRoutingKey", null, message.getBytes(StandardCharsets.UTF_8));
            /**
             * 提交事务，如果我们调用txSelect() 启动了事务，必须显示调用事务的提交
             * 否则消息不会写入队列，提交事务以后会将内存中的消息写入队列并释放内存
             */
            channel.txCommit();

            System.out.println("Transaction message sent.");

            //回滚事务，放弃当前事务中所有没有提交的消息，释放内存
            channel.txRollback();

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
