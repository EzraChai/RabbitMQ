package com.bjpowernode.springboot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * 配置一个Direct类型交换机
     * @return
     */
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("bootDirectExchange");
    }

    /**
     * 配置一个队列
     * @return
     */
    @Bean
    public Queue directQueue(){
        return new Queue("bootDirectQueue");
    }


    /**
     * 配置一个队列和交换机的绑定
     * @param directQueue 需要绑定的队列的对象，参数名必须要以某个@Bean方法名完全相同
     * @param directExchange 需要绑定的交换机的对象，参数名必须要以某个@Bean方法名完全相同
     * @return
     */
    @Bean
    public Binding directBinding(Queue directQueue,DirectExchange directExchange){
        //完成绑定
        // 参数 1：需要绑定的队列，
        // 参数 2：需要绑定的交换机，
        // 参数 3：绑定时的RoutingKey
        return BindingBuilder.bind(directQueue).to(directExchange).with("bootDirectRoutingKey");
    }
}
