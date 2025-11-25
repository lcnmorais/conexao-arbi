package software.stoica.labs.baas.ConexaoArbi.core.model;

import java.time.LocalDateTime;

public record DeviceListResponse(
    String id,
    DeviceAccount account,
    String alias,
    DevicePerson person,
    String type,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}