package info.logbat.domain.project.application;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"app"})
public class AppService {

    private static final String APP_NOT_FOUND_MESSAGE = "앱을 찾을 수 없습니다.";

    @Transactional(readOnly = true)
    @Cacheable(key = "#token")
    public Long getAppIdByToken(String token) {
        return 1L;
    }

    @CacheEvict(key = "#token")
    public void evictAppCache(String token) {
    }
}
