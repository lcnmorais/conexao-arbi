package software.stoica.labs.baas.ConexaoArbi.adapters.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import software.stoica.labs.baas.ConexaoArbi.core.model.FundingRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.PaymentResponse;
import software.stoica.labs.baas.ConexaoArbi.core.services.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/funding/{nroContaPagador}")
    public ResponseEntity<?> processPayment(
            @PathVariable String nroContaPagador,
            @RequestBody FundingRequest paymentRequest) {
        try {
            PaymentResponse response = paymentService.processFunding(nroContaPagador, paymentRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to process payment: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}