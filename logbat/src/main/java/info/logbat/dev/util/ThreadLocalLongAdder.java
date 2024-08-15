package info.logbat.dev.util;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadLocalLongAdder {

  private final ThreadLocal<Counter> localCounter = new ThreadLocal<>();
  private final Queue<WeakReference<Counter>> activeCounters = new ConcurrentLinkedQueue<>();

  private long value = 0L;

  public void increment() {
    Counter counter = localCounter.get();

    // Thread 내의 Counter가 없으면, 새로운 Counter를 생성하여 ThreadLocal과 activeCounters에 추가
    // WeakReference를 사용하여 GC가 Counter를 수거할 수 있도록 함
    if (counter == null) {
      counter = new Counter();
      localCounter.set(counter);
      activeCounters.add(new WeakReference<>(counter));
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
      activeCounters.removeIf(weakCounter -> {
        Counter counter = weakCounter.get();
        if (counter == null) {
          return true; // Thread가 제거되어 WeakReference가 null을 가리키면 해당 엔트리 제거
        }
        value += (counter.getValue());
        counter.resetValue();
        return false;
      });
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