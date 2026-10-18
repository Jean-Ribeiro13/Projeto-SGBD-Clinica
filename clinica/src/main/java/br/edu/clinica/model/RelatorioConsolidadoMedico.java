package br.edu.clinica.model;

import java.math.BigDecimal;

public record RelatorioConsolidadoMedico(
        Integer idMedico,
        String nomeMedico,
        String crm,
        String especialidades,
        Long quantidadeConsultas,
        Long quantidadePacientes,
        Long quantidadePrescricoes,
        BigDecimal valorTotal,
        BigDecimal valorMedio
) {
}
