package br.edu.clinica.model;

import java.time.LocalDate;

public record MedicoEspecialidade(
        Integer idMedico,
        String nomeMedico,
        String crm,
        Integer idEspecialidade,
        String nomeEspecialidade,
        LocalDate dataObtencao) {
}
