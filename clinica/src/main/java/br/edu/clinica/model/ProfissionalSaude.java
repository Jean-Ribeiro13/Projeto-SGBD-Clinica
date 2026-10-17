package br.edu.clinica.model;

import java.time.LocalDate;

public record ProfissionalSaude(Integer id, String nome, String cpf, LocalDate dataNascimento,
                                String tipo, String registro, Integer supervisorId, String supervisorNome) {
}
