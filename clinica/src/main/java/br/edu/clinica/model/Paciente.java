package br.edu.clinica.model;

import java.time.LocalDate;

/*
    Esta classe define quais informações de um paciente a aplicação utiliza: idPaciente, nome, cpf
    e dataNascimento

    Cada registro retornado pelo banco vira um objeto dessa clase. Se a consulta retornar 40 pacientes
    o programa terá uma lista com 40 objetos 'Paciente'.

    O construtor recebe os dados para criar o objeto. Os metodos getters, permitem consultar esses valores.
    Quando o HTML utiliza ${paciente.nome}, o Thymeleaf acessa o nome por meio de getNome().

    Essa classe não executa SQL nem cria tabela no MySQL. Ela representa, dentro do programa, os dados
    que estão no banco.
 */


public class Paciente {
    private final Integer idPaciente;
    private final String nome;
    private final String cpf;
    private final LocalDate dataNascimento;

    public Paciente(Integer idPaciente, String nome, String cpf, LocalDate dataNascimento) {
        this.idPaciente = idPaciente;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
    }

    public Integer getIdPaciente() { return idPaciente; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; }
}
