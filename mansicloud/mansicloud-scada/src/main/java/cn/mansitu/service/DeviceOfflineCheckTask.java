package cn.mansitu.service;

import cn.mansitu.domain.Device;
import cn.mansitu.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Device 离线检测定时任务
 * 每3分钟执行一次，检查设备的 update_time
 * 如果超过3分钟未更新，将 online 置为 0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceOfflineCheckTask {

    private final DeviceRepository deviceRepository;

    /**
     * 定时检查设备离线状态
     * 每3分钟执行一次
     */
    private static final int OFFLINE_THRESHOLD_MINUTES = 3;

    @Scheduled(fixedDelay = 3 * 60 * 1000, initialDelay = 5 * 1000)
    @Transactional(rollbackFor = Exception.class)
    public void checkAndSetOffline() {
        log.info("开始执行设备离线检测任务...");

        try {
            // 计算3分钟前的时间点
            LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(OFFLINE_THRESHOLD_MINUTES);
            Timestamp threshold = Timestamp.valueOf(thresholdTime);

            // 批量更新所有超过3分钟未更新的在线设备为离线状态
            int updatedCount = deviceRepository.batchSetOfflineByUpdateTime(threshold);

            if (updatedCount == 0) {
                log.info("没有需要设置为离线的设备");
            } else {
                log.info("设备离线检测任务执行完成，共设置 {} 个设备为离线状态", updatedCount);
            }

        } catch (Exception e) {
            log.error("设备离线检测任务执行失败，阈值时间: {}", 
                    LocalDateTime.now().minusMinutes(OFFLINE_THRESHOLD_MINUTES), e);
        }
    }
}
