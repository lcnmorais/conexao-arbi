package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.PaymentFeignClient;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.PaymentRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.FundingRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.PaymentResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.SweepRequest;

import java.time.LocalDate;
import java.util.UUID;
import software.stoica.labs.baas.ConexaoArbi.core.model.IdentificacaoArbiProperties;

@Service
public class PaymentService {

    @Autowired
    private PaymentFeignClient paymentFeignClient;

    @Autowired
    private TokenManagementService tokenManagementService;

    @Autowired
    private RequestPreparationService requestPreparationService;

    @Autowired
    private TraceService traceService;

    @Autowired
    private IdentificacaoArbiProperties identificacaoArbiProperties;

    @Value("${banco.arbi.conexao.codInstituicao:54403563}")
    private String COD_INSTITUICAO;

    @Value("${banco.arbi.conexao.codAgencia:0001}")
    private String COD_AGENCIA;

    @Value("${banco.arbi.conexao.tipoConta:CACC}")
    private String TIPO_CONTA;

    @Value("${banco.arbi.conexao.codUsuario:_autbank1}")
    private String codUsuario;

    @Value("${banco.arbi.conexao.nomePagador:Parceiro}")
    private String NOME_PREVIMIL;

    @Value("${banco.arbi.conexao.campoLivre:Transferência para quitação de boleto}")
    private String campoLivre;

    @Value("${banco.arbi.conexao.canalEntrada:Mobile App}")
    private String canalEntrada;

    @Value("${banco.arbi.conexao.prioridade:HIGH}")
    private String prioridade;

