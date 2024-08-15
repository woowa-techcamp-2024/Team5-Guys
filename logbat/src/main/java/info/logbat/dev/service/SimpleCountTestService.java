package info.logbat.dev.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class SimpleCountTestService implements CountTestService {

  private final AtomicLong successCount = new AtomicLong(0L);
  private final AtomicLong errorCount = new AtomicLong(0L);

  @Override
  public void increaseSuccessCount() {
    successCount.incrementAndGet();
  }

  @Override
  public void increaseErrorCount() {
    errorCount.incrementAndGet();
  }

  @Override
  public long getSuccessCount() {
    return successCount.get();
  }

  @Override
  public long getErrorCount() {
    return errorCount.get();
  }

  @Override
  public void reset() {
    successCount.set(0L);
    errorCount.set(0L);
  }
}
