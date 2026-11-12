package br.edu.clinica.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public class AgendamentoFormulario {

    @NotNull(message = "Escolha o médico.")
    private Integer idMedico;

    @NotNull(message = "Escolha a data da consulta.")
    private LocalDate data;

    @NotBlank(message = "Escolha um horário.")
    @Pattern(regexp = "([01][0-9]|2[0-3]):[0-5][0-9]", message = "Horário inválido.")
    private String hora;

    @NotBlank(message = "Informe o CPF.")
    @Pattern(regexp = "[0-9]{11}", message = "O CPF deve conter exatamente 11 números, sem pontos ou traço.")
    private String cpf;

    @NotNull(message = "Informe a data de nascimento.")
    @Past(message = "A data de nascimento deve estar no passado.")
    private LocalDate dataNascimento;

    public Integer getIdMedico() { return idMedico; }
    public void setIdMedico(Integer idMedico) { this.idMedico = idMedico; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}
