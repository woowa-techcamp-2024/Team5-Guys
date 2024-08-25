package info.logbat.config;

import static org.assertj.core.api.Assertions.assertThat;

import info.logbat.exception.InvalidOptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LogbatConfigLoader는")
class LogBatConfigLoaderTest {

    /*
     * jar 배포 시 읽기 전용이기 때문에 파일을 쓰고 삭제하면서 테스트를 진행할 수 없다.
     * 이에 테스트 선행 조건으로 파일이 존재함을 명시한다.
     */
    @Test
    @DisplayName("설정 파일이 여러개 존재할 때 .yml 파일을 우선적으로 로드한다.")
    void testLoadConfigWithBothFiles() throws InvalidOptionException {
        // Arrange
        LogBatConfig config = LogBatConfigLoader.loadConfig();
        // Act
        String actualResult = config.getValue("appKey");
        // Assert
        assertThat(actualResult).isEqualTo("yamlAppKey");
    }
}
