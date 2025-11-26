package software.stoica.labs.baas.ConexaoArbi.core.model;

public record PixPaymentResponse(
    String situacao,
    String mensagem,
    String idOperacao
) {}