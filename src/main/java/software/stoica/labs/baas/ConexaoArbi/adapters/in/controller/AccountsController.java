package software.stoica.labs.baas.ConexaoArbi.adapters.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.stoica.labs.baas.ConexaoArbi.core.services.AccountsService;

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
            Map<String, Object> response = accountsService.getAccountBalance(accountNumber, fromDate, toDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of("error", "Failed to retrieve account balance: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}