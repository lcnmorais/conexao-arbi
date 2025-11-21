package software.stoica.labs.baas.ConexaoArbi.core.services;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TraceService {

    private static final String TRACE_FILE = "accounts.dat";

    public void logOperation(String idRequisicaoArbi, String direcao, String operacao, String cpf, String conta, String idRequisicaoParceiro, String situacao) {
        try (PrintWriter out = new PrintWriter(new FileWriter(TRACE_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logLine = String.format("%s;%s;%s;%s;%s;%s;%s;%s",
                    timestamp,
                    idRequisicaoArbi != null ? idRequisicaoArbi : "",
                    direcao,
                    operacao,
                    cpf != null ? cpf : "",
                    conta != null ? conta : "",
                    idRequisicaoParceiro != null ? idRequisicaoParceiro : "",
                    situacao);
            out.println(logLine);
        } catch (IOException e) {
            System.err.println("Error writing to trace file: " + e.getMessage());
        }
    }
}