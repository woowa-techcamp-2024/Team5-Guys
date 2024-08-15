package info.logbat.dev.service;

public interface CountTestService {

  void increaseSuccessCount();

  void increaseErrorCount();

  long getSuccessCount();

  long getErrorCount();

  void reset();
}