    public PaymentResponse processFunding(String nroContaPagador, FundingRequest fundingRequest) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        // Compose the full payment request with constants
        PaymentRequest fullPaymentRequest = composeFundingPaymentRequest(nroContaPagador, fundingRequest);

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "FUNDING",
                identificacaoArbiProperties.inscricaoParceiro(),
                identificacaoArbiProperties.contaPrevimil(),
                requestIdStr,
                "SUCESSO");

            // Call the feign client to process the payment
            PaymentResponse[] responseArray = paymentFeignClient.processPayment(fullPaymentRequest);

            // Log the response with Arbi request ID
            if (responseArray != null && responseArray.length > 0) {
                PaymentResponse response = responseArray[0];
                traceService.logOperation(response.idrequisicaoarbi(),
                    "RESPOSTA", "FUNDING",
                    fundingRequest.cpfCnpjBeneficiario(),
                    fundingRequest.nroContaBeneficiario(),
                    requestIdStr,
                    "SUCESSO -> "+response);
                return response;
            } else {
                traceService.logOperation("",
                    "RESPOSTA", "FUNDING",
                    fundingRequest.cpfCnpjBeneficiario(),
                    fundingRequest.nroContaBeneficiario(),
                    requestIdStr,
                    "No response returned from funding processing");
                throw new RuntimeException("No response returned from funding processing");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("",
                "RESPOSTA", "FUNDING",
                identificacaoArbiProperties.inscricaoParceiro(),
                identificacaoArbiProperties.contaPrevimil(),
                requestIdStr,
                errorMessage);

            throw new RuntimeException("Error processing funding: " + errorMessage, e);
        }
    }

    public PaymentResponse processSweep(String numeroConta, SweepRequest sweepRequest) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        // Compose the full payment request for sweep operation
        PaymentRequest fullPaymentRequest = composeSweepPaymentRequest(numeroConta, sweepRequest);

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "SWEEP",
                sweepRequest.cpfCnpjBeneficiario(),
                numeroConta,
                requestIdStr,
                "SUCESSO");

            // Call the feign client to process the sweep
            PaymentResponse[] responseArray = paymentFeignClient.processPayment(fullPaymentRequest);

            // Log the response with Arbi request ID
            if (responseArray != null && responseArray.length > 0) {
                PaymentResponse response = responseArray[0];
                traceService.logOperation(response.idrequisicaoarbi(),
                    "RESPOSTA", "SWEEP",
                    sweepRequest.cpfCnpjBeneficiario(),
                    identificacaoArbiProperties.contaPrevimil(),
                    requestIdStr,
                    "SUCESSO -> "+response);
                return response;
            } else {
                traceService.logOperation("",
                    "RESPOSTA", "SWEEP",
                    sweepRequest.cpfCnpjBeneficiario(),
                    identificacaoArbiProperties.contaPrevimil(),
                    requestIdStr,
                    "No response returned from sweep processing");
                throw new RuntimeException("No response returned from sweep processing");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("",
                "RESPOSTA", "SWEEP",
                sweepRequest.cpfCnpjBeneficiario(),
                numeroConta,
                requestIdStr,
                errorMessage);

            throw new RuntimeException("Error processing sweep: " + errorMessage, e);
        }
    }

    private PaymentRequest composeFundingPaymentRequest(String nroContaPagador, FundingRequest fundingRequest) {
        // Get current date
        String currentDate = LocalDate.now().toString();

        // Generate UUID for idIdempotente
        String idIdempotente = UUID.randomUUID().toString();

        return new PaymentRequest(
            COD_INSTITUICAO,      // Constant: "54403563"
            COD_AGENCIA,          // Constant: "0001"
            identificacaoArbiProperties.contaPrevimil(), // Source is contaPrevimil
            TIPO_CONTA,           // Constant: "CACC"
            identificacaoArbiProperties.inscricaoParceiro(), // From properties
            fundingRequest.valorOperacao(), // From funding request
            codUsuario,                 // Constant: "_autbank1"
            currentDate,                // Current date
            NOME_PREVIMIL,                // Constant: "Parceiro"
            campoLivre,                 // Constant: "Transferência para quitação de boleto"
            idIdempotente,              // Generated UUID
            canalEntrada,               // Constant: "Mobile App"
            prioridade,                 // Constant: "HIGH"
            COD_INSTITUICAO,            // Constant: "54403563"
            COD_AGENCIA,                // Constant: "0001"
            fundingRequest.nroContaBeneficiario(),       // Destination from funding request
            TIPO_CONTA,                 // Constant: "CACC"
            fundingRequest.cpfCnpjBeneficiario(),        // From funding request
            fundingRequest.nomeBeneficiario()            // From funding request
        );
    }

    private PaymentRequest composeSweepPaymentRequest(String nroContaPagador, SweepRequest sweepRequest) {
        // Get current date
        String currentDate = LocalDate.now().toString();

        // Generate UUID for idIdempotente
        String idIdempotente = UUID.randomUUID().toString();

        return new PaymentRequest(
            COD_INSTITUICAO,      // Constant: "54403563"
            COD_AGENCIA,          // Constant: "0001"
            nroContaPagador,      // Source is the account to sweep from
            TIPO_CONTA,           // Constant: "CACC"                
            sweepRequest.cpfCnpjBeneficiario(),  // From sweep request
            sweepRequest.valorOperacao(), // From sweep request                
            codUsuario,           // Constant: "_autbank1"
            currentDate,          // Current date
            sweepRequest.nomeBeneficiario(),          // Constant: "Parceiro"
            "Sweep operation - moving funds to main account", // Specific field for sweep
            idIdempotente,        // Generated UUID
            canalEntrada,         // Constant: "Mobile App"
            prioridade,           // Constant: "HIGH"
            COD_INSTITUICAO,      // Constant: "54403563"
            COD_AGENCIA,          // Constant: "0001"
            identificacaoArbiProperties.contaPrevimil(), // Destination is contaPrevimil
            TIPO_CONTA,           // Constant: "CACC"
            identificacaoArbiProperties.inscricaoParceiro(), // From properties
            NOME_PREVIMIL// From sweep request
        );
    }
}