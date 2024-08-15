package info.logbat.dev.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DivideCountTestService implements CountTestService {

  private final ThreadLocal<Counter> localCounter = ThreadLocal.withInitial(Counter::new);

  private final AtomicLong successCount = new AtomicLong(0L);
  private final AtomicLong errorCount = new AtomicLong(0L);

  // ThreadLocal -> Counter 인스턴스
  //                  ^
  //                 /
  // Map.get(Thread)

  // Key가 Thread.currentThread()이기 때문에 경쟁상태가 발생하지 않음
  private final Map<Thread, Counter> activeThreads = new HashMap<>(150);

  public void increaseSuccessCount() {
    Counter counter = localCounter.get();

    // Thread 내의 Counter에 증가연산을 수행하여, 경쟁상태가 발생하지 않음
    counter.increaseSuccessCount();
    activeThreads.putIfAbsent(Thread.currentThread(), counter);
  }

  public void increaseErrorCount() {
    Counter counter = localCounter.get();

    // Thread 내의 Counter에 증가연산을 수행하여, 경쟁상태가 발생하지 않음
    counter.increaseErrorCount();
    activeThreads.putIfAbsent(Thread.currentThread(), counter);
  }

  public long getSuccessCount() {
    flushAllThreadLocals();
    return successCount.get();
  }

  public long getErrorCount() {
    flushAllThreadLocals();
    return errorCount.get();
  }

  public void reset() {
    resetAllThreadLocals();
    successCount.set(0L);
    errorCount.set(0L);
  }

  // 중복 집계되는 경우를 방지하기 위해, flush 시 Counter 객체에 대해서 동기화 처리
  private synchronized void flushAllThreadLocals() {
    activeThreads.values().forEach(counter -> {
      successCount.addAndGet(counter.getSuccessCount());
      errorCount.addAndGet(counter.getErrorCount());
      counter.resetIncreaseCount();
      counter.resetErrorCount();
    });
  }

  private void resetAllThreadLocals() {
    activeThreads.values().forEach(counter -> {
      counter.resetIncreaseCount();
      counter.resetErrorCount();
    });

    activeThreads.clear();
  }

  static class Counter {

    private long successCount;
    private long errorCount;

    public void increaseSuccessCount() {
      successCount++;
    }

    public void increaseErrorCount() {
      errorCount++;
    }

    public long getSuccessCount() {
      return successCount;
    }

    public long getErrorCount() {
      return errorCount;
    }

    public void resetIncreaseCount() {
      successCount = 0L;
    }

    public void resetErrorCount() {
      errorCount = 0L;
    }

  }
}
