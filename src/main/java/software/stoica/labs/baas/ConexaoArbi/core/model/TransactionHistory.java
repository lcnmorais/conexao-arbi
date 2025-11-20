package software.stoica.labs.baas.ConexaoArbi.core.model;

public record TransactionHistory(
    String datamovimento,        // Movement date
    String conta,                // Account number
    String natureza,             // Nature (C for credit, D for debit)
    String historico,            // Description
    String nromovimento,         // Movement number
    String valor,                // Value
    String inscricaocontraparte, // Counterparty registration
    String nomecontraparte,      // Counterparty name
    String finalidade,           // Purpose
    String evento,               // Event
    String sisorigem,            // Source system
    String codhist,              // History code
    String dataatualizacao,      // Update date
    String nrodocto              // Document number
) {}