package software.stoica.labs.baas.ConexaoArbi.core.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.stoica.labs.baas.ConexaoArbi.adapters.out.feign.PixFeignClient;
import software.stoica.labs.baas.ConexaoArbi.core.model.*;
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
    private IdentificacaoArbiProperties identificacaoArbiProperties;

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

    public DeviceRegistrationResponse registerDevice(String numeroAgencia, String numeroConta, String numeroTelefone) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Get document info from properties
            String documentNumber = identificacaoArbiProperties.inscricaoParceiro();

            // Create a simplified device registration request
            DeviceRegistrationRequest request = new DeviceRegistrationRequest(
                new DeviceAccount(
                    numeroConta,
                    new DevicePspInformation("54403563", numeroAgencia)
                ),
                "iPhone 12",  // alias
                "9774d56d682e549c",  // androidId
                "620fe0d249aa4d17524ae4c3b3332a8be2913a750bb151bf225794cdcb5ba4c1",  // canvasHash
                true,  // cookiesEnabled
                "480",  // densityDPI
                "string",  // fingerPrint
                "ABCDEF12-1234-5678-9ABC-DEF123456789",  // idfv
                "490154203237518",  // imei
                true,  // isRooted
                "es",  // language
                "Samsung",  // manufacturer
                "Samsung Galaxy S20",  // model
                new DevicePerson(
                    "Juan Pérez",  // fullName
                    documentNumber,  // documentNumber
                    "CNPJ",  // documentType
                    "NATURAL_PERSON",  // type
                    "email@domain.com",  // email
                    numeroTelefone  // phoneNumber
                ),
                "win32",  // platform
                "5120x1440",  // screenResolution
                "128GB",  // storageCapacity
                "America/Buenos_Aires",  // timeZone
                "ANDROID",  // type
                "123e4567-e89b-12d3-a456-426614174000"  // uuid
            );

            // Log the request
            traceService.logOperation(request.toString(), "ENVIO", "DEVICE_REGISTER", documentNumber, numeroConta, requestIdStr, "SUCESSO");

            // Call the feign client to register the device
            DeviceRegistrationResponse response = pixFeignClient.registerDevice(request);

            // Log the response with Arbi request ID
            traceService.logOperation(response.id(), "RESPOSTA", "DEVICE_REGISTER", documentNumber, numeroConta, requestIdStr, "SUCESSO");

            return response;
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("", "RESPOSTA", "DEVICE_REGISTER", identificacaoArbiProperties.inscricaoParceiro(), numeroConta, requestIdStr, errorMessage);

            throw new RuntimeException("Error registering device for agency: " + numeroAgencia +
                ", account: " + numeroConta +
                ", phone: " + numeroTelefone +
                " - Error: " + errorMessage, e);
        }
    }

    public DeviceListResponse[] listDevices() {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Log the request
            traceService.logOperation("", "ENVIO", "DEVICE_LIST", identificacaoArbiProperties.inscricaoParceiro(), "", requestIdStr, "SUCESSO");

            // Call the feign client to list devices
            DeviceListResponse[] response = pixFeignClient.listDevices();

            // Log the response with Arbi request ID
            if (response != null && response.length > 0) {
                traceService.logOperation(
                    java.util.Arrays.toString(response),
                    "RESPOSTA",
                    "DEVICE_LIST",
                    identificacaoArbiProperties.inscricaoParceiro(),
                    "",
                    requestIdStr,
                    "SUCESSO"
                );
            } else {
                traceService.logOperation("", "RESPOSTA", "DEVICE_LIST", identificacaoArbiProperties.inscricaoParceiro(), "", requestIdStr, "No devices returned");
            }

            return response;
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";

            // Log the error
            traceService.logOperation("", "RESPOSTA", "DEVICE_LIST", identificacaoArbiProperties.inscricaoParceiro(), "", requestIdStr, errorMessage);

            throw new RuntimeException("Error listing devices - Error: " + errorMessage, e);
        }
    }

    public PixPaymentStringResponse generatePixPaymentString(
            String documentoId,
            Double valor,
            Boolean reutilizavel,
            Integer calendarioExpiracaoSegundos) {

        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Generate a UUID for referenciaInterna
            String referenciaInterna = java.util.UUID.randomUUID().toString().replaceAll("-","");

            // Use default values for other required fields
            String recebedorCpf = "03740038748";  // Default CPF
            String chaveEnderecamento = "3b52661f-d875-4123-b372-8972fb155905";  // Default PIX key
            String tipoPix = "NORMAL";
            String nomeBeneficiario = "Previmil";  // Default beneficiary name
            String cidade = "RIO DE JANEIRO";  // Default city
            String codUsuario = "_autbank1";  // Default user code
            String codIspb = "54403563";  // Default ISPB code
            String documentoRevisao = "0";  // Default document revision

            // Create the request object
            PixPaymentStringRequest request = new PixPaymentStringRequest(
                    nomeBeneficiario,
                    cidade,
                    codUsuario,
                    codIspb,
                    referenciaInterna,
                    documentoId,
                    documentoRevisao,
                    recebedorCpf,
                    valor,
                    chaveEnderecamento,
                    reutilizavel,
                    calendarioExpiracaoSegundos,
                    tipoPix
            );

            // Log the request
            traceService.logOperation(request.toString(), "ENVIO", "PIX_PAYMENT_STRING", recebedorCpf, "", requestIdStr, "SUCESSO");

            // Call the feign client to generate the PIX payment string
            PixKeyResponse[] responseArray = pixFeignClient.generatePixPaymentString(request);

            // Log the response with Arbi request ID
            if (responseArray != null && responseArray.length > 0) {
                PixKeyResponse response = responseArray[0];

                // Parse the JSON string response into a PixPaymentStringResponse object
                PixPaymentStringResponse parsedResponse = objectMapper.readValue(
                    response.response(),
                    PixPaymentStringResponse.class
                );

                traceService.logOperation(response.response(), "RESPOSTA", "PIX_PAYMENT_STRING", recebedorCpf, "", requestIdStr, "SUCESSO");
                return parsedResponse;
            } else {
                traceService.logOperation("", "RESPOSTA", "PIX_PAYMENT_STRING", recebedorCpf, "", requestIdStr, "No response returned");
                throw new RuntimeException("No response returned for PIX payment string generation");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            String defaultRecebedorCpf = "03740038748";  // Default CPF used in the method

            // Log the error
            traceService.logOperation("", "RESPOSTA", "PIX_PAYMENT_STRING", defaultRecebedorCpf, "", requestIdStr, errorMessage);

            throw new RuntimeException("Error generating PIX payment string - Error: " + errorMessage, e);
        }
    }

    public EndToEndResponse processarPixEnd2End(PixEnd2EndProcessingRequest request) {
        // Generate a request ID
        Long requestId = requestPreparationService.generateRequestId();
        String requestIdStr = requestId.toString();

        try {
            // Get a valid access token
            AccessTokenResponse token = tokenManagementService.getAccessToken();

            // Get CPF/CNPJ from properties
            String cpfCnpj = identificacaoArbiProperties.inscricaoParceiro();

            // Create the request object for the Feign client
            PixProcessamentoRequest feignRequest = new PixProcessamentoRequest(
                cpfCnpj,
                request.paymentString()
            );

            // Log the request
            traceService.logOperation(feignRequest.toString(), "ENVIO", "PIX_END2END", cpfCnpj, "", requestIdStr, "SUCESSO");

            // Call the feign client to process the PIX end-to-end
            String responseJson = pixFeignClient.processarPixPayment(feignRequest);

            // Log the response with Arbi request ID
            String responseLog = responseJson != null ? responseJson : "";
            String status = "ERROR";
            String endToEnd = "No endToEnd returned";

            if (responseJson != null && !responseJson.isEmpty()) {
                try {
                    // Parse the outer JSON array
                    PixProcessamentoResponse[] responseArray = objectMapper.readValue(
                        responseJson,
                        PixProcessamentoResponse[].class
                    );

                    if (responseArray != null && responseArray.length > 0) {
                        PixProcessamentoResponse response = responseArray[0]; // Get the first response from the array
                        status = response != null ? response.status().toString() : "ERROR";

                        // Extract the endToEnd value from the inner JSON response string
                        if (response != null && response.response() != null) {
                            // Parse the inner response JSON string to extract endToEnd
                            java.util.Map<String, Object> responseMap = objectMapper.readValue(
                                response.response(),
                                new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {}
                            );

                            if (responseMap.containsKey("endToEnd")) {
                                endToEnd = responseMap.get("endToEnd").toString();
                            }
                        }
                    }
                } catch (Exception parseException) {
                    // If parsing fails, log the error and continue with default message
                    traceService.logOperation("Error parsing response JSON: " + parseException.getMessage(), "RESPOSTA", "PIX_END2END", cpfCnpj, "", requestIdStr, "JSON Parse Error");
                }
            }

            traceService.logOperation(responseLog, "RESPOSTA", "PIX_END2END", cpfCnpj, "", requestIdStr, status);

            return new EndToEndResponse(endToEnd);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            String cpfCnpj = identificacaoArbiProperties.inscricaoParceiro();

            // Log the error
            traceService.logOperation("", "RESPOSTA", "PIX_END2END", cpfCnpj, "", requestIdStr, errorMessage);

            throw new RuntimeException("Error processing PIX end-to-end - Error: " + errorMessage, e);
        }
    }
}