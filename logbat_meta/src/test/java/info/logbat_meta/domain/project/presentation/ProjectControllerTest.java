package info.logbat_meta.domain.project.presentation;

import info.logbat_meta.domain.common.ControllerTestSupport;
import info.logbat_meta.domain.project.presentation.payload.response.ProjectCommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectController는")
class ProjectControllerTest extends ControllerTestSupport {

    private final Long expectedId = 1L;
    private final String expectedName = "projectName";
    private final ProjectCommonResponse projectCommonResponse = mock(ProjectCommonResponse.class);
    private final LocalDateTime expectedCreatedAt = LocalDateTime.of(2024, 8, 15, 12, 1, 2, 3);

    @Nested
    @DisplayName("GET /v1/projects/{id}에 대해")
    class describeGet {

        @Test
        @DisplayName("프로젝트 아이디로 프로젝트를 조회할 수 있다.")
        void willReturnProjectInformation() throws Exception {
            // Arrange
            given(projectService.getProjectById(expectedId)).willReturn(projectCommonResponse);
            given(projectCommonResponse.id()).willReturn(expectedId);
            given(projectCommonResponse.name()).willReturn(expectedName);
            given(projectCommonResponse.createdAt()).willReturn(expectedCreatedAt);
            // Act
            MockHttpServletRequestBuilder get = get("/v1/projects/{id}", expectedId);
            // Assert
            mockMvc.perform(get)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").value(expectedId))
                .andExpect(jsonPath("$.data.name").value(expectedName))
                .andExpect(jsonPath("$.data.createdAt").value(expectedCreatedAt.toString()))
                .andExpect(jsonPath("$.data.updatedAt").doesNotExist());
        }

    }

    @Nested
    @DisplayName("POST /v1/projects에 대해")
    class describePost {

        @Test
        @DisplayName("프로젝트를 생성할 수 있다.")
        void willCreateProject() throws Exception {
            // Arrange
            given(projectService.createProject(expectedName)).willReturn(projectCommonResponse);
            given(projectCommonResponse.id()).willReturn(expectedId);
            given(projectCommonResponse.name()).willReturn(expectedName);
            given(projectCommonResponse.createdAt()).willReturn(expectedCreatedAt);
            // Act
            MockHttpServletRequestBuilder post = post("/v1/projects")
                .contentType("application/json")
                .content("{\"name\":\"" + expectedName + "\"}");
            // Assert
            mockMvc.perform(post)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").value(expectedId))
                .andExpect(jsonPath("$.data.name").value(expectedName))
                .andExpect(jsonPath("$.data.createdAt").value(expectedCreatedAt.toString()))
                .andExpect(jsonPath("$.data.updatedAt").doesNotExist());
        }

    }

    @Nested
    @DisplayName("PUT /v1/projects/{id}에 대해")
    class describePut {

        @Test
        @DisplayName("프로젝트를 수정할 수 있다.")
        void willUpdateProject() throws Exception {
            // Arrange
            given(projectService.updateProjectValues(expectedId, expectedName)).willReturn(
                projectCommonResponse);
            given(projectCommonResponse.id()).willReturn(expectedId);
            given(projectCommonResponse.name()).willReturn(expectedName);
            given(projectCommonResponse.createdAt()).willReturn(expectedCreatedAt);
            // Act
            MockHttpServletRequestBuilder put = put("/v1/projects/{id}", expectedId)
                .contentType("application/json")
                .content("{\"name\":\"" + expectedName + "\"}");
            // Assert
            mockMvc.perform(put)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").value(expectedId))
                .andExpect(jsonPath("$.data.name").value(expectedName))
                .andExpect(jsonPath("$.data.createdAt").value(expectedCreatedAt.toString()))
                .andExpect(jsonPath("$.data.updatedAt").doesNotExist());
        }

    }

    @Nested
    @DisplayName("DELETE /v1/projects/{id}에 대해")
    class describeDelete {

        @Test
        @DisplayName("프로젝트를 삭제할 수 있다.")
        void willDeleteProject() throws Exception {
            // Arrange
            given(projectService.deleteProject(any(Long.class))).willReturn(expectedId);
            // Act
            MockHttpServletRequestBuilder delete = delete("/v1/projects/{id}", expectedId);
            // Assert
            mockMvc.perform(delete)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").value(expectedId));
        }

    }

}