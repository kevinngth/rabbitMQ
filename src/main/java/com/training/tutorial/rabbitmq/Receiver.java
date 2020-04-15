package com.training.tutorial.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {

    private CountDownLatch latch = new CountDownLatch(1);

    @RabbitListener(queues="queue")
    public void receiveMessage(String message) {
        Logger logger = LoggerFactory.getLogger(Receiver.class);
        message = String.format( "Receiver Received <%s>", message );
        logger.info(message);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}