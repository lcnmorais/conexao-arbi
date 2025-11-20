package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenCache {

    private final ConcurrentHashMap<String, CachedToken> tokenCache = new ConcurrentHashMap<>();

    private static class CachedToken {
        private final AccessTokenResponse token;
        private final LocalDateTime timestamp;

        public CachedToken(AccessTokenResponse token) {
            this.token = token;
            this.timestamp = LocalDateTime.now();
        }

        public AccessTokenResponse getToken() {
            return token;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    public AccessTokenResponse getToken(String key) {
        CachedToken cachedToken = tokenCache.get(key);
        if (cachedToken != null) {
            // Check if token is still valid (within 5 minutes)
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(cachedToken.getTimestamp().plusMinutes(5))) {
                return cachedToken.getToken();
            } else {
                // Token expired, remove it
                tokenCache.remove(key);
            }
        }
        return null;
    }

    public void putToken(String key, AccessTokenResponse token) {
        tokenCache.put(key, new CachedToken(token));
    }

    public void clearToken(String key) {
        tokenCache.remove(key);
    }

    public boolean isTokenExpired(String key) {
        CachedToken cachedToken = tokenCache.get(key);
        if (cachedToken != null) {
            LocalDateTime now = LocalDateTime.now();
            return now.isAfter(cachedToken.getTimestamp().plusMinutes(5));
        }
        return true;
    }

    /**
     * Removes all expired tokens from the cache
     */
    public void cleanupExpiredTokens() {
        tokenCache.entrySet().removeIf(entry -> {
            LocalDateTime now = LocalDateTime.now();
            return now.isAfter(entry.getValue().getTimestamp().plusMinutes(5));
        });
    }

    /**
     * Gets the number of cached tokens
     */
    public int getCachedTokenCount() {
        return tokenCache.size();
    }
}