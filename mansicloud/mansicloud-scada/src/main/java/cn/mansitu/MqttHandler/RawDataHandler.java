package cn.mansitu.MqttHandler;

import cn.mansitu.domain.Device;
import cn.mansitu.domain.DeviceType;
import cn.mansitu.domain.RawData;
import cn.mansitu.modules.system.repository.DeptRepository;
import cn.mansitu.repository.DeviceRepository;
import cn.mansitu.repository.DeviceTypeRepository;
import cn.mansitu.repository.RawDataRepository;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: zhaodan
 * Date: 2023/4/26
 * Time: 下午3:00
 */
@Component
public class RawDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(RawDataHandler.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;


    @Autowired
    private RawDataRepository rawDataRepository;

    private boolean checkDeviceTypeExists(String devtype) {
        try {
            Optional<DeviceType> existingDeviceType = deviceTypeRepository.findByTypeCode(devtype);

            if (existingDeviceType.isPresent()) {
                logger.debug("设备类型存在（数据库）: typeCode={}", devtype);
                return true;
            }

            logger.warn("设备类型不存在，跳过设备创建: typeCode={}", devtype);
            return false;

        } catch (Exception e) {
            logger.error("检查设备类型失败: typeCode={}", devtype, e);
            return false;
        }
    }

    private void checkAndAutoCreateOrUpdateDevice(String devtype, String deviceName) {
        try {
            Optional<Device> existingDevice = Optional.ofNullable(deviceRepository.findByDeviceCode(deviceName));

            if (existingDevice.isPresent()) {
                Device device = existingDevice.get();
                device.setStatus(1);
                device.setOnline(1);
                device.setUpdateTime(new Timestamp(System.currentTimeMillis()));

                deviceRepository.save(device);

                logger.debug("设备状态已更新（数据库）: deviceCode={}", deviceName);
                return;
            }

            // 获取设备类型的部门ID
            Long deptId = null;
            Optional<DeviceType> deviceTypeOpt = deviceTypeRepository.findByTypeCode(devtype);
            if (deviceTypeOpt.isPresent()) {
                deptId = deviceTypeOpt.get().getDeptId();
                logger.debug("从设备类型获取部门ID: typeCode={}, deptId={}", devtype, deptId);
            }

            Device newDevice = new Device();
            newDevice.setDeviceCode(deviceName);
            newDevice.setDeviceName(deviceName);
            newDevice.setTypeCode(devtype);
            newDevice.setDeptId(deptId);
            newDevice.setStatus(1);
            newDevice.setOnline(1);
            newDevice.setCreateTime(new Timestamp(System.currentTimeMillis()));
            newDevice.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            newDevice.setRemark("自动创建");
            deviceRepository.save(newDevice);

            logger.info("自动创建设备并设置为在线: typeCode={}, deviceCode={}, deptId={}",
                    devtype, deviceName, deptId);

        } catch (Exception e) {
            logger.error("自动处理设备失败: deviceCode={}", deviceName, e);
        }
    }

    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            String payload = message.getPayload().toString();

            JSONObject json = JSONObject.parseObject(payload);

            List<String> deviceNames = extractDeviceNames(json);
            for (String fullDeviceName : deviceNames) {
                RawData data = new RawData();
                data.setTs(new Timestamp(System.currentTimeMillis()));

                String deviceName = fullDeviceName;

                String devtype = null;
                if (fullDeviceName != null && !fullDeviceName.isEmpty()) {
                    String[] parts = fullDeviceName.split("-");
                    if (parts.length >= 2) {
                        devtype = parts[parts.length - 2];
                    }
                    else {
                        continue;
                    }
                }

                if (devtype == null || deviceName == null) {
                    logger.warn("设备数据不完整，跳过处理 - devtype: {}, deviceName: {}",
                        devtype, deviceName);
                    continue;
                }

                if (!checkDeviceTypeExists(devtype)) {
                    logger.warn("设备类型不存在，跳过处理 - devtype: {}", devtype);
                    continue;
                }

                checkAndAutoCreateOrUpdateDevice(devtype, deviceName);

                data.setDeviceId(devtype);
                data.setDeviceName(deviceName);

                JSONObject deviceJson = extractDeviceValues(json, deviceName);
                if (deviceJson == null) {
                    logger.warn("设备JSON数据为空，跳过处理 - deviceName: {}", deviceName);
                    continue;
                }
                data.setV1(getSafeValue(deviceJson, "v1"));
                data.setV2(getSafeValue(deviceJson, "v2"));
                data.setV3(getSafeValue(deviceJson, "v3"));
                data.setV4(getSafeValue(deviceJson, "v4"));
                data.setV5(getSafeValue(deviceJson, "v5"));
                data.setV6(getSafeValue(deviceJson, "v6"));
                data.setV7(getSafeValue(deviceJson, "v7"));
                data.setV8(getSafeValue(deviceJson, "v8"));
                data.setV9(getSafeValue(deviceJson, "v9"));
                data.setV10(getSafeValue(deviceJson, "v10"));
                data.setV11(getSafeValue(deviceJson, "v11"));
                data.setV12(getSafeValue(deviceJson, "v12"));
                data.setV13(getSafeValue(deviceJson, "v13"));
                data.setV14(getSafeValue(deviceJson, "v14"));
                data.setV15(getSafeValue(deviceJson, "v15"));
                data.setV16(getSafeValue(deviceJson, "v16"));
                data.setV17(getSafeValue(deviceJson, "v17"));
                data.setV18(getSafeValue(deviceJson, "v18"));
                data.setV19(getSafeValue(deviceJson, "v19"));
                data.setV20(getSafeValue(deviceJson, "v20"));

                try {
                    rawDataRepository.save(data);
                    logger.debug("原始数据已入库 - deviceName: {}", deviceName);
                } catch (Exception e) {
                    logger.error("原始数据入库失败 - deviceName: {}", deviceName, e);
                }
            }
        } catch (Exception e) {
            logger.error("MQTT接收异常", e);
        }
    }

    /**
     * 提取设备数据对象，兼容新旧两种格式
     * 旧格式：{"设备-01": {"v1":220.5,"v2":18.2}}
     * 新格式：{"设备-01": [{"values":{"v1":220.5,"v2":18.2}}]}
     *
     * @param json 完整JSON对象
     * @param deviceName 设备名称
     * @return 设备数据JSONObject，如果解析失败返回null
     */
    private JSONObject extractDeviceValues(JSONObject json, String deviceName) {
        try {
            Object deviceData = json.get(deviceName);
            if (deviceData == null) {
                return null;
            }

            // 判断是新格式（JSONArray）还是旧格式（JSONObject）
            if (deviceData instanceof JSONArray) {
                // 新格式：数组包含一个对象，对象中有values字段
                JSONArray array = (JSONArray) deviceData;
                if (array.isEmpty()) {
                    logger.warn("设备数据数组为空 - deviceName: {}", deviceName);
                    return null;
                }

                Object firstElement = array.get(0);
                if (firstElement instanceof JSONObject) {
                    JSONObject firstObj = (JSONObject) firstElement;
                    // 尝试获取values字段
                    JSONObject values = firstObj.getJSONObject("values");
                    if (values != null) {
                        return values;
                    }
                    // 如果没有values字段，直接返回第一个对象
                    return firstObj;
                }
                return null;
            } else if (deviceData instanceof JSONObject) {
                // 旧格式：直接是对象
                return (JSONObject) deviceData;
            }

            return null;
        } catch (Exception e) {
            logger.error("提取设备数据失败 - deviceName: {}", deviceName, e);
            return null;
        }
    }

    /**
     * 从 JSON 数据的第一级 key 中提取设备名称，排除以 "__" 开头的 key
     *
     * @return 包含所有设备名称的列表
     */
    private List<String> extractDeviceNames(JSONObject json) {
        List<String> deviceNames = new ArrayList<>();
        try {
            logger.info("设备列表:");
            for (String key : json.keySet()) {
                if (key != null && !key.startsWith("__")) {
                    deviceNames.add(key);
                    logger.info("  - " + key);
                }
            }
        } catch (Exception e) {
            logger.error("提取设备列表失败", e);
        }
        return deviceNames;
    }

    private Float getSafeValue(JSONObject json, String key) {
        try {
            if (!json.containsKey(key)) return null;
            Object val = json.get(key);
            if (val == null) return null;

            if (val instanceof Number) {
                return ((Number) val).floatValue();
            }
            if (val instanceof Boolean) {
                return (Boolean) val ? 1.0F : 0.0F;
            }
            if (val instanceof String) {
                return Float.parseFloat(val.toString().trim());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
