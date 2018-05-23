package com.itmuch.cloud.study.provider;

import com.coolmq.amqp.annotation.MqSender;
import com.coolmq.amqp.sender.RabbitSender;
import com.coolmq.amqp.util.MQConstants;
import com.coolmq.amqp.util.RabbitMetaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


@RestController
public class MessageProviderController {

    /**
     * test if can get value from config center
     **/
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqAddress;

    @Autowired
    private RabbitSender rabbitSender;

    @Autowired
    private EntityManager entityManager;

    private Random mock = new Random();

    Logger logger = LoggerFactory.getLogger(getClass());


    @GetMapping("testconfig")
    public String testconfig() {
        return rabbitmqAddress;
    }

    @GetMapping("testmq")
    @Transactional
    public String testMessage() throws Exception {
        /* 生成一个发送对象 */
        RabbitMetaMessage rabbitMetaMessage = new RabbitMetaMessage();
        /*设置交换机 */
        rabbitMetaMessage.setExchange(MQConstants.BUSINESS_EXCHANGE);
        /*指定routing key */
        rabbitMetaMessage.setRoutingKey(MQConstants.BUSINESS_KEY);
        /* 设置需要传递的消息体,可以是任意对象 */
        rabbitMetaMessage.setPayload("the message you want to send");

        //do some biz
        String result = insetUser();

        /* 发送消息 */
        rabbitSender.send(rabbitMetaMessage);

        return result;
    }

    @GetMapping("testMQWithAnnotation")
    @Transactional
    @MqSender(exchange = MQConstants.BUSINESS_EXCHANGE, routingKey = MQConstants.BUSINESS_KEY)
    public String testMQWithAnnotation() {
        //do some biz
        return insetUser();
    }

    private String insetUser() {
        String id = UUID.randomUUID().toString().replace("-", "");
        Query query = entityManager.createNativeQuery("insert into user values(?,'zhangsan',?)");
        query.setParameter(1, id);
        query.setParameter(2, mock.nextInt(100));
        query.executeUpdate();
        return "######insert " + id + " created at " + new Date().toString();
    }
}
