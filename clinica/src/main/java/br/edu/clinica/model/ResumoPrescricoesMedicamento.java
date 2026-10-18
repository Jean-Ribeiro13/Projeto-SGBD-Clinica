package br.edu.clinica.model;

public record ResumoPrescricoesMedicamento(
        Integer idMedicamento,
        String nomeMedicamento,
        Long quantidadePrescricoes,
        Long quantidadePacientes
) {
}
