package info.logbat.domain.log.repository;

import static org.assertj.core.api.Assertions.assertThat;

import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.domain.enums.Level;
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
@DisplayName("SynchoronousLogRepository 테스트")
class SynchronousLogRepositoryTest {

    @Autowired
    private SynchronousLogRepository synchronousLogRepository;

    private static final String 앱_키_문자열 = UUID.randomUUID().toString();

    @DisplayName("Log를 저장할 수 있다.")
    @Test
    void saveLog() {
        // given
        String 로그_레벨 = "INFO";
        String 로그_데이터 = "테스트_로그_데이터";
        LocalDateTime 타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

        Log 로그 = new Log(앱_키_문자열, 로그_레벨, 로그_데이터, 타임스탬프);

        // when
        long 저장된_ID = synchronousLogRepository.save(로그);

        // then
        assertThat(저장된_ID)
            .isPositive();
    }

    @DisplayName("저장한 Log를 조회할 수 있다.")
    @Test
    void findLog() {
        // given
        String 로그_레벨 = "INFO";
        String 로그_데이터 = "테스트_로그_데이터";
        LocalDateTime 타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

        long 로그_ID = synchronousLogRepository.save(new Log(앱_키_문자열, 로그_레벨, 로그_데이터, 타임스탬프));

        // when
        Optional<Log> 저장된_로그 = synchronousLogRepository.findById(로그_ID);

        // then
        assertThat(저장된_로그).isPresent()
            .get()
            .extracting("logId", "appKey", "level", "data.value", "timestamp")
            .containsExactly(로그_ID, 앱_키_문자열, Level.INFO, "테스트_로그_데이터",
                LocalDateTime.of(2021, 1, 1, 0, 0, 0));
    }

    @DisplayName("없는 Log를 조회하면 빈 Optional을 반환한다.")
    @Test
    void findNonexistentLog() {
        // given
        long 없는_로그_ID = 1L;

        // when
        Optional<Log> 저장된_로그 = synchronousLogRepository.findById(없는_로그_ID);

        // then
        assertThat(저장된_로그).isEmpty();
    }
}