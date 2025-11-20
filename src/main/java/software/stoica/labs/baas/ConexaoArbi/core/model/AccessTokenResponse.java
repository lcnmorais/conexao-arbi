package software.stoica.labs.baas.ConexaoArbi.core.model;

public record AccessTokenResponse(String access_token, String token_type, Integer expires_in, String refresh_token, String scope) {}