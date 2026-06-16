package cn.mansitu.config;

import cn.hutool.core.util.RandomUtil;
import cn.mansitu.MqttHandler.RawDataHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * 〈MQTT接收消息处理〉
 * <p>
 * ServiceActivator注解表明当前方法用于处理MQTT消息，inputChannel 参数指定了用于接收消息的channel
 */
@Configuration
@IntegrationComponentScan
public class MqttInbandConfig {
    private static final Logger logger = LoggerFactory.getLogger(MqttInbandConfig.class);

    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Value("${spring.mqtt.url}")
    private String hostUrl;

    @Value("${spring.mqtt.client.id}")
    private String clientId;

    @Value("${spring.mqtt.default.topic}")
    private String defaultTopic;

    @Value("${spring.mqtt.completionTimeout}")
    private int completionTimeout;   //连接超时


    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{hostUrl});
        mqttConnectOptions.setConnectionTimeout(10);  // 设置超时时间 单位为秒
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        mqttConnectOptions.setKeepAliveInterval(20);
        mqttConnectOptions.setAutomaticReconnect(true);  // 自动重连
        //网络断开后，客户端会进行重连，但是重连之前订阅的主题就失效了，不再接受之前订阅主题的消息。
        //因为将cleanSession设为true，当客户端掉线时，服务器端会清除客户端session。重连后，客户端会有一个新的session。
        /////把配置里的 cleanSession 设为false，客户端掉线后 服务器端不会清除session，当重连后可以接收之前订阅主题的消息。当客户端上线后会接受到它离线的这段时间的消息，clientId 是不能更改的
        mqttConnectOptions.setCleanSession(true);
        return mqttConnectOptions;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    //接收通道
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    //配置client,监听的topic
    @Bean
    public MessageProducer inbound() {
        Integer random = RandomUtil.randomInt(99999);
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + random, mqttClientFactory(),
                        defaultTopic.split(","));

        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        logger.info("MQTT Client Started");
        return adapter;
    }

    //通过通道获取数据
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Autowired
            RawDataHandler rawDataHandler;

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
                //获取mqtt消息的payload并解析
                JSONObject mqttpayload;
                try {
                    mqttpayload = JSON.parseObject((String) message.getPayload());
                } catch (JSONException e) {
                    logger.error("收到错误信息，json解析失败：" + message.getPayload());
                    return;
                }

                if (topic != null && topic.startsWith("msc/")) {
                    logger.debug("收到 msc/ 开头的消息，topic: " + topic);
                    try {
                        rawDataHandler.handleMessage(message);
                    } catch (Exception e) {
                        logger.error("处理 msc/ 消息失败", e);
                    }
                } else {
                    logger.error("收到无效topic：" + topic);
                }
            }
        };
    }
}
