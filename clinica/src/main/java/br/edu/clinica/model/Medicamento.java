package br.edu.clinica.model;

public class Medicamento {

    private int idMedicamento;
    private String nome;

    public Medicamento(int idMedicamento, String nome) {
        this.idMedicamento = idMedicamento;
        this.nome = nome;
    }

    public int getIdMedicamento() {
        return idMedicamento;
    }

    public String getNome() {
        return nome;
    }
}
