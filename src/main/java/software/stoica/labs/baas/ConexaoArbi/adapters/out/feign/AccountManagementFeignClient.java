package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import feign.Headers;
import software.stoica.labs.baas.ConexaoArbi.core.model.CadastroPFRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.CadastroPFResponse;

@FeignClient(
        name = "accountManagementFeignClient",
        url = "${banco.arbi.conexao.url}"
)
@Headers("Content-Type: application/json")
public interface AccountManagementFeignClient {

    @PostMapping("/cadastropf/v2/cadastropf")
    CadastroPFResponse[] submitKYC(@RequestBody CadastroPFRequest request);
}