package software.stoica.labs.baas.ConexaoArbi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "bancor.arbi.base-url=https://gapp-homolog.bancoarbi.com.br",
    "bancor.arbi.client-id=dummy-id",
    "bancor.arbi.redirect-uri=http://localhost/"
})
class ConexaoArbiApplicationTests {

	@Test
	void contextLoads() {
	}

}