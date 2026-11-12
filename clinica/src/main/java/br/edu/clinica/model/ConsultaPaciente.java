package br.edu.clinica.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Consulta como o paciente a enxerga: quando, com quem e em qual especialidade. */
public record ConsultaPaciente(LocalDateTime dataHora, String medico, String especialidades) {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public String dataFormatada() {
        return dataHora.format(DATA);
    }

    public String horaFormatada() {
        return dataHora.format(HORA);
    }
}
