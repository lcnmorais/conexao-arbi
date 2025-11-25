package software.stoica.labs.baas.ConexaoArbi.core.model;

public record DeviceRegistrationRequest(
    DeviceAccount account,
    String alias,
    String androidId,
    String canvasHash,
    Boolean cookiesEnabled,
    String densityDPI,
    String fingerPrint,
    String idfv,
    String imei,
    Boolean isRooted,
    String language,
    String manufacturer,
    String model,
    DevicePerson person,
    String platform,
    String screenResolution,
    String storageCapacity,
    String timeZone,
    String type,
    String uuid
) {}