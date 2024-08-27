package info.logbat.dev.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@Component
public class MemoryUsageLogger {

    private static final Logger logger = LoggerFactory.getLogger(MemoryUsageLogger.class);
    private final MemoryMXBean memoryMXBean;

    public MemoryUsageLogger() {
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    @Scheduled(fixedRate = 1000)
    public void logMemoryUsage() {
        // Heap 메모리 사용량
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long usedHeapMemory = heapMemoryUsage.getUsed();
        long maxHeapMemory = heapMemoryUsage.getMax();
        long committedHeapMemory = heapMemoryUsage.getCommitted();

        // Non-Heap 메모리 사용량
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        long usedNonHeapMemory = nonHeapMemoryUsage.getUsed();
        long maxNonHeapMemory = nonHeapMemoryUsage.getMax();
        long committedNonHeapMemory = nonHeapMemoryUsage.getCommitted();

        // Finalization 대기 객체 수
        int pendingFinalizationCount = memoryMXBean.getObjectPendingFinalizationCount();

        logger.info("Memory Usage [Heap: Used={} MB, Max={} MB, Committed={} MB | Non-Heap: Used={} MB, Max={} MB, Committed={} MB | Pending Finalizations: {}]",
                usedHeapMemory / 1024 / 1024,
                maxHeapMemory / 1024 / 1024,
                committedHeapMemory / 1024 / 1024,
                usedNonHeapMemory / 1024 / 1024,
                maxNonHeapMemory / 1024 / 1024,
                committedNonHeapMemory / 1024 / 1024,
                pendingFinalizationCount);
    }
}
