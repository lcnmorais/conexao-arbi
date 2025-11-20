package software.stoica.labs.baas.ConexaoArbi.core.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "banco.arbi.conexao")
public record CredencialPrevimilArbi(
   String url,
   String clientId,
   String clientSecret,
   String redirectUri
){
    public String getEncondedCredentials() {
        return new String(java.util.Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes()));
    }
}