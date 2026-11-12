package br.edu.clinica.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Cadastro feito pelo próprio paciente. A data de nascimento é obrigatória porque identifica o paciente no agendamento. */
public class AutoCadastroFormulario {

    @NotBlank(message = "Informe seu nome completo.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "Informe o CPF.")
    @Pattern(regexp = "[0-9]{11}", message = "O CPF deve conter exatamente 11 números, sem pontos ou traço.")
    private String cpf;

    @NotNull(message = "Informe a data de nascimento.")
    @Past(message = "A data de nascimento deve estar no passado.")
    private LocalDate dataNascimento;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}
