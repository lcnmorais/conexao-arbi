package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import feign.Headers;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.AccessTokenResponse;

@FeignClient(
        name = "accessTokenFeignClient",
    url = "${banco.arbi.conexao.url}"
)
@Headers("Content-Type: application/json")
public interface AccessTokenFeignClient {

    @PostMapping("/oauth/access-token")
    AccessTokenResponse getAccessToken(
        @RequestBody AccessTokenRequest request
    );
}