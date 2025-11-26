package software.stoica.labs.baas.ConexaoArbi.core.model;

public record PixProcessamentoRequest(
    String cpfCnpj,
    String qrCodeValue
) {}