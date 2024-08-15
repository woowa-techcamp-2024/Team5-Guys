package info.logbat.dev.presentation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import info.logbat.domain.common.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("카운트 조회 API 테스트")
class CountTestControllerTest extends ControllerTestSupport {

  @DisplayName("[GET] /test/count 성공")
  @Test
  void testGetCount() throws Exception {
    when(countTestService.getSuccessCount()).thenReturn(10L);
    when(countTestService.getErrorCount()).thenReturn(5L);

    mockMvc.perform(get("/test/count")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.successCount").value(10))
        .andExpect(jsonPath("$.data.errorCount").value(5));

    verify(countTestService).getSuccessCount();
    verify(countTestService).getErrorCount();
  }

  @Test
  @DisplayName("[PUT] /test/count/reset reset api 호출 성공")
  void testResetCount() throws Exception {
    mockMvc.perform(put("/test/count/reset")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(countTestService).reset();
  }
}