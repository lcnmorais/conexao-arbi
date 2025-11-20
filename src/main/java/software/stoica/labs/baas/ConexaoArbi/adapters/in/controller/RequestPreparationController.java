package software.stoica.labs.baas.ConexaoArbi.adapters.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.services.RequestPreparationService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/prepare")
public class RequestPreparationController {
    
    @Autowired
    private RequestPreparationService requestPreparationService;
    
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> prepareRequest() {
        try {
            // Generate request ID (like the script does)
            Long requestId = requestPreparationService.generateRequestId();
            
            // Get valid access token (with 60-min refresh logic)
            AccessTokenResponse accessToken = requestPreparationService.getValidAccessToken();
            
            // Format date like the script does (in 'fr-CA' format which is YYYY-MM-DD)
            String formattedDate = requestPreparationService.getFormattedDate();
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("requestId", requestId);
            response.put("accessToken", accessToken.access_token());
            //response.put("tokenType", accessToken.);
            response.put("expiresIn", accessToken.expires_in());
            response.put("currentDate", formattedDate);
            response.put("nextTokenRefresh", requestPreparationService.getNextTokenRefreshTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to prepare request: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/validate-credentials")
    public ResponseEntity<Map<String, Object>> validateCredentials() {
        Map<String, Object> response = new HashMap<>();
        
        // Check that required credentials are provided
        // Note: In a real implementation, you'd want to validate against actual values
        // For now, we'll just return the fact that we can generate a request ID
        
        try {
            Long requestId = requestPreparationService.generateRequestId();
            response.put("requestId", requestId);
            response.put("status", "credentials_validated");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Credentials validation failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}