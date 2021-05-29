package com.bjpowernode.rabbitmq.service.impl;


import com.bjpowernode.rabbitmq.service.ReceiveService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReceiveServiceImpl implements ReceiveService {

    @Autowired
    private AmqpTemplate amqpTemplate;


    /**
     * 不建议使用
     */
    @Override
    public void receiveMessage() {
        String message = (String) amqpTemplate.receiveAndConvert("bootDirectQueue");
        System.out.println(message);
    }

    /**
     * @param message 接收到的具体的消息数据
     *                <p>
     *                Note：
     *                如果当前监听方法正常结束，Spring会自动确认消息。
     *                如果出现异常，Spring不会确认消息
     *                因此在消息处理时，我们需要做好消息的防重复处理工作
     * @RabbitListener() 用于标记当前方法时一个RabbitMQ的消息监听方法，Use: 持续性自动接收方法
     * 这个方法不需要手动调用，Spring自动运行监听
     * parameter
     * queues 用于指定已经存在的队列名，用于进行队列的监听
     */
    @RabbitListener(queues = {"bootDirectQueue"})
    @Override
    public void directReceive(String message) {
        System.out.println("Listener received message --- " + message);
    }

    @RabbitListener(bindings = {
            /*完成交换机绑定*/
            @QueueBinding(
                    /*创建一个队列（没有指定参数 = 创建一个随机队列）*/
                    value = @Queue(),
                    /*创建一个交换机*/
                    exchange = @Exchange(
                            name = "fanoutBootExchange",
                            type = "fanout"
                    )
            )
    })
    public void findout02Receive(String message) {
        System.out.println("Fanout01 Listener received message --- " + message);
    }

    @RabbitListener(bindings = {
            /*完成交换机绑定*/
            @QueueBinding(
                    /*创建一个队列（没有指定参数 = 创建一个随机队列）*/
                    value = @Queue(),
                    /*创建一个交换机*/
                    exchange = @Exchange(
                            name = "fanoutBootExchange",
                            type = "fanout"
                    )
            )
    })
    public void findoutReceive(String message) {
        System.out.println("Fanout02 Listener received message --- " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue("topic01"),
                    key = {"product"},
                    exchange = @Exchange(
                            name = "topicBootExchange",
                            type = "topic"
                    )
            )
    })
    public void topicReceive01(String message) {
        System.out.println("Topic Listener 01 [product] received message --- " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue("topic02"),
                    key = {"product.*"},
                    exchange = @Exchange(
                            name = "topicBootExchange",
                            type = "topic"
                    )
            )
    })
    public void topicReceive02(String message) {
        System.out.println("Topic Listener 02 [product.*] received message --- " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue("topic03"),
                    key = {"product.#"},
                    exchange = @Exchange(
                            name = "topicBootExchange",
                            type = "topic"
                    )
            )
    })
    public void topicReceive03(String message) {
        System.out.println("Topic Listener 03 [product.#] received message --- " + message);
    }
}
