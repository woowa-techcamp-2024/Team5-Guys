package info.logbat.dev.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocalLongAdder {

  private final ThreadLocal<Counter> localCounter = new ThreadLocal<>();

  /**
   * ConcurrentHashMap을 사용해야하는 이유
   * <p>
   * 두 Thread가 같은 hashCode를 가지고 동시에 새 Counter를 추가하려 할 때, 한 Thread의 Counter가 다른 Thread의 Counter로 덮어쓰일
   * 가능성이 있습니다.
   */
  private final Map<Thread, Counter> activeCounters = new ConcurrentHashMap<>(200);
  private long value = 0L;

  public void increment() {
    Counter counter = localCounter.get();

    // Thread 내의 Counter가 없으면, 새로운 Counter를 생성하여 ThreadLocal과 activeCounters에 추가
    if (counter == null) {
      counter = new Counter();
      localCounter.set(counter);
      activeCounters.put(Thread.currentThread(), counter);
    }

    // Thread 내의 Counter에 증가연산을 수행하여, 경쟁상태가 발생하지 않음
    counter.increaseValue();
  }

  public long get() {
    flush();
    return value;
  }

  // reset 시에도 activeCounters를 유지하여 reset 후에도 추적 가능함
  public void reset() {
    flush();
    value = 0L;
  }

  private void flush() {
    synchronized (activeCounters) {
      for (Counter counter : activeCounters.values()) {
        value += counter.getValue();
        counter.resetValue();
      }
    }
  }


  private static class Counter {

    private long value;

    public void increaseValue() {
      value++;
    }

    public long getValue() {
      return value;
    }

    public void resetValue() {
      value = 0;
    }

  }
}