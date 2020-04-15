package com.training.tutorial.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitMQApplication {

	static final String QUEUE_NAME = "queue";
	static final String TOPIC_EXCHANGE_NAME = "topic-exchange";

	@Bean
	Queue queue() { // creates an AMQP queue.
		return new Queue(QUEUE_NAME, false);
	}

	@Bean
	TopicExchange topicExchange() { // creates a topic exchange.
		return new TopicExchange(TOPIC_EXCHANGE_NAME);
		// default exchange: messages routed to queue with name specified by routing key if exist
		// direct exchange: message goes to queue with binding key that matches exactly
		// topic exchange: messages goes to queues with binding keys containing its routing keys. * replaces exactly 1 word, # replaces 0 or more words and . separates them
		// fanout exchange: broadcast all received messages to all queues
		// headers exchange: ignore routing key attribute and uses message headers instead, setting "x-match" to any or all
	}

	@Bean
	Binding binding(Queue queue, TopicExchange topicExchange) { // binds these queue & exchange together,
		return BindingBuilder.bind(queue).to(topicExchange).with("foo.bar.#"); // defining the behavior that occurs when RabbitTemplate publishes to an exchange.
	} // the queue is bound with a routing key of foo.bar.#, which means that any messages sent with a routing key that begins with foo.bar. are routed to the queue.

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
											 MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QUEUE_NAME);
		container.setMessageListener(listenerAdapter); // registered as a message listener in the container. It listens for messages on the spring-boot queue.
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) { // Because the Receiver class is a POJO, it needs to be wrapped in the MessageListenerAdapter,
		return new MessageListenerAdapter(receiver, "receiveMessage"); // where you specify that it invokes receiveMessage.
	}

	public static void main(String[] args) {
		SpringApplication.run(RabbitMQApplication.class, args);
	}
}