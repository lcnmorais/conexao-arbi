package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.PaymentFeignClient;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.PaymentRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.FundingRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.PaymentResponse;

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
    private String nomePagador;

    @Value("${banco.arbi.conexao.campoLivre:Transferência para quitação de boleto}")
    private String campoLivre;

    @Value("${banco.arbi.conexao.canalEntrada:Mobile App}")
    private String canalEntrada;

    @Value("${banco.arbi.conexao.prioridade:HIGH}")
    private String prioridade;

    public PaymentResponse processFunding(String nroContaPagador, FundingRequest beneficiaryRequest) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        // Compose the full payment request with constants
        PaymentRequest fullPaymentRequest = composePaymentRequest(nroContaPagador, beneficiaryRequest);

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "TRANSFERENCIA",
                identificacaoArbiProperties.inscricaoParceiro(),
                nroContaPagador,
                requestIdStr,
                "SUCESSO");

            // Call the feign client to process the payment
            PaymentResponse[] responseArray = paymentFeignClient.processPayment(fullPaymentRequest);

            // Log the response with Arbi request ID
            if (responseArray != null && responseArray.length > 0) {
                PaymentResponse response = responseArray[0];
                traceService.logOperation(response.idrequisicaoarbi(),
                    "RESPOSTA", "TRANSFERENCIA",
                    beneficiaryRequest.cpfCnpjBeneficiario(),
                    beneficiaryRequest.nroContaBeneficiario(),
                    requestIdStr,
                    "SUCESSO -> "+response);
                return response;
            } else {
                traceService.logOperation("",
                    "RESPOSTA", "TRANSFERENCIA",
                    beneficiaryRequest.cpfCnpjBeneficiario(),
                    beneficiaryRequest.nroContaBeneficiario(),
                    requestIdStr,
                    "No response returned from payment processing");
                throw new RuntimeException("No response returned from payment processing");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("",
                "RESPOSTA", "TRANSFERENCIA",
                identificacaoArbiProperties.inscricaoParceiro(),
                nroContaPagador,
                requestIdStr,
                errorMessage);

            throw new RuntimeException("Error processing payment: " + errorMessage, e);
        }
    }

    private PaymentRequest composePaymentRequest(String nroContaPagador, FundingRequest beneficiaryRequest) {
        // Get current date
        String currentDate = LocalDate.now().toString();

        // Generate UUID for idIdempotente
        String idIdempotente = UUID.randomUUID().toString();

        return new PaymentRequest(
            COD_INSTITUICAO,      // Constant: "54403563"
            COD_AGENCIA,          // Constant: "0001"
            nroContaPagador,            // Value from controller path variable
            TIPO_CONTA,           // Constant: "CACC"
            identificacaoArbiProperties.inscricaoParceiro(), // From properties
            beneficiaryRequest.valorOperacao(), // From beneficiary request
            codUsuario,                 // Constant: "_autbank1"
            currentDate,                // Current date
            nomePagador,                // Constant: "Parceiro"
            campoLivre,                 // Constant: "Transferência para quitação de boleto"
            idIdempotente,              // Generated UUID
            canalEntrada,               // Constant: "Mobile App"
            prioridade,                 // Constant: "HIGH"
            COD_INSTITUICAO, // Constant: "54403563"
            COD_AGENCIA,     // Constant: "0001"
            beneficiaryRequest.nroContaBeneficiario(),       // From beneficiary request
            TIPO_CONTA,           // Constant: "CACC"
            beneficiaryRequest.cpfCnpjBeneficiario(),        // From beneficiary request
            beneficiaryRequest.nomeBeneficiario()            // From beneficiary request
        );
    }
}