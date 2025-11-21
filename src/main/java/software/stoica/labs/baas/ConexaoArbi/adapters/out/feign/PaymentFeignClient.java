package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import feign.Headers;
import software.stoica.labs.baas.ConexaoArbi.core.model.PaymentRequest;
import software.stoica.labs.baas.ConexaoArbi.core.model.PaymentResponse;

@FeignClient(
        name = "paymentFeignClient",
        url = "${banco.arbi.conexao.url}"
)
@Headers("Content-Type: application/json")
public interface PaymentFeignClient {

    @PostMapping("/pix/v2/operacao/ordem_pagamento")
    PaymentResponse[] processPayment(@RequestBody PaymentRequest request);
}