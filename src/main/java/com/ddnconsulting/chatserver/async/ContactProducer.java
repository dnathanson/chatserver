package com.ddnconsulting.chatserver.async;

import javax.annotation.Resource;

import com.ddnconsulting.chatserver.model.ContactChange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * Publishes new contact events to Redis topic.
 *
 * @author Dan Nathanson
 */
public class ContactProducer {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOG = LoggerFactory.getLogger(ContactProducer.class);

    private ObjectMapper objectMapper;

    @Resource(name = "messageTemplate")
    RedisTemplate messageTemplate;


    @Autowired
    public ContactProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Publish new contact to Redis topic
     *
     * Note: Implementation is not ideal. Should be able to just pass Message object to convertAndSend() method
     * directly and Jackson serializer should be called behind the scenes, but I can't get it working at the
     * moment and it's not really what this exercise is about.
     */
    public void publish(ContactChange contactChange) {
        try {
            messageTemplate.convertAndSend("contacts", objectMapper.writeValueAsString(contactChange));
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize Message [" + contactChange + "] as String", e);
        }
    }
}
