package software.stoica.labs.baas.ConexaoArbi.core.model;

import java.util.List;

public record ContaCorrenteResponse(
    Integer idmodulo,
    Integer idtransacao,
    String idrequisicaoarbi,
    String idrequisicaoparceiro,
    Integer idstatus,
    String descricaostatus,
    String resultado
) {}