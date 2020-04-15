package com.training.tutorial.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private final Receiver receiver;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
        this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Logger logger = LoggerFactory.getLogger(Runner.class);
        logger.info("Sending message..."); // Notice that the template routes the message to the exchange with a routing key of foo.bar.#, which matches the binding.
        rabbitTemplate.convertAndSend(RabbitMQApplication.TOPIC_EXCHANGE_NAME, "foo.bar.qix", "Hello from RabbitMQ! Welcome #1");
        Thread.sleep(2000);
        rabbitTemplate.convertAndSend(RabbitMQApplication.TOPIC_EXCHANGE_NAME, "foo.bar.qix", "Hello from RabbitMQ! Hello #2");
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }

}
