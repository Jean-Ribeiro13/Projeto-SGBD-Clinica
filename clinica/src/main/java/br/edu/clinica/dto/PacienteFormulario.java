package br.edu.clinica.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PacienteFormulario {

    @NotBlank(message = "Informe o nome do paciente.") // @NotBlank rejeita null, texto vazio e texto composto apenas por espaços.
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.") // @Size(max = 100) limita o tamanho à capacidade da coluna nome no banco.
    private String nome;

    @NotBlank(message = "Informe o CPF.") // @NotBlank exige o preenchimento
    @Pattern( // @Pattern verifica o formato
            regexp = "[0-9]{11}", // Aqui ele permite um número de 0 a 9 e exige exatamente 11 ocorrências ou caracteres
            message = "O CPF deve conter exatamente 11 números, sem pontos ou traço."
    )
    private String cpf;
    private LocalDate dataNascimento;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
