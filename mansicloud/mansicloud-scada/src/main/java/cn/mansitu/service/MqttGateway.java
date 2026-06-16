package cn.mansitu.service;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 * MQTT 对外提供发送消息的API时，需要使用@MessagingGateway 注解，去提供一个消息网关代理，参数defaultRequestChannel 指定发送消息绑定的channel。
 * 可以实现三种API接口，payload 为发送的消息，topic 发送消息的主题，qos 消息质量。
 * 配置MqttGateway消息推送接口类，
 * 在sendToMqtt(String data,@Header(MqttHeaders.TOPIC)String topic)接口中，data为发送的消息内容，topic为主题。
 * 指定topic，则我们的接口可以根据需要，向不同的主题发送消息，方便灵活应用。如果不指定，则使用默认配置的主题。
 */
@Service
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {
    void sendToMqtt(String data,@Header(MqttHeaders.TOPIC) String topic);

    void sendDefaultTopicToMqtt(String data);

    // 向默认的 topic 发送消息
    //void sendMessage2Mqtt(String payload);
    // 向指定的 topic 发送消息
    //void sendMessage2Mqtt(String payload,@Header(MqttHeaders.TOPIC) String topic);
    // 向指定的 topic 发送消息，并指定服务质量参数
    //void sendMessage2Mqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);
}

