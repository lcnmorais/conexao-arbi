package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.AccountsFeignClient;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.AccountManagementFeignClient;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteData;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.CadastroPFRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.CadastroPFData;
import software.stoica.labs.baas.ConexaoArbi.core.model.CadastroPFResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.IdentificacaoArbiProperties;
import software.stoica.labs.baas.ConexaoArbi.core.model.ConexaoArbiProperties;
import software.stoica.labs.baas.ConexaoArbi.core.model.KYCRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.TransactionHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AccountsService {
    
    private static final String CONTA_DEPOSITO_VISTA_PF = "12";

    @Autowired
    private AccountsFeignClient accountsFeignClient;

    @Autowired
    private AccountManagementFeignClient accountManagementFeignClient;

    @Autowired
    private TokenManagementService tokenManagementService;

    @Autowired
    private RequestPreparationService requestPreparationService;

    @Autowired
    private IdentificacaoArbiProperties identificacaoArbiProperties;

    @Autowired
    private ConexaoArbiProperties conexaoArbiProperties;

    @Autowired
    private TraceService traceService;

    public List<ContaCorrenteResponse> getAccountBalance(String accountNumber, String fromDate, String toDate) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "SALDO", null, accountNumber, requestIdStr, "SUCESSO");

            // Create the inner data object for the request
            ContaCorrenteData requestData = new ContaCorrenteData(
                identificacaoArbiProperties.inscricaoParceiro(), // inscricaoparceiro
                conexaoArbiProperties.userToken(), // tokenusuario
                requestIdStr, // idrequisicao
                "1", // idmodulo
                "3", // idtransacao
                "213", // bancoorigem
                "00019", // agenciaorigem
                accountNumber, // contaorigem (using the account number from the path parameter)
                "CC", // tipocontadebitada (assuming Corrente)
                "", // bancodestino
                "", // agenciadestino
                "", // contadestino
                "", // tipocontacreditada
                "", // cnpjcpfclicred
                "", // nomeclicred
                "", // tipopessoaclicred
                "", // finalidade
                "", // historico
                "", // dataagendamento
                "0", // valor
                fromDate != null ? fromDate : "", // datainicial
                toDate != null ? toDate : "", // datafinal
                "", // periodoemdias
                "E" // canalentrada
            );

            // Create the request object for the feign client based on the provided structure
            ContaCorrenteRequest request = new ContaCorrenteRequest(requestData);

            // Call the feign client to get the balance
            ContaCorrenteResponse[] responseArray = accountsFeignClient.getBalance(request);

            // Log the response with Arbi request ID
            if (responseArray != null && responseArray.length > 0) {
                String idRequisicaoArbi = responseArray[0].idrequisicaoarbi();
                traceService.logOperation(idRequisicaoArbi, "RESPOSTA", "SALDO", null, accountNumber, requestIdStr, "SUCESSO");
            }

            // Convert array to list for better serialization
            return responseArray != null ? Arrays.asList(responseArray) : new ArrayList<>();
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("", "RESPOSTA", "SALDO", null, accountNumber, requestIdStr, errorMessage);

            throw new RuntimeException("Error retrieving account balance: " + errorMessage, e);
        }
    }
    
    public List<TransactionHistory> getAccountHistory(String accountNumber, String fromDate, String toDate) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "EXTRATO", null, accountNumber, requestIdStr, "SUCESSO");

            // Create the inner data object for the request
            // For history, we use transaction ID 4
            ContaCorrenteData requestData = new ContaCorrenteData(
                identificacaoArbiProperties.inscricaoParceiro(), // inscricaoparceiro
                conexaoArbiProperties.userToken(), // tokenusuario
                requestIdStr, // idrequisicao
                "1", // idmodulo
                "4", // idtransacao (transaction history)
                "213", // bancoorigem
                "00019", // agenciaorigem
                accountNumber, // contaorigem (using the account number from the path parameter)
                "CC", // tipocontadebitada (assuming Corrente)
                "", // bancodestino
                "", // agenciadestino
                "", // contadestino
                "", // tipocontacreditada
                "", // cnpjcpfclicred
                "", // nomeclicred
                "", // tipopessoaclicred
                "", // finalidade
                "", // historico
                "", // dataagendamento
                "0", // valor
                fromDate != null ? fromDate : "", // datainicial
                toDate != null ? toDate : "", // datafinal
                "", // periodoemdias
                "E" // canalentrada
            );

            // Create the request object for the feign client based on the provided structure
            ContaCorrenteRequest request = new ContaCorrenteRequest(requestData);

            // Call the feign client to get the history
            ContaCorrenteResponse[] responseArray = accountsFeignClient.getBalance(request);

            if (responseArray == null) {
                return new ArrayList<>();
            }

            // Log the response with Arbi request ID
            if (responseArray.length > 0) {
                String idRequisicaoArbi = responseArray[0].idrequisicaoarbi();
                traceService.logOperation(idRequisicaoArbi, "RESPOSTA", "EXTRATO", null, accountNumber, requestIdStr, "SUCESSO");
            }

            // Parse the JSON strings in the resultado field to TransactionHistory objects
            List<TransactionHistory> historyList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

            for (ContaCorrenteResponse response : responseArray) {
                if (response.resultado() != null && !response.resultado().isEmpty()) {
                    // Check if the resultado is a JSON object or a plain text message
                    String resultado = response.resultado().trim();

                    if (resultado.startsWith("{") && resultado.endsWith("}")) {
                        // It's a JSON object, parse it as TransactionHistory
                        try {
                            TransactionHistory transaction = objectMapper.readValue(resultado, TransactionHistory.class);
                            historyList.add(transaction);
                        } catch (Exception e) {
                            // Log the error but continue processing other items
                            System.err.println("Error parsing transaction history JSON: " + e.getMessage());
                        }
                    } else {
                        // It's a plain text message, create a TransactionHistory with the message
                        // In this case, we'll create a special TransactionHistory object to represent the message
                        TransactionHistory messageTransaction = new TransactionHistory(
                            null, // datamovimento
                            null, // conta
                            null, // natureza
                            resultado, // historico - put the message here
                            null, // nromovimento
                            null, // valor
                            null, // inscricaocontraparte
                            null, // nomecontraparte
                            null, // finalidade
                            null, // evento
                            null, // sisorigem
                            null, // codhist
                            null, // dataatualizacao
                            null  // nrodocto
                        );
                        historyList.add(messageTransaction);
                    }
                }
            }

            return historyList;
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("", "RESPOSTA", "EXTRATO", null, accountNumber, requestIdStr, errorMessage);

            throw new RuntimeException("Error retrieving account history: " + errorMessage, e);
        }
    }

    public CadastroPFResponse submitAnalyseKYC(KYCRequest kycRequest) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "ANALISE_KYC", kycRequest.cpf(), null, requestIdStr, "SUCESSO");

            // Create the inner data object for the request
            CadastroPFData requestData = kyc2cadastro(kycRequest, "2");
            
            // Create the request object for the feign client based on the provided structure
            CadastroPFRequest request = new CadastroPFRequest(requestData);

            // Call the feign client to submit KYC analysis
            CadastroPFResponse[] responseArray = accountManagementFeignClient.submitKYC(request);

            // Log the response with Arbi request ID
            if (responseArray != null && responseArray.length > 0) {
                CadastroPFResponse response = responseArray[0];
                traceService.logOperation(response.idrequisicaoarbi(), "RESPOSTA", "ANALISE_KYC", kycRequest.cpf(), null, requestIdStr, "SUCESSO");
                return response;
            } else {
                traceService.logOperation("", "RESPOSTA", "ANALISE_KYC", kycRequest.cpf(), null, requestIdStr, "No response returned from KYC analysis submission");
                throw new RuntimeException("No response returned from KYC analysis submission");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("", "RESPOSTA", "ANALISE_KYC", kycRequest.cpf(), null, requestIdStr, errorMessage);

            throw new RuntimeException("Error submitting KYC analysis: " + errorMessage, e);
        }
    }

    public CadastroPFResponse checkKYCApproval(KYCRequest kycRequest) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "VERIFICACAO_KYC", kycRequest.cpf(), null, requestIdStr, "SUCESSO");

            // Create the inner data object for the request
            CadastroPFData requestData = kyc2cadastro(kycRequest, "3");

            // Create the request object for the feign client based on the provided structure
            CadastroPFRequest request = new CadastroPFRequest(requestData);

            // Call the feign client to check KYC approval
            CadastroPFResponse[] responseArray = accountManagementFeignClient.submitKYC(request);

            // Log the response with Arbi request ID
            if (responseArray != null && responseArray.length > 0) {
                CadastroPFResponse response = responseArray[0];
                traceService.logOperation(response.idrequisicaoarbi(), "RESPOSTA", "VERIFICACAO_KYC", kycRequest.cpf(), null, requestIdStr, "SUCESSO");
                return response;
            } else {
                traceService.logOperation("", "RESPOSTA", "VERIFICACAO_KYC", kycRequest.cpf(), null, requestIdStr, "No response returned from KYC approval check");
                throw new RuntimeException("No response returned from KYC approval check");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("", "RESPOSTA", "VERIFICACAO_KYC", kycRequest.cpf(), null, requestIdStr, errorMessage);

            throw new RuntimeException("Error checking KYC approval: " + errorMessage, e);
        }
    }

    public CadastroPFResponse submitRenewKYC(KYCRequest kycRequest) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "RENOVACAO_KYC", kycRequest.cpf(), null, requestIdStr, "SUCESSO");

            // Create the inner data object for the request
            CadastroPFData requestData = kyc2cadastro(kycRequest, "5");
            
            // Create the request object for the feign client based on the provided structure
            CadastroPFRequest request = new CadastroPFRequest(requestData);

            // Call the feign client to submit KYC renewal
            CadastroPFResponse[] responseArray = accountManagementFeignClient.submitKYC(request);

            // Log the response with Arbi request ID
            if (responseArray != null && responseArray.length > 0) {
                CadastroPFResponse response = responseArray[0];
                traceService.logOperation(response.idrequisicaoarbi(), "RESPOSTA", "RENOVACAO_KYC", kycRequest.cpf(), null, requestIdStr, "SUCESSO");
                return response;
            } else {
                traceService.logOperation("", "RESPOSTA", "RENOVACAO_KYC", kycRequest.cpf(), null, requestIdStr, "No response returned from KYC renewal submission");
                throw new RuntimeException("No response returned from KYC renewal submission");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("", "RESPOSTA", "RENOVACAO_KYC", kycRequest.cpf(), null, requestIdStr, errorMessage);

            throw new RuntimeException("Error submitting KYC renewal: " + errorMessage, e);
        }
    }

    public CadastroPFResponse createAccount(KYCRequest kycRequest) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "CRIACAO_CONTA", kycRequest.cpf(), null, requestIdStr, "SUCESSO");

            // Create the inner data object for the request
            CadastroPFData requestData = kyc2cadastro(kycRequest, "1");

            // Create the request object for the feign client based on the provided structure
            CadastroPFRequest request = new CadastroPFRequest(requestData);

            // Call the feign client to create account
            CadastroPFResponse[] responseArray = accountManagementFeignClient.submitKYC(request);

            // Log the response with Arbi request ID and account number
            if (responseArray != null && responseArray.length > 0) {
                CadastroPFResponse response = responseArray[0];
                // Extract account number from resultado if possible, or use a default value
                String situacao = "SUCESSO";
                if (response.resultado() != null && !response.resultado().isEmpty()) {
                    // For account creation, we want to include the account number in the status
                    // This would require parsing the resultado to extract the account number
                    // For now, using placeholder - in a real implementation you would extract the account number from response
                    situacao = "SUCESSO -> "+response.resultado();
                }
                traceService.logOperation(response.idrequisicaoarbi(), "RESPOSTA", "CRIACAO_CONTA", kycRequest.cpf(), null, requestIdStr, situacao);
                return response;
            } else {
                traceService.logOperation("", "RESPOSTA", "CRIACAO_CONTA", kycRequest.cpf(), null, requestIdStr, "No response returned from account creation request");
                throw new RuntimeException("No response returned from account creation request");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("", "RESPOSTA", "CRIACAO_CONTA", kycRequest.cpf(), null, requestIdStr, errorMessage);

            throw new RuntimeException("Error creating account: " + errorMessage, e);
        }
    }
    
    private CadastroPFData kyc2cadastro(KYCRequest kycRequest, String idTransacao) {
            Long requestId = requestPreparationService.generateRequestId();
            String requestIdStr = requestId.toString();
        
            return new CadastroPFData(
                identificacaoArbiProperties.inscricaoParceiro(), // inscricaoparceiro
                conexaoArbiProperties.userToken(), // tokenusuario
                requestIdStr, // idrequisicao
                "4", // idmodulo
                idTransacao, // idtransacao (Abertura de Conta)
                kycRequest.nome(), // nome
                kycRequest.cpf(), // cpf
                kycRequest.endereco(), // endereco
                kycRequest.numero(), // numero
                kycRequest.complemento(), // complemento
                kycRequest.bairro(), // bairro
                kycRequest.cidade(), // cidade
                kycRequest.cep(), // cep
                kycRequest.uf(), // uf
                kycRequest.datanascimento(), // datanascimento
                kycRequest.naturalidade(), // naturalidade
                kycRequest.tipoident(), // tipoident
                kycRequest.numeroident(), // numeroident
                kycRequest.dataident(), // dataident
                kycRequest.orgaoident(), // orgaoident
                kycRequest.uforgaoident(), // uforgaoident
                kycRequest.nomemae(), // nomemae
                kycRequest.nomepai(), // nomepai
                kycRequest.email(), // email
                kycRequest.ddd(), // ddd
                kycRequest.telefone(), // telefone
                kycRequest.ppe(), // ppe
                kycRequest.cargo(), // cargo
                kycRequest.renda(), // renda
                kycRequest.nacionalidade(), // nacionalidade
                kycRequest.sexo(), // sexo
                kycRequest.periodo(), // periodo
                kycRequest.observacao(), // observacao
                "", // foto (not provided in simplified DTO)
                CONTA_DEPOSITO_VISTA_PF // tipoproduto for account opening
            );
    }

}