package com.bjpowernode.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Send {
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
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

            for (int i = 0; i < 100; i++) {
                //获取连接
                connection = factory.newConnection();
                //获取通道
                channel = connection.createChannel();

                /**
                 * 声明一个队列，
                 * 参数 1：队列名取值任意
                 * 参数 2：是否为持久化的队列
                 * 参数 3：是否排外，如果排外这个队列，这个队列只允许一个消费者监听
                 * 参数 4：是否自动删除队列 ，if true 当前队列没有消息也没有消费者连接时结汇自动删除
                 * 参数 5：队列的一些属性设置，normally it should be null.
                 *
                 * Note:
                 *  1：声明队列名称，如果已经存在，则放弃声明，如果队列不存在则会声明一个新的队列
                 *  2：队列名可以取值任意，但是要以消息接受是一致
                 *  3：这行代码是可有可无的，但一定要在发送消息时确认队列名称已经存在 RabbitMQ 中，否则就会 throw Exception
                 */
                channel.queueDeclare("myQueue", true, false, false, null);

                String message = "Chloe Gan is my crush and she will be my girlfriend in year 2021";

                /**
                 *  发送消息到MQ
                 *  参数 1：交换机名称，这里为空字符串，表示不使用交换机
                 *  参数 2：队列名或Routing,当指定了交换机名称后，这个值就是 RoutingKey
                 *  参数 3：消息的属性信息（Normally it should be null）
                 *  参数 4：具体的消息数据的字节数组
                 */

                channel.basicPublish("", "myQueue", null, message.getBytes(StandardCharsets.UTF_8));

                System.out.println("Message sent.");
            }


        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (channel.isOpen()) {
                channel.close();
            }
            if (connection.isOpen()) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
