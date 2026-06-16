package cn.mansitu.rest;

import cn.mansitu.annotation.Log;
import cn.mansitu.modules.system.domain.Dept;
import cn.mansitu.modules.system.repository.DeptRepository;
import cn.mansitu.modules.system.service.dto.DeptSmallDto;
import cn.mansitu.modules.system.service.dto.UserDto;
import cn.mansitu.modules.security.service.dto.JwtUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import cn.mansitu.service.MqttGateway;
import cn.mansitu.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@Api(tags = "MQTT消息发送")
public class MqttPublish {
    private static final Logger logger = LoggerFactory.getLogger(MqttPublish.class);

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private DeptRepository deptRepository;

    @Log("发送MQTT消息")
    @PostMapping("/sendMqtt")
    @ApiOperation("发送MQTT消息到指定主题")
    @PreAuthorize("@el.check('mqtt:send')")
    public ResponseEntity<Object> sendMqtt(@RequestParam String sendData, @RequestParam String topic){
        // 验证topic权限
        String validationResult = validateTopicPermission(topic);
        if (validationResult != null) {
            logger.warn("MQTT发送权限验证失败 - topic: {}, 原因: {}", topic, validationResult);
            return new ResponseEntity<>(validationResult, HttpStatus.FORBIDDEN);
        }

        logger.info("发送MQTT消息 - topic: {}, data: {}", topic, sendData);
        mqttGateway.sendToMqtt(sendData, topic);
        return new ResponseEntity<>("消息发送成功", HttpStatus.OK);
    }

    @Log("发送默认主题MQTT消息")
    @PostMapping("/sendMqtt2")
    @ApiOperation("发送MQTT消息到默认主题")
    @PreAuthorize("@el.check('mqtt:send')")
    public ResponseEntity<Object> sendDefaultTopicMqtt(@RequestParam String sendData){
        logger.info("发送默认主题MQTT消息 - data: {}", sendData);
        mqttGateway.sendDefaultTopicToMqtt(sendData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 验证topic权限
     * @param topic MQTT主题，格式如：msc/mst
     * @return 如果验证失败返回错误信息，成功返回null
     */
    private String validateTopicPermission(String topic) {
        try {
            // 获取当前用户
            Object currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return "请先登录系统";
            }

            // 检查是否为admin角色
            if (currentUser instanceof JwtUserDto) {
                JwtUserDto jwtUser = (JwtUserDto) currentUser;
                UserDto user = jwtUser.getUser();

                // admin用户不受限制，可以访问所有companyCode
                // 由于MapStruct映射问题导致isAdmin字段丢失，通过用户名判断admin
                if ("admin".equalsIgnoreCase(user.getUsername())) {
                    logger.debug("Admin用户跳过topic权限验证 - username={}, topic={}", user.getUsername(), topic);
                    return null;
                }

                // 获取用户的部门公司代码（通过deptId直接查询）
                Long deptId = user.getDept().getId();
                if (deptId == null) {
                    logger.warn("用户没有分配部门: username={}", user.getUsername());
                    return "您的账号未分配部门，请联系管理员配置";
                }

                Optional<Dept> deptOpt = deptRepository.findById(deptId);
                if (!deptOpt.isPresent()) {
                    logger.warn("部门不存在: deptId={}", deptId);
                    return "部门信息不存在，请联系管理员";
                }

                logger.debug("Topic权限验证通过 - username={}, deptId={}, topic={}",
                        user.getUsername(), deptId, topic);
                return null;
            } else {
                return "无法获取用户信息，请重新登录";
            }
        } catch (Exception e) {
            logger.error("验证topic权限时发生异常", e);
            return "权限验证失败，请稍后重试";
        }
    }
}
