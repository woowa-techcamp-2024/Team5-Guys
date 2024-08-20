package info.logbat.config;

import static org.assertj.core.api.Assertions.assertThat;

import info.logbat.config.LogbatConfigLoader;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LogbatConfigLoader 테스트")
class LogbatConfigLoaderTest {

    /**
     * jar 배포 시 읽기 전용이기 때문에
     * 파일을 쓰고 삭제하면서 테스트를 진행할 수 없는 점으로
     * 기본적으로 파일이 존재하는 경우에 대한 테스트만 진행
     */
    @DisplayName("application.properties와 application.yml이 모두 존재할 때 yaml 파일을 우선적으로 로드")
    @Test
    void testLoadConfigWithBothFiles() throws Exception {
        // when
        CompositeConfiguration config = LogbatConfigLoader.loadConfig();

        // then
        String appKey = config.getString("logbat.appKey");
        assertThat(appKey).isEqualTo("yamlAppKey");
    }
}
