package info.logbat.domain.log.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import info.logbat.domain.log.application.payload.request.CreateLogServiceRequest;
import info.logbat.domain.log.repository.LogRepository;
import info.logbat.domain.project.repository.AppJpaRepository;
import java.time.LocalDateTime;
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
    private AppJpaRepository appJpaRepository;

    @DisplayName("Log를 저장할 수 있다.")
    @Test
    void saveLog() {
        throw new UnsupportedOperationException("아직 구현되지 않았습니다.");/*
        // given
        String 프로젝트_이름 = "테스트_프로젝트";
        Project 프로젝트 = Project.from(프로젝트_이름);

        AppType 앱_타입 = AppType.JAVA;

        App 앱 = App.of(프로젝트, 앱_타입);

        projectJpaRepository.save(프로젝트);
        appJpaRepository.save(앱);

        UUID 앱_키 = 앱.getAppKey();

        String 로그_레벨 = "INFO";
        String 로그_데이터 = "테스트_로그_데이터";
        LocalDateTime 타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

        CreateLogServiceRequest 요청_DTO = new CreateLogServiceRequest(앱_키.toString(), 로그_레벨, 로그_데이터,
            타임스탬프);

        // when & then
        assertThatCode(() -> logService.saveLog(요청_DTO))
            .doesNotThrowAnyException();*/
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
            .hasMessage("앱을 찾을 수 없습니다.");
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
            .hasMessage("앱을 찾을 수 없습니다.");
    }

}