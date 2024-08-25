package info.logbat.domain.project.presentation;

import info.logbat.domain.project.application.AppService;
import info.logbat.domain.project.presentation.payload.request.AppCacheRemoveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/apps")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAppCache(@RequestBody @Valid AppCacheRemoveRequest request) {
        appService.evictAppCache(request.token());
    }

}
