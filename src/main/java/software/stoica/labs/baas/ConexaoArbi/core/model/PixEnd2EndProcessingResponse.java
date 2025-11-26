package software.stoica.labs.baas.ConexaoArbi.core.model;

public record PixEnd2EndProcessingResponse(
    String qrCodeValue,
    String cpfCnpj,
    String status,
    String message
) {}