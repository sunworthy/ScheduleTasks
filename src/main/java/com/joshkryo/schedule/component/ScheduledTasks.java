package com.joshkryo.schedule.component;

import com.joshkryo.schedule.service.CheckServerStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//    @Scheduled(fixedRate = 5000)
    @Scheduled(cron = "0 0 7,14,23 * * ?")
    public void reportCurrentTime() {
        log.info("task running:The time is now {}", dateFormat.format(new Date()));
        CheckServerStatusService.getInstence().checkServerStatus();
    }
}
