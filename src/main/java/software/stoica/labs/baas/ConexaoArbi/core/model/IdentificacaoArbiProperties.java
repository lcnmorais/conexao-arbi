package software.stoica.labs.baas.ConexaoArbi.core.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "banco.arbi.identificacao")
public record IdentificacaoArbiProperties(
   String inscricaoParceiro,
   String contaPrevimil,
   String contaMockExterno
){}


