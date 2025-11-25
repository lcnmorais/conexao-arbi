package software.stoica.labs.baas.ConexaoArbi.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public record PixKeyData(
    String agencia,
    String conta,
    String cpfCnpj,
    String instituicao,
    String tipoConta,
    Boolean confirmado,
    String cid,
    String nome,
    String tipoPessoa,
    String chave,
    String tipoChave,
    @JsonProperty("dataCriacao") 
    LocalDateTime dataCriacao,
    @JsonProperty("dataPosse") 
    LocalDateTime dataPosse,
    @JsonProperty("reivindicadaDoacao") 
    String reivindicadaDoacao,
    String endToEnd,
    String nomePsp,
    @JsonProperty("dataAbertura") 
    LocalDateTime dataAbertura,
    Map<String, Object> estatisticas
) {}