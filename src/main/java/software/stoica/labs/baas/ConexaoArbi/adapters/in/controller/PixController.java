package software.stoica.labs.baas.ConexaoArbi.adapters.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.stoica.labs.baas.ConexaoArbi.core.model.*;
import software.stoica.labs.baas.ConexaoArbi.core.model.IdentificacaoArbiProperties;
import software.stoica.labs.baas.ConexaoArbi.core.services.PixService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pix")
public class PixController {

    @Autowired
    private PixService pixService;

    @Autowired
    private IdentificacaoArbiProperties identificacaoArbiProperties;

    @GetMapping("/keys/{numeroCPFCNPJ}/{numeroAgencia}/{numeroConta}")
    public ResponseEntity<?> obterChavesPixPorDocumentoAgenciaConta(
            @PathVariable String numeroCPFCNPJ,
            @PathVariable String numeroAgencia,
            @PathVariable String numeroConta) {
        try {
            List<PixKeyData> response = pixService.obterChavesPixPorDocumentoAgenciaConta(numeroCPFCNPJ, numeroAgencia, numeroConta);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to retrieve PIX keys: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/mock/payment")
    public ResponseEntity<?> obterChavesPixMock() {
        try {
            String cpfCnpj = identificacaoArbiProperties.inscricaoParceiro();
            String conta = identificacaoArbiProperties.contaMockExterno();

            // Assuming agency is always "0001" for mock accounts based on other configurations
            String agencia = "0001";

            List<PixKeyData> response = pixService.obterChavesPixPorDocumentoAgenciaConta(cpfCnpj, agencia, conta);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to retrieve mock PIX keys: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/mock/receive")
    public ResponseEntity<?> obterChavesPix() {
        try {
            String cpfCnpj = identificacaoArbiProperties.inscricaoParceiro();
            String conta = identificacaoArbiProperties.contaPrevimil();

            // Assuming agency is always "0001" for mock accounts based on other configurations
            String agencia = "0001";

            List<PixKeyData> response = pixService.obterChavesPixPorDocumentoAgenciaConta(cpfCnpj, agencia, conta);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to retrieve mock PIX keys: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/device/register")
    public ResponseEntity<?> registerDevice(
            @RequestParam String numeroAgencia,
            @RequestParam String numeroConta,
            @RequestParam String numeroTelefone) {
        try {
            var response = pixService.registerDevice(numeroAgencia, numeroConta, numeroTelefone);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to register device: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/device/list")
    public ResponseEntity<?> listDevices() {
        try {
            var response = pixService.listDevices();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to list devices: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/payment-string")
    public ResponseEntity<?> generatePixPaymentString(@RequestBody GeneratePixPaymentStringRequest request) {
        try {
            var response = pixService.generatePixPaymentString(
                    request.documentoId(),
                    request.valor(),
                    request.reutilizavel(),
                    request.calendarioExpiracaoSegundos()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to generate PIX payment string: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/generate-end2end")
    public ResponseEntity<?> processarPixEnd2End(@RequestBody PixEnd2EndProcessingRequest request) {
        try {
            var response = pixService.processarPixEnd2End(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to process PIX end-to-end: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<?> realizarPixPayment(@RequestBody PixPaymentRequest request) {
        try {
            var response = pixService.realizarPixPayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to perform PIX payment: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}