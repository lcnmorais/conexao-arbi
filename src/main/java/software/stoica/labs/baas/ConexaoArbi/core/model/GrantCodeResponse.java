package software.stoica.labs.baas.ConexaoArbi.core.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record GrantCodeResponse(String redirect_uri) {

    /**
     * Método para extrair o código de autorização isoladamente do redirect_uri
     * @return código de autorização ou null se não encontrado
     */
    public String extractCode() {
        if (redirect_uri == null || redirect_uri.isEmpty()) {
            return null;
        }

        // Padrão para capturar o código após "?code="
        Pattern pattern = Pattern.compile("code=([a-fA-F0-9\\-]+)");
        Matcher matcher = pattern.matcher(redirect_uri);

        if (matcher.find()) {
            return matcher.group(1); // Retorna o código capturado
        }

        return null; // Retorna null se não encontrar o código
    }
}