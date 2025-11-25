package software.stoica.labs.baas.ConexaoArbi.core.model;

public record PixPaymentStringResponse(
    String qrCodeValue,
    String calendarioCriacao
) {}