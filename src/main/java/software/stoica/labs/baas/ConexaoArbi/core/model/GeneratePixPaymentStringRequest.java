package software.stoica.labs.baas.ConexaoArbi.core.model;

public record GeneratePixPaymentStringRequest(
    String documentoId,
    Double valor,
    Boolean reutilizavel,
    Integer calendarioExpiracaoSegundos
) {}