package br.edu.clinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EspecialidadeFormulario {
    @NotBlank(message = "Informe o nome da especialidade.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}