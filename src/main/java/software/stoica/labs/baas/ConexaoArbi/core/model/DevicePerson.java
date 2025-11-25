package software.stoica.labs.baas.ConexaoArbi.core.model;

public record DevicePerson(
    String fullName,
    String documentNumber,
    String documentType,
    String type,
    String email,
    String phoneNumber
) {}