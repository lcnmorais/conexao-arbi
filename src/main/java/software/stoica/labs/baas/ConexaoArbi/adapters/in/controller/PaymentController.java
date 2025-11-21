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
import software.stoica.labs.baas.ConexaoArbi.core.model.SweepRequest;
import software.stoica.labs.baas.ConexaoArbi.core.services.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/funding/{nroContaPagador}")
    public ResponseEntity<?> processFunding(
            @PathVariable String nroContaPagador,
            @RequestBody FundingRequest fundingRequest) {
        try {
            PaymentResponse response = paymentService.processFunding(nroContaPagador, fundingRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to process funding: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/sweep/{numeroConta}")
    public ResponseEntity<?> processSweep(
            @PathVariable String numeroConta,
            @RequestBody SweepRequest sweepRequest) {
        try {
            PaymentResponse response = paymentService.processSweep(numeroConta, sweepRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to process sweep: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}