package info.logbat.dev.service;

import java.util.concurrent.atomic.LongAdder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class LongAdderCountTestService implements CountTestService {

  private final LongAdder successCount = new LongAdder();
  private final LongAdder errorCount = new LongAdder();

  @Override
  public void increaseSuccessCount() {
    successCount.increment();
  }

  @Override
  public void increaseErrorCount() {
    errorCount.increment();
  }

  @Override
  public long getSuccessCount() {
    return successCount.sum();
  }

  @Override
  public long getErrorCount() {
    return errorCount.sum();
  }

  @Override
  public void reset() {
    successCount.reset();
    errorCount.reset();
  }
}
