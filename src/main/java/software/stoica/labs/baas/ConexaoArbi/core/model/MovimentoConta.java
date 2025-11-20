package software.stoica.labs.baas.ConexaoArbi.core.model;

import java.math.BigDecimal;

public class MovimentoConta {
    private String data;
    private String descricao;
    private String tipo;
    private BigDecimal valor;
    private BigDecimal saldoAposMovimento;

    public MovimentoConta(String data, String descricao, String tipo, BigDecimal valor, BigDecimal saldoAposMovimento) {
        this.data = data;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
        this.saldoAposMovimento = saldoAposMovimento;
    }

    // Getters
    public String getData() { return data; }
    public String getDescricao() { return descricao; }
    public String getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getSaldoAposMovimento() { return saldoAposMovimento; }

    // Setters
    public void setData(String data) { this.data = data; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public void setSaldoAposMovimento(BigDecimal saldoAposMovimento) { this.saldoAposMovimento = saldoAposMovimento; }
}