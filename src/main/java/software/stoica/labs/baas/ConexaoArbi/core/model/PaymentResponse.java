package software.stoica.labs.baas.ConexaoArbi.core.model;

public record PaymentResponse(
    String idrequisicaoarbi,
    String idrequisicao,
    String resultado,
    String mensagem,
    String status
) {}