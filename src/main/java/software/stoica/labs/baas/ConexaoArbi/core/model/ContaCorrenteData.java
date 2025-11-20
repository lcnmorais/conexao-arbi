package software.stoica.labs.baas.ConexaoArbi.core.model;

public record ContaCorrenteData(
    String inscricaoparceiro,
    String tokenusuario,
    String idrequisicao,
    String idmodulo,
    String idtransacao,
    String bancoorigem,
    String agenciaorigem,
    String contaorigem,
    String tipocontadebitada,
    String bancodestino,
    String agenciadestino,
    String contadestino,
    String tipocontacreditada,
    String cnpjcpfclicred,
    String nomeclicred,
    String tipopessoaclicred,
    String finalidade,
    String historico,
    String dataagendamento,
    String valor,
    String datainicial,
    String datafinal,
    String periodoemdias,
    String canalentrada
) {}