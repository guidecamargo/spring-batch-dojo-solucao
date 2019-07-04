package br.com.ciandt.dojo.model;

public class Cliente {

    private String nome;
    private String sobrenome;
    private String regiao;

    public Cliente() {

    }

    public Cliente(final String nome, final String sobrenome, final String regiao) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.regiao = regiao;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return this.sobrenome;
    }

    public void setSobrenome(final String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getRegiao() {
        return this.regiao;
    }

    public void setRegiao(final String regiao) {
        this.regiao = regiao;
    }

    @Override
    public String toString() {
        return "Cliente [nome=" + this.nome + ", sobrenome=" + this.sobrenome + ", regiao=" + this.regiao + "]";
    }
}
