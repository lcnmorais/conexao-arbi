package software.stoica.labs.baas.ConexaoArbi.core.model;

import java.math.BigDecimal;
import java.util.UUID;

public record PixPaymentInternalRequest(
    String codInstituicaoPagador,
    String codAgenciaPagador,
    String nroContaPagador,
    String tipoContaPagador,
    String cpfCnpjPagador,
    BigDecimal valorOperacao,
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
) {
    public static PixPaymentInternalRequest fromPixPaymentRequest(PixPaymentRequest request,
            IdentificacaoArbiProperties identificacaoArbiProperties) {
        String institutionCode = "54403563"; // Constant institution code
        String userCode = "_autbank1"; // Default user code

        return new PixPaymentInternalRequest(
            institutionCode, // codInstituicaoPagador
            request.codAgenciaPagador(),
            request.nroContaPagador(),
            request.tipoContaPagador(),
            request.cpfCnpjPagador(),
            request.valorOperacao(),
            userCode, // codUsuario
            request.dataPagamento(),
            request.nomePagador(),
            request.campoLivre(),
            UUID.randomUUID().toString().replace("-", ""), // idIdempotente as UUID without dashes
            "Mobile App", // canalEntrada
            "HIGH", // prioridade
            institutionCode, // codInstituicaoBeneficiario (same as pagador)
            "0001", // codAgenciaBeneficiario (default)
            identificacaoArbiProperties.contaPrevimil(), // nroContaBeneficiario (from properties)
            "CACC", // tipoContaBeneficiario (default)
            identificacaoArbiProperties.inscricaoParceiro(), // cpfCnpjBeneficiario (from properties)
            "Previmil" // nomeBeneficiario (default)
        );
    }
}