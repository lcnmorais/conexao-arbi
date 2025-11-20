package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import software.stoica.labs.baas.ConexaoArbi.core.model.CredencialPrevimilArbi;

@Component
public class ArbiAuthRequestInterceptor implements RequestInterceptor {

    private final TokenCache tokenCache;
    private final CredencialPrevimilArbi credencialPrevimilArbi;

    public ArbiAuthRequestInterceptor(TokenCache tokenCache, CredencialPrevimilArbi credencialPrevimilArbi) {
        this.tokenCache = tokenCache;
        this.credencialPrevimilArbi = credencialPrevimilArbi;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Basic " + credencialPrevimilArbi.getEncondedCredentials());

        if (template.url().contains("grant-code") || template.url().contains("access-token")) {
            return;
        }

        String accessToken = tokenCache.getToken("default_access_token").access_token();
        template.header("client_id",  credencialPrevimilArbi.clientId());
        template.header("access_token",  accessToken);

    }
}