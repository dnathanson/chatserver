package com.ddnconsulting.chatserver;

import com.ddnconsulting.chatserver.async.ContactConsumer;
import com.ddnconsulting.chatserver.async.ContactProducer;
import com.ddnconsulting.chatserver.async.NewMessageConsumer;
import com.ddnconsulting.chatserver.async.NewMessageProducer;
import com.ddnconsulting.chatserver.dao.ConnectedSessionDao;
import com.ddnconsulting.chatserver.dao.UserDao;
import com.ddnconsulting.chatserver.session.SessionCheckInterceptor;
import com.ddnconsulting.chatserver.session.SessionMonitor;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * Main application using Spring Boot.  Replacement for application context XML file.  Useful for little POCs like this.
 */
@SpringBootApplication
@EnableScheduling
@EnableWebMvc
public class Application extends WebMvcAutoConfigurationAdapter
{
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    /*
     * The following beans define the Redis async message handing components
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            NewMessageConsumer messageConsumer,
                                            ContactConsumer contactConsumer) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messagesMessageListener(messageConsumer), new PatternTopic("messages"));
        container.addMessageListener(contactsMessageListener(contactConsumer), new PatternTopic("contacts"));

        return container;
    }
    @Bean
    MessageListenerAdapter messagesMessageListener(NewMessageConsumer receiver) {
        return new MessageListenerAdapter(receiver, "handleMessage");
    }

    @Bean
    MessageListenerAdapter contactsMessageListener(ContactConsumer receiver) {
        return new MessageListenerAdapter(receiver, "handleMessage");
    }
    @Bean(name = "messageTemplate")
    RedisTemplate messageTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate messageTemplate = new StringRedisTemplate();
        messageTemplate.setConnectionFactory(connectionFactory);
        return messageTemplate;
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionCheckInterceptor()).excludePathPatterns("/login",
                                                                               "/logout",
                                                                               "/users");

    }

    /*
     * Producer and consumer for handing new chat messages asynchronously
     */
    @Bean
    NewMessageConsumer messageConsumer(ObjectMapper objectMapper, ConnectedSessionDao sessionDao) {
        return new NewMessageConsumer(objectMapper, sessionDao);
    }


    @Bean
    NewMessageProducer messageProducer(ObjectMapper objectMapper) {
        return new NewMessageProducer(objectMapper);
    }

    /*
     * Producer and consumer for handing new contacts asynchronously
     */
    @Bean
    ContactConsumer contactConsumer(ObjectMapper objectMapper, ConnectedSessionDao sessionDao, UserDao userDao) {
        return new ContactConsumer(objectMapper, sessionDao, userDao);
    }

    @Bean
    ContactProducer contactProducer(ObjectMapper objectMapper) {
        return new ContactProducer(objectMapper);
    }

    @Bean
    SessionCheckInterceptor sessionCheckInterceptor() {
        return new SessionCheckInterceptor();
    }

    /*
     * Common Jackson object mapper for JSON serialization/deserialization
     */
    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_DEFAULT);
        return objectMapper;
    }


    @Bean
    SessionMonitor sessionMonitor() {
        return new SessionMonitor();
    }


    public static void main(String[] args) throws JsonProcessingException {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
    }
}