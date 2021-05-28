package com.bjpowernode.rabbitmq.confirm;

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

            channel.queueDeclare("confirmQueue",true,false,false,null);

            channel.exchangeDeclare("directConfirmExchange","direct",true);

            channel.queueBind("confirmQueue","directConfirmExchange","directRoutingKey");

            //启动事务
            channel.txSelect();

            /**
             * 接收消息
             * 参数 2：消息的确认机制
             */
            channel.basicConsume("confirmQueue",false,"",new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {




                    //获取当前消息是否被接收过 true == 消息被接收过，false == 消息还没被接收过
                    //因此我们要防止消息重复处理
                    Boolean isRedelivered = envelope.isRedeliver();

                    //获取当前内部类的通道
                    Channel channel1 = this.getChannel();

                    if (!isRedelivered){
                        String message = new String(body);
                        System.out.println(message);

                        //获取消息编号，我们需要消息编号来确认消息
                        long tag = envelope.getDeliveryTag();

                        //手动确认消息，确认后表示当前消息已经成功处理了，需要从队列中移除
                        //这个方法应该在当前消息完全处理程序后执行
                        //参数 1：消息序号
                        //参数 2：是否确认多个，true == 需要确认小于等于当前编号的所有消息，false == 单个确认，只确认当前消息
                        channel1.basicAck(tag,true);
                    }else{
                        //程序到这里表示消息曾经被接受过，需要进行防重处理
                        //例如查询数据库中是否已经添加记录或已经修改过了记录
                        //如果经过判断这条没有被处理完成，则需要重新处理然后确认掉者消息
                        //如果已经处理则直接确认消息即可，不需要进行其他操作处理
                    }



                    //Note: 如果启动了事务，而消息消费者确认模式为手动确认那么必须要提交事务，否则即使调用了方法
                    //      消息也不会从队列中移除
                    channel1.txCommit();


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
