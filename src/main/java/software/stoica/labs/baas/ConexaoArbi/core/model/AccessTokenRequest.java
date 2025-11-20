package software.stoica.labs.baas.ConexaoArbi.core.model;

public record AccessTokenRequest(String grant_type, String code) {}