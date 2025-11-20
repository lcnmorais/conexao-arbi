package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import feign.Headers;
import software.stoica.labs.baas.ConexaoArbi.core.model.GrantCodeRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.GrantCodeResponse;

@FeignClient(
    name = "grantCodeFeignClient",
    url = "${banco.arbi.conexao.url}"
)
@Headers("Content-Type: application/json")
public interface GrantCodeFeignClient {

    @PostMapping("/oauth/grant-code")
    GrantCodeResponse getCodeGrant(
        @RequestBody GrantCodeRequest request
    );

}