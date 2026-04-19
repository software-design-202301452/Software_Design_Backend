package inu.lecture.software_design.global.backup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CEW-76: DB 백업 정책 스케줄러
 * 매일 새벽 2시 백업 정책 실행 및 로깅
 */
@Slf4j
@Component
public class BackupScheduler {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 매일 새벽 2시 DB 백업 실행
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void runDailyBackup() {
        String now = LocalDateTime.now().format(FORMATTER);
        log.info("[{}] ===== DB 백업 정책 시작 =====", now);

        try {
            executeBackup();
            log.info("[{}] ===== DB 백업 정책 완료 =====", LocalDateTime.now().format(FORMATTER));
        } catch (Exception e) {
            log.error("[{}] DB 백업 정책 실패: {}", LocalDateTime.now().format(FORMATTER), e.getMessage(), e);
        }
    }

    /**
     * 백업 스크립트 실행
     */
    private void executeBackup() throws Exception {
        String backupScript = System.getenv().getOrDefault(
                "BACKUP_SCRIPT_PATH",
                System.getProperty("user.dir") + "/scripts/backup.sh"
        );

        log.info("백업 스크립트 경로: {}", backupScript);

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", backupScript);
        processBuilder.inheritIO();

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            log.info("백업 스크립트 정상 완료 (exitCode={})", exitCode);
        } else {
            log.error("백업 스크립트 비정상 종료 (exitCode={})", exitCode);
            throw new RuntimeException("백업 스크립트 실패 (exitCode=" + exitCode + ")");
        }
    }
}
