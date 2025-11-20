package software.stoica.labs.baas.ConexaoArbi.adapters.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;
import software.stoica.labs.baas.ConexaoArbi.core.services.TokenManagementService;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private static final Logger logger = Logger.getLogger(TokenController.class.getName());

    @Autowired
    private TokenManagementService tokenManagementService;
    
    @PostMapping("/connect")
    public ResponseEntity<AccessTokenResponse> connect() {
        try {
            AccessTokenResponse tokenResponse = tokenManagementService.getAccessToken();
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken() {
        try {
            AccessTokenResponse tokenResponse = tokenManagementService.refreshAccessToken();
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}