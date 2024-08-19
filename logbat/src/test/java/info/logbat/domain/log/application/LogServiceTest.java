package info.logbat.domain.log.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import info.logbat.domain.log.application.payload.request.CreateLogServiceRequest;
import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.domain.enums.Level;
import info.logbat.domain.log.repository.LogRepository;
import info.logbat.domain.project.domain.App;
import info.logbat.domain.project.domain.Project;
import info.logbat.domain.project.domain.enums.AppType;
import info.logbat.domain.project.repository.AppJpaRepository;
import info.logbat.domain.project.repository.ProjectJpaRepository;
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

  @Autowired
  private ProjectJpaRepository projectJpaRepository;

  @Autowired
  private AppJpaRepository appJpaRepository;

  @DisplayName("Log를 저장할 수 있다.")
  @Test
  void saveLog() {
    // given
    String 프로젝트_이름 = "테스트_프로젝트";
    Project 프로젝트 = Project.from(프로젝트_이름);

    AppType 앱_타입 = AppType.JAVA;

    App 앱 = App.of(프로젝트, 앱_타입);

    projectJpaRepository.save(프로젝트);
    appJpaRepository.save(앱);

    UUID 앱_키 = 앱.getToken();

    String 로그_레벨 = "INFO";
    String 로그_데이터 = "테스트_로그_데이터";
    LocalDateTime 타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    CreateLogServiceRequest 요청_DTO = new CreateLogServiceRequest(앱_키.toString(), 로그_레벨, 로그_데이터,
        타임스탬프);

    // when
    long 저장된_ID = logService.saveLog(요청_DTO);

    // then
    Optional<Log> 찾은_로그 = logRepository.findById(저장된_ID);

    assertThat(찾은_로그).isPresent()
        .get()
        .extracting("logId", "appKey", "level", "data.value", "timestamp")
        .contains(저장된_ID, 앱_키.toString(), Level.INFO, "테스트_로그_데이터", 타임스탬프);
  }

  @DisplayName("존재하지 않는 Application Key로 Log를 저장할 수 없다.")
  @Test
  void saveLogWithNonExistentAppKey() {
    // given
    String 존재하지_않는_앱_키_문자열 = UUID.randomUUID().toString();
    String 로그_레벨 = "INFO";
    String 로그_데이터 = "테스트_로그_데이터";
    LocalDateTime 타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    CreateLogServiceRequest 요청_DTO = new CreateLogServiceRequest(존재하지_않는_앱_키_문자열, 로그_레벨, 로그_데이터,
        타임스탬프);

    // when & then
    assertThatThrownBy(() -> logService.saveLog(요청_DTO))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("존재하지 않는 Application Key 입니다.");
  }

  @DisplayName("잘못된 형태의 Application Key로 Log를 저장할 수 없다.")
  @Test
  void saveLogWithInvalidAppKey() {
    // given
    String 잘못된_앱_키_문자열 = "잘못된_앱_키_문자열";
    String 로그_레벨 = "INFO";
    String 로그_데이터 = "테스트_로그_데이터";
    LocalDateTime 타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    CreateLogServiceRequest 요청_DTO = new CreateLogServiceRequest(잘못된_앱_키_문자열, 로그_레벨, 로그_데이터,
        타임스탬프);

    // when & then
    assertThatThrownBy(() -> logService.saveLog(요청_DTO))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("잘못된 형식의 Application Key 입니다.");
  }

}