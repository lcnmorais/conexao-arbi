package software.stoica.labs.baas.ConexaoArbi.core.model;

import java.math.BigDecimal;

public record PixPaymentRequest(
    String codAgenciaPagador,
    String nroContaPagador,
    String tipoContaPagador,
    String cpfCnpjPagador,
    BigDecimal valorOperacao,
    String dataPagamento,
    String nomePagador,
    String campoLivre
) {}