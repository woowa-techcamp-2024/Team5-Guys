package info.logbat.dev.aop;

import info.logbat.dev.service.CountTestService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CountTestAspect {

  private final CountTestService countTestService;

  @Pointcut("@annotation(info.logbat.dev.aop.CountTest)")
  public void countTestPointcut() {
  }

  @Around("countTestPointcut()")
  public Object countTest(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      Object proceed = joinPoint.proceed();
      countTestService.increaseSuccessCount();

      return proceed;
    } catch (Throwable e) {
      countTestService.increaseErrorCount();
      throw e;
    }
  }

}
