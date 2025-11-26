package software.stoica.labs.baas.ConexaoArbi.adapters.out.feign;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import software.stoica.labs.baas.ConexaoArbi.core.model.*;

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

    @PostMapping("/device-manager/api/v1/devices")
    DeviceRegistrationResponse registerDevice(
            @RequestBody DeviceRegistrationRequest request);

    @GetMapping("/device-manager/api/v1/devices")
    DeviceListResponse[] listDevices();

    @PostMapping("/pix/v2/qrcode/dinamico/imediato/v2")
    PixKeyResponse[] generatePixPaymentString(@RequestBody PixPaymentStringRequest request);

    @PostMapping("/pix/v2/qrcode/processamento")
    String processarPixPayment(@RequestBody PixProcessamentoRequest request);

    @PostMapping("/pix/v2/operacao/ordem_pagamento/")
    String realizarPixPayment(@RequestBody PixPaymentInternalRequest request);
}