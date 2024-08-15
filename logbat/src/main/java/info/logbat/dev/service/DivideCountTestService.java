package info.logbat.dev.service;

import info.logbat.dev.util.ThreadLocalLongAdder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class DivideCountTestService implements CountTestService {

  private final ThreadLocalLongAdder successCount = new ThreadLocalLongAdder();
  private final ThreadLocalLongAdder errorCount = new ThreadLocalLongAdder();

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
    return successCount.get();
  }

  @Override
  public long getErrorCount() {
    return errorCount.get();
  }

  @Override
  public void reset() {
    successCount.reset();
    errorCount.reset();
  }
}

