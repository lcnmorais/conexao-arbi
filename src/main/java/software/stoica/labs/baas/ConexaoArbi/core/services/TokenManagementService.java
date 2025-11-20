package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.TokenCache;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.GrantCodeResponse;

@Service
public class TokenManagementService {
    
    @Autowired
    private BancoArbiAuthService bancoArbiAuthService;
    
    @Autowired
    private TokenCache tokenCache;
    
    private static final String DEFAULT_TOKEN_KEY = "default_access_token";
    
    public AccessTokenResponse getAccessToken() {
        // Try to get cached token first
        AccessTokenResponse cachedToken = tokenCache.getToken(DEFAULT_TOKEN_KEY);
        if (cachedToken != null) {
            return cachedToken;
        }
        
        // If no cached token or expired, get a new one
        return refreshAccessToken();
    }
    
    public AccessTokenResponse refreshAccessToken() {
        // First get the grant code
        GrantCodeResponse grantCodeResponse = bancoArbiAuthService.getGrantCode();
        
        // Then use the grant code to get the access token
        AccessTokenResponse tokenResponse = bancoArbiAuthService.getAccessTokenWithCode(grantCodeResponse.extractCode());
        
        // Store the new token in cache
        tokenCache.putToken(DEFAULT_TOKEN_KEY, tokenResponse);
        
        return tokenResponse;
    }
    
    public void invalidateToken() {
        tokenCache.clearToken(DEFAULT_TOKEN_KEY);
    }
}