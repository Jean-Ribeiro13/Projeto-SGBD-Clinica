package br.edu.clinica.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class MedicoEspecialidadeFormulario {
    @NotNull(message = "Selecione um médico.")
    private Integer idMedico;

    @NotNull(message = "Selecione uma especialidade.")
    private Integer idEspecialidade;

    @NotNull(message = "Informe a data de obtenção.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataObtencao;

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public Integer getIdEspecialidade() {
        return idEspecialidade;
    }

    public void setIdEspecialidade(Integer idEspecialidade) {
        this.idEspecialidade = idEspecialidade;
    }

    public LocalDate getDataObtencao() {
        return dataObtencao;
    }

    public void setDataObtencao(LocalDate dataObtencao) {
        this.dataObtencao = dataObtencao;
    }
}
