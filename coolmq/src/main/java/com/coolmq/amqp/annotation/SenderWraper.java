package com.coolmq.amqp.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coolmq.amqp.sender.RabbitSender;
import com.coolmq.amqp.util.MQConstants;
import com.coolmq.amqp.util.RabbitMetaMessage;

/**
 * 描述：封装sender
 *
 * @author fw
 * 创建时间：2017年10月14日 下午10:30:00
 * @version 1.0.0
 */
@Component
@Aspect
public class SenderWraper {
    private Logger logger = LoggerFactory.getLogger(SenderWraper.class);

    @Autowired
    private RabbitSender rabbitSender;

    /**
     * 定义注解类型的切点，只要方法上有该注解，都会匹配
     */
    @Pointcut("@annotation(com.coolmq.amqp.annotation.MqSender)")
    public void annotationSender() {
    }

    @Around("annotationSender()&& @annotation(args)")
    public Object sendMsg(ProceedingJoinPoint joinPoint, MqSender args) throws Throwable {

        String exchange = args.exchange();
        String routingKey = args.routingKey();
        String payload = args.payload();
        /*annotaton中的exchange和queue不得为空 */
        if (exchange.isEmpty() || routingKey.isEmpty()) {
            logger.error("MqSender args is null");
        }


        /*执行业务函数 */
        Object returnObj = joinPoint.proceed();
        if (returnObj == null) {
            returnObj = MQConstants.BLANK_STR;
        }

        /* 生成一个发送对象 */
        RabbitMetaMessage rabbitMetaMessage = new RabbitMetaMessage();
        /*设置交换机 */
        rabbitMetaMessage.setExchange(exchange);
        /*指定routing key */
        rabbitMetaMessage.setRoutingKey(routingKey);
        /* 设置需要传递的消息体,可以是任意对象 */
        rabbitMetaMessage.setPayload(payload.isEmpty() ? returnObj : payload);

        /* 发送消息 */
        try {
            rabbitSender.send(rabbitMetaMessage);
        } catch (Exception e) {
            logger.error("消息发送异常" + e.toString());
            throw e;
        }
        return returnObj;
    }
}
