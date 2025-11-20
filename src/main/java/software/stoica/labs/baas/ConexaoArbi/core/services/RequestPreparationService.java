package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.TokenCache;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.config.RequestIdGenerator;

import java.time.LocalDateTime;

@Service
public class RequestPreparationService {
    
    @Autowired
    private BancoArbiAuthService bancoArbiAuthService;
    
    @Autowired
    private TokenManagementService tokenManagementService;
    
    @Autowired
    private TokenCache tokenCache;
    
    @Autowired
    private RequestIdGenerator requestIdGenerator;
    
    // Token refresh interval in minutes (mimicking the 60 minutes from the script)
    private static final int TOKEN_REFRESH_INTERVAL_MINUTES = 60;
    
    // Default token key for caching
    private static final String DEFAULT_TOKEN_KEY = "default_access_token";
    
    // Track the next refresh time for tokens
    private LocalDateTime nextTokenRefreshTime = LocalDateTime.now();
    
    public Long generateRequestId() {
        return requestIdGenerator.generateRequestId();
    }
    
    public String getFormattedDate() {
        return requestIdGenerator.generateFormattedDate();
    }
    
    public AccessTokenResponse getValidAccessToken() {
        // Check if we need to refresh the token based on the script's 60-minute logic
        LocalDateTime now = LocalDateTime.now();
        
        // If the token refresh time has passed, force refresh
        if (now.isAfter(nextTokenRefreshTime)) {
            // Refresh the token
            AccessTokenResponse token = tokenManagementService.refreshAccessToken();
            // Set next refresh time to 60 minutes from now
            nextTokenRefreshTime = now.plusMinutes(TOKEN_REFRESH_INTERVAL_MINUTES);
            return token;
        } else {
            // Return cached token (the tokenManagementService handles caching internally)
            return tokenManagementService.getAccessToken();
        }
    }
    
    /**
     * Gets the current access token without checking expiration
     */
    public AccessTokenResponse getCurrentAccessToken() {
        return tokenManagementService.getAccessToken();
    }
    
    /**
     * Forces a token refresh for testing purposes
     */
    public AccessTokenResponse forceRefreshToken() {
        AccessTokenResponse token = tokenManagementService.refreshAccessToken();
        nextTokenRefreshTime = LocalDateTime.now().plusMinutes(TOKEN_REFRESH_INTERVAL_MINUTES);
        return token;
    }
    
    public LocalDateTime getNextTokenRefreshTime() {
        return nextTokenRefreshTime;
    }
}