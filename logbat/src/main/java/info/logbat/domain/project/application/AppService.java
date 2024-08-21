package info.logbat.domain.project.application;

import info.logbat.domain.project.repository.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"app"})
public class AppService {

    private static final String APP_NOT_FOUND_MESSAGE = "앱을 찾을 수 없습니다.";

    private final AppRepository appRepository;

    @Cacheable(key = "#token")
    public Long getAppIdByToken(String token) {
        return appRepository.getAppIdByToken(token)
            .orElseThrow(() -> new IllegalArgumentException(APP_NOT_FOUND_MESSAGE));
    }

    @CacheEvict(key = "#token")
    public void evictAppCache(String token) {
        // TODO: Implement cache eviction

    }
}
