package com.ddnconsulting.chatserver.async;

import javax.annotation.Resource;

import com.ddnconsulting.chatserver.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * Publishes new message events to Redis topic.
 *
 * @author Dan Nathanson
 */
public class NewMessageProducer {
    private static final Logger LOG = LoggerFactory.getLogger(NewMessageProducer.class);

    ObjectMapper objectMapper;

    @Resource(name = "messageTemplate")
    RedisTemplate messageTemplate;


    @Autowired
    public NewMessageProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Publish message to Redis topic
     *
     * Note: Implementation is not ideal. Should be able to just pass Message object to convertAndSend() method
     * directly and Jackson serializer should be called behind the scenes, but I can't get it working at the
     * moment and it's not really what this exercise is about.
     */
    public void publish(Message message) {
        try {
            messageTemplate.convertAndSend("messages", objectMapper.writeValueAsString(message));
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize Message [" + message + "] as String", e);
        }
    }
}
