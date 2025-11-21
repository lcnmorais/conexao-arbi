package software.stoica.labs.baas.ConexaoArbi.core.model;

public record FundingRequest(
    String nroContaBeneficiario,
    String cpfCnpjBeneficiario,
    String nomeBeneficiario,
    Double valorOperacao
) {}