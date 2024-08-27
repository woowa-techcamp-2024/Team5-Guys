package info.logbat_meta.domain.project.presentation;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import info.logbat_meta.domain.common.ControllerTestSupport;
import info.logbat_meta.domain.project.domain.enums.AppType;
import info.logbat_meta.domain.project.presentation.payload.response.AppCommonResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppController는")
class AppControllerTest extends ControllerTestSupport {

    private final AppCommonResponse expectedAppCommonResponse = mock(AppCommonResponse.class);
    private final Long expectedId = 1L;
    private final Long expectedProjectId = 1L;
    private final AppType expectedAppType = AppType.JAVA;
    private final UUID expectedToken = UUID.randomUUID();
    private final LocalDateTime expectedCreatedAt = LocalDateTime.of(2024, 8, 15, 12, 1, 2, 3);

    @BeforeEach
    void init() {
        given(expectedAppCommonResponse.getId()).willReturn(expectedId);
        given(expectedAppCommonResponse.getProjectId()).willReturn(expectedProjectId);
        given(expectedAppCommonResponse.getName()).willReturn("앱 이름");
        given(expectedAppCommonResponse.getAppType()).willReturn(expectedAppType.name());
        given(expectedAppCommonResponse.getToken()).willReturn(expectedToken.toString());
        given(expectedAppCommonResponse.getCreatedAt()).willReturn(expectedCreatedAt);
    }

    @Nested
    @DisplayName("GET에 대해")
    class describeGetApps {

        @Test
        @DisplayName("/v1/projects/{projectId}/apps 요청시 프로젝트 ID로 앱 목록을 조회할 수 있다.")
        void willReturnAppList() throws Exception {
            // Arrange
            given(appService.getAppsByProjectId(expectedProjectId)).willReturn(
                List.of(expectedAppCommonResponse));
            MockHttpServletRequestBuilder get = get("/v1/projects/{projectId}/apps",
                expectedProjectId);
            // Act & Assert
            mockMvc.perform(get)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data[0].id").value(expectedId))
                .andExpect(jsonPath("$.data[0].projectId").value(expectedProjectId))
                .andExpect(jsonPath("$.data[0].appType").value(expectedAppType.name()))
                .andExpect(jsonPath("$.data[0].token").value(expectedToken.toString()))
                .andExpect(jsonPath("$.data[0].createdAt").value(expectedCreatedAt.toString()));
        }

        @Test
        @DisplayName("/v1/projects/{projectId}/apps/{id} 요청시 앱 ID로 앱 정보를 조회할 수 있다.")
        void willReturnAppInformation() throws Exception {
            // Arrange
            given(appService.getAppById(expectedId)).willReturn(expectedAppCommonResponse);
            MockHttpServletRequestBuilder get = get("/v1/projects/{projectId}/apps/{id}",
                expectedProjectId, expectedId);
            // Act & Assert
            mockMvc.perform(get)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").value(expectedId))
                .andExpect(jsonPath("$.data.projectId").value(expectedProjectId))
                .andExpect(jsonPath("$.data.appType").value(expectedAppType.name()))
                .andExpect(jsonPath("$.data.token").value(expectedToken.toString()))
                .andExpect(jsonPath("$.data.createdAt").value(expectedCreatedAt.toString()));
        }

    }

    @Nested
    @DisplayName("POST에 대해")
    class describePostApp {

        @Test
        @DisplayName("/v1/projects/{projectId}/apps 요청시 앱을 생성할 수 있다.")
        void willCreateApp() throws Exception {
            // Arrange
            String expectedAppName = "앱 이름";
            given(
                appService.createApp(expectedProjectId, expectedAppName, expectedAppType.name()))
                .willReturn(expectedAppCommonResponse);
            MockHttpServletRequestBuilder post = post("/v1/projects/{projectId}/apps",
                expectedProjectId)
                .contentType("application/json")
                .content("""
                    {
                        "name": "앱 이름",
                        "appType": "JAVA"
                    }
                    """);
            // Act & Assert
            mockMvc.perform(post)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").value(expectedId))
                .andExpect(jsonPath("$.data.projectId").value(expectedProjectId))
                .andExpect(jsonPath("$.data.appType").value(expectedAppType.name()))
                .andExpect(jsonPath("$.data.token").value(expectedToken.toString()))
                .andExpect(jsonPath("$.data.createdAt").value(expectedCreatedAt.toString()));
        }

    }

}