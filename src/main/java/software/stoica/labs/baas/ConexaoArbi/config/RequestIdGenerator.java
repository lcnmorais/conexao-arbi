package software.stoica.labs.baas.ConexaoArbi.config;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RequestIdGenerator {
    
    public Long generateRequestId() {
        // Generate a unique request ID based on current timestamp
        return Instant.now().toEpochMilli();
    }
    
    public String generateFormattedDate() {
        // Generate date in the format "YYYY-MM-DD" as used in the script
        return java.time.LocalDate.now().toString();
    }
}