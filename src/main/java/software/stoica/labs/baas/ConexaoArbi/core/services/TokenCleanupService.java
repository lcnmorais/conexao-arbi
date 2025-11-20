package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.TokenCache;

@Service
public class TokenCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(TokenCleanupService.class);

    @Autowired
    private TokenCache tokenCache;

    /**
     * Scheduled cleanup task that runs every 1 minute to remove expired tokens
     */
    @Scheduled(fixedRate = 60000) // 60 seconds
    public void cleanupExpiredTokens() {
        int beforeCount = tokenCache.getCachedTokenCount();
        tokenCache.cleanupExpiredTokens();
        int afterCount = tokenCache.getCachedTokenCount();

        if (beforeCount != afterCount) {
            logger.info("Cleaned up expired tokens. Before: {}, After: {}", beforeCount, afterCount);
        }
    }
}