package software.stoica.labs.baas.ConexaoArbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.stoica.labs.baas.ConexaoArbi.core.model.CredencialPrevimilArbi;
import software.stoica.labs.baas.ConexaoArbi.core.model.IdentificacaoArbiProperties;
import software.stoica.labs.baas.ConexaoArbi.core.model.ConexaoArbiProperties;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties({
	CredencialPrevimilArbi.class,
	IdentificacaoArbiProperties.class,
	ConexaoArbiProperties.class
})
@EnableScheduling
public class ConexaoArbiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConexaoArbiApplication.class, args);
	}

}
