package software.stoica.labs.baas.ConexaoArbi.core.model;

public record SweepRequest(
    String cpfCnpjBeneficiario,
    String nomeBeneficiario,
    Double valorOperacao
) {}