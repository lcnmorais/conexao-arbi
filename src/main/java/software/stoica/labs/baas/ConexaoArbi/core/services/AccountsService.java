package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.AccountsFeignClient;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteData;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteResponse;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountsService {

    @Autowired
    private AccountsFeignClient accountsFeignClient;

    @Autowired
    private TokenManagementService tokenManagementService;

    @Autowired
    private RequestPreparationService requestPreparationService;

    public Map<String, Object> getAccountBalance(String accountNumber, String fromDate, String toDate) {
        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Generate a request ID
            Long requestId = requestPreparationService.generateRequestId();

            // Create the inner data object for the request
            ContaCorrenteData requestData = new ContaCorrenteData(
                System.getenv("Inscricao"), // inscricaoparceiro
                System.getenv("TokenUsuarioPrd"), // tokenusuario
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
            ContaCorrenteResponse response = accountsFeignClient.getBalance(request);

            // Return the response
            return Map.of(
                "accountNumber", accountNumber,
                "balance", response.resultado(),
                "status", response.descricaostatus(),
                "fromDate", fromDate,
                "toDate", toDate
            );
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving account balance: " + e.getMessage(), e);
        }
    }
}