package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.AccountsFeignClient;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteData;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.IdentificacaoArbiProperties;
import software.stoica.labs.baas.ConexaoArbi.core.model.ConexaoArbiProperties;
import software.stoica.labs.baas.ConexaoArbi.core.model.TransactionHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AccountsService {

    @Autowired
    private AccountsFeignClient accountsFeignClient;

    @Autowired
    private TokenManagementService tokenManagementService;

    @Autowired
    private RequestPreparationService requestPreparationService;

    @Autowired
    private IdentificacaoArbiProperties identificacaoArbiProperties;

    @Autowired
    private ConexaoArbiProperties conexaoArbiProperties;

    public List<ContaCorrenteResponse> getAccountBalance(String accountNumber, String fromDate, String toDate) {
        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Generate a request ID
            Long requestId = requestPreparationService.generateRequestId();

            // Create the inner data object for the request
            ContaCorrenteData requestData = new ContaCorrenteData(
                identificacaoArbiProperties.inscricaoParceiro(), // inscricaoparceiro
                conexaoArbiProperties.userToken(), // tokenusuario
                requestId.toString(), // idrequisicao
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

            // Convert array to list for better serialization
            return responseArray != null ? Arrays.asList(responseArray) : new ArrayList<>();
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            throw new RuntimeException("Error retrieving account balance: " + errorMessage, e);
        }
    }
    
    public List<TransactionHistory> getAccountHistory(String accountNumber, String fromDate, String toDate) {
        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Generate a request ID
            Long requestId = requestPreparationService.generateRequestId();

            // Create the inner data object for the request
            // For history, we use transaction ID 4
            ContaCorrenteData requestData = new ContaCorrenteData(
                identificacaoArbiProperties.inscricaoParceiro(), // inscricaoparceiro
                conexaoArbiProperties.userToken(), // tokenusuario
                requestId.toString(), // idrequisicao
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
            throw new RuntimeException("Error retrieving account history: " + errorMessage, e);
        }
    }
    
}