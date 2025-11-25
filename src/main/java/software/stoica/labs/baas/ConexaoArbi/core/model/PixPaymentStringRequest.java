package software.stoica.labs.baas.ConexaoArbi.core.model;

public record PixPaymentStringRequest(
    String nomeBeneficiario,
    String cidade,
    String codUsuario,
    String codIspb,
    String referenciaInterna,
    String documentoId,
    String documentoRevisao,
    String recebedorCpf,
    Double valor,
    String chaveEnderecamento,
    Boolean reutilizavel,
    Integer calendarioExpiracaoSegundos,
    String tipoPix
) {}