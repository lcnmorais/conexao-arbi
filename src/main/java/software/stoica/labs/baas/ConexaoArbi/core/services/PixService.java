package software.stoica.labs.baas.ConexaoArbi.core.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.PixFeignClient;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.model.PixKeyData;
import software.stoica.labs.baas.ConexaoArbi.core.model.PixKeyResponse;

import java.util.List;

@Service
public class PixService {

    @Autowired
    private PixFeignClient pixFeignClient;

    @Autowired
    private TokenManagementService tokenManagementService;

    @Autowired
    private RequestPreparationService requestPreparationService;

    @Autowired
    private TraceService traceService;

    @Autowired
    private ObjectMapper objectMapper;

    public List<PixKeyData> obterChavesPixPorDocumentoAgenciaConta(String numeroCPFCNPJ, String numeroAgencia, String numeroConta) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "PIX_LIST", numeroCPFCNPJ, numeroConta, requestIdStr, "SUCESSO");

            // Call the feign client to get PIX keys by agency and account
            PixKeyResponse[] responseArray = pixFeignClient.obterChavesPixPorDocumentoAgenciaConta(numeroAgencia, numeroConta);

            // Log the response with Arbi request ID
            if (responseArray != null && responseArray.length > 0) {
                PixKeyResponse response = responseArray[0];

                // Parse the JSON string response into a List of PixKeyData objects
                List<PixKeyData> pixKeys = objectMapper.readValue(
                    response.response(),
                    new TypeReference<List<PixKeyData>>() {}
                );

                traceService.logOperation(response.response(), "RESPOSTA", "PIX_LIST", numeroCPFCNPJ, numeroConta, requestIdStr, "SUCESSO");
                return pixKeys;
            } else {
                traceService.logOperation("", "RESPOSTA", "PIX_LIST", numeroCPFCNPJ, numeroConta, requestIdStr, "No PIX keys returned");
                throw new RuntimeException("No PIX keys returned for document: " + numeroCPFCNPJ + ", agency: " + numeroAgencia + ", account: " + numeroConta);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("", "RESPOSTA", "PIX_LIST", numeroCPFCNPJ, numeroConta, requestIdStr, errorMessage);

            throw new RuntimeException("Error retrieving PIX keys for document: " + numeroCPFCNPJ +
                ", agency: " + numeroAgencia +
                ", account: " + numeroConta +
                " - Error: " + errorMessage, e);
        }
    }
}