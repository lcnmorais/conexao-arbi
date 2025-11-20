package software.stoica.labs.baas.ConexaoArbi.adapters.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.stoica.labs.baas.ConexaoArbi.core.services.AccountsService;
import software.stoica.labs.baas.ConexaoArbi.core.model.TransactionHistory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    @Autowired
    private AccountsService accountsService;

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getAccountBalance(
            @PathVariable String accountNumber,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        try {
            // The service returns the response list from the API
            var responseList = accountsService.getAccountBalance(accountNumber, fromDate, toDate);

            // Return the list with additional metadata
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to retrieve account balance: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/{accountNumber}/history")
    public ResponseEntity<?> getAccountHistory(
            @PathVariable String accountNumber,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        try {
            // The service returns the parsed transaction history list
            List<TransactionHistory> responseList = accountsService.getAccountHistory(accountNumber, fromDate, toDate);

            // Return the list with additional metadata
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getName() + " occurred without a message";
            Map<String, Object> errorResponse = Map.of("error", "Failed to retrieve account history: " + errorMessage);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
}