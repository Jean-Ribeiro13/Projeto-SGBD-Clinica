package br.edu.clinica.model;

import java.math.BigDecimal;

public record ResumoConsultasMedico(
        Integer idProfissional,
        String nomeMedico,
        String crm,
        Long quantidadeConsultas,
        BigDecimal valorTotal
) {
}
