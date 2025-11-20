package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import feign.Headers;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.ContaCorrenteResponse;

@FeignClient(
        name = "accountsFeignClient",
        url = "${banco.arbi.conexao.url}"
)
@Headers("Content-Type: application/json")
public interface AccountsFeignClient {

    @PostMapping("/contacorrente/v2/contacorrente")
    ContaCorrenteResponse[] getBalance(@RequestBody ContaCorrenteRequest request);
}