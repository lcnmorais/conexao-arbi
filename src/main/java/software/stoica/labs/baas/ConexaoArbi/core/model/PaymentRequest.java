package software.stoica.labs.baas.ConexaoArbi.core.model;

public record PaymentRequest(
    String codInstituicaoPagador,
    String codAgenciaPagador,
    String nroContaPagador,
    String tipoContaPagador,
    String cpfCnpjPagador,
    Double valorOperacao,
    String codUsuario,
    String dataPagamento,
    String nomePagador,
    String campoLivre,
    String idIdempotente,
    String canalEntrada,
    String prioridade,
    String codInstituicaoBeneficiario,
    String codAgenciaBeneficiario,
    String nroContaBeneficiario,
    String tipoContaBeneficiario,
    String cpfCnpjBeneficiario,
    String nomeBeneficiario
) {}