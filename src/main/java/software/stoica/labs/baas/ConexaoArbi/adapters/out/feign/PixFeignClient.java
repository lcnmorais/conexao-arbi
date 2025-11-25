package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import feign.Headers;
import software.stoica.labs.baas.ConexaoArbi.core.model.PixKeyResponse;

@FeignClient(
        name = "pixFeignClient",
        url = "${banco.arbi.conexao.url}"
)
@Headers("Content-Type: application/json")
public interface PixFeignClient {

    @GetMapping("/pix/v2/enderecamento/cliente/lista_por_conta/{numeroAgencia}/{numeroConta}")
    PixKeyResponse[] obterChavesPixPorDocumentoAgenciaConta(
            @PathVariable("numeroAgencia") String numeroAgencia,
            @PathVariable("numeroConta") String numeroConta);
}