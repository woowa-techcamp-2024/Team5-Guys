package info.logbat.dev.service;

import java.util.concurrent.atomic.AtomicLong;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountTestService {

  private final AtomicLong successCount = new AtomicLong(0L);
  private final AtomicLong errorCount = new AtomicLong(0L);

  public void increaseSuccessCount() {
    successCount.incrementAndGet();
  }

  public void increaseErrorCount() {
    errorCount.incrementAndGet();
  }

  public long getSuccessCount() {
    return successCount.get();
  }

  public long getErrorCount() {
    return errorCount.get();
  }

  public void reset() {
    successCount.set(0L);
    errorCount.set(0L);
  }

}
