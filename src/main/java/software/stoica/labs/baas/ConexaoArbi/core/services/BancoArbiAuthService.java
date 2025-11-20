package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.GrantCodeRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.GrantCodeResponse;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.AccessTokenFeignClient;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.GrantCodeFeignClient;
import software.stoica.labs.baas.ConexaoArbi.core.model.CredencialPrevimilArbi;

@Service
public class BancoArbiAuthService {

    @Autowired
    private GrantCodeFeignClient grantCodeFeignClient;

    @Autowired
    private AccessTokenFeignClient accessTokenFeignClient;

    @Autowired
    private CredencialPrevimilArbi properties;

    /**
     * Gets the authorization code from Banco Arbi
     */
    public GrantCodeResponse getGrantCode() {
        GrantCodeRequest request = new GrantCodeRequest(
            properties.clientId(),
            properties.redirectUri()
        );

        return grantCodeFeignClient.getCodeGrant(request);
    }

    /**
     * Exchanges the authorization code for an access token using Basic authentication
     */
    public AccessTokenResponse getAccessTokenWithCode(String authorizationCode) {
        AccessTokenRequest request = new AccessTokenRequest(
            "authorization_code",
            authorizationCode
        );

        return accessTokenFeignClient.getAccessToken(request);
    }

    public String getClientId() {
        return properties.clientId();
    }

    public String getUsername() {
        return properties.clientId();
    }

    public String getPassword() {
        return properties.clientSecret();
    }
}