package info.logbat.domain.log.application;

import static org.assertj.core.api.Assertions.assertThat;

import info.logbat.domain.log.application.payload.request.CreateLogServiceRequest;
import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.domain.enums.Level;
import info.logbat.domain.log.repository.LogRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("LogService 테스트")
class LogServiceTest {

  @Autowired
  private LogService logService;

  @Autowired
  private LogRepository logRepository;

  private static final String 앱_키_문자열 = UUID.randomUUID().toString();

  @DisplayName("Log를 저장할 수 있다.")
  @Test
  void saveLog() {
    // given
    String 로그_레벨 = "INFO";
    String 로그_데이터 = "테스트_로그_데이터";
    LocalDateTime 타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    CreateLogServiceRequest 요청_DTO = new CreateLogServiceRequest(앱_키_문자열, 로그_레벨, 로그_데이터, 타임스탬프);

    // when
    long 저장된_ID = logService.saveLog(요청_DTO);

    // then
    Optional<Log> 찾은_로그 = logRepository.findById(저장된_ID);

    assertThat(찾은_로그).isPresent()
        .get()
        .extracting("logId", "appKey", "level", "data.value", "timestamp")
        .contains(저장된_ID, 앱_키_문자열, Level.INFO, "테스트_로그_데이터", 타임스탬프);
  }

}