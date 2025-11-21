package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.stoica.labs.baas.ConexaoArbi.core.model.CredencialPrevimilArbi;

@Component
public class ArbiAuthRequestInterceptor implements RequestInterceptor {

    private static final Logger logger = Logger.getLogger(ArbiAuthRequestInterceptor.class.getName());
    
    @Value("${banco.arbi.conexao.url}")
    private String baseURL;
    
    private final TokenCache tokenCache;
    private final CredencialPrevimilArbi credencialPrevimilArbi;

    public ArbiAuthRequestInterceptor(TokenCache tokenCache, CredencialPrevimilArbi credencialPrevimilArbi) {
        this.tokenCache = tokenCache;
        this.credencialPrevimilArbi = credencialPrevimilArbi;
    }

    @Override
    public void apply(RequestTemplate template) {
        
        logger.info(String.format("Método: %s --> %s --> %s", template.method(), baseURL, template.path()));
        
        template.header("Authorization", "Basic " + credencialPrevimilArbi.getEncondedCredentials());

        if (template.url().contains("grant-code") || template.url().contains("access-token")) {
            return;
        }

        String accessToken = tokenCache.getToken("default_access_token").access_token();
        template.header("client_id",  credencialPrevimilArbi.clientId());
        template.header("access_token",  accessToken);

    }
}