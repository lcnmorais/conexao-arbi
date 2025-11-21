package software.stoica.labs.baas.ConexaoArbi.core.model;

public record CadastroPFResponse(
    Integer idmodulo,
    Integer idtransacao,
    String idrequisicaoarbi,
    String idrequisicaoparceiro,
    Integer idstatus,
    String descricaostatus,
    String resultado
) {}