package software.stoica.labs.baas.ConexaoArbi.core.model;

public record DeviceAccount(
    String number,
    DevicePspInformation pspInformation
) {}