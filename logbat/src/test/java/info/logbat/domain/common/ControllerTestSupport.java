package info.logbat.domain.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.logbat.domain.log.application.LogService;
import info.logbat.domain.log.presentation.LogController;
import info.logbat.domain.project.presentation.ProjectController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/*
 * 이후 테스트 하려면 Controller에 대해 controllers에 추가하고, ControllerTestSupport를 상속받아 테스트를 진행하시면 됩니다.
 */
@WebMvcTest(controllers = {LogController.class, ProjectController.class})
@ActiveProfiles("test")
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected LogService logService;

    /*
     * 이후 필요한 서비스에 대해 MockBean을 추가하여 테스트를 진행하시면 됩니다.
     */

}