package software.stoica.labs.baas.ConexaoArbi.core.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "banco.arbi.conexao")
public record ConexaoArbiProperties(
   String url,
   String clientId,
   String clientSecret,
   String userToken,
   String redirectUri
){}