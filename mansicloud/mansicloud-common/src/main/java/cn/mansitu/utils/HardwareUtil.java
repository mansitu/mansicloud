package cn.mansitu.utils;

import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * 服务器硬件信息工具类
 * 使用OSHI库获取硬件唯一标识
 */
public class HardwareUtil {

    /**
     * 获取服务器硬件UUID
     * 仅使用BIOS序列号，无需root权限
     */
    public static String getHardwareUuid() {
        SystemInfo systemInfo = new SystemInfo();

        try {
            ComputerSystem computerSystem = systemInfo.getHardware().getComputerSystem();
            String hardwareUuid = computerSystem.getHardwareUUID();

            if (isNotBlank(hardwareUuid)) {
                return normalizeUuid(hardwareUuid);
            }
        } catch (Exception e) {
            throw new RuntimeException("无法获取硬件UUID，请检查系统权限或联系技术支持", e);
        }

        throw new RuntimeException("无法获取服务器硬件唯一标识（UUID为空）");
    }

    /**
     * 生成机器码（用于RSA签名）
     * 对硬件UUID进行SHA256哈希处理
     */
    public static String generateMachineCode() {
        String hardwareUuid = getHardwareUuid();
        return sha256Hash(hardwareUuid);
    }

    /**
     * SHA256哈希
     */
    private static String sha256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("生成机器码失败", e);
        }
    }

    /**
     * 标准化UUID格式
     */
    private static String normalizeUuid(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return uuid.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    private static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
