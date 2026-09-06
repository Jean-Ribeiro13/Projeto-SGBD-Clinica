
CREATE DATABASE clinica_medica;
USE clinica_medica;

CREATE TABLE Profissional_Saude (
    id_profissional INT AUTO_INCREMENT, -- Define um numero inteiro que cresce sozinho a cada novo cadastro, sendo a base de identificação
    nome VARCHAR(100) NOT NULL, -- Texto de até 100 caracteres. O "NOT NULL" impede que o usuario salve o registro sem preencher o nome
    cpf CHAR(11) NOT NULL, -- Como o dominio de CPF exige exatos 11 digitos numericos, "CHAR(11)" é mais eficiente que VARCHAR
    data_nascimento DATE NOT NULL, -- "DATE" guarda a data no formato AAAA-MM-DD
    
    CONSTRAINT pk_profissional PRIMARY KEY (id_profissional), -- Nomeia explicitamente a restrição de chave primária usando o prefixo pk_ exigido
    CONSTRAINT uq_profissional_cpf UNIQUE (cpf) -- Usa o prefixo uq_ para garantir a nível de banco de dados que não existirão CPFs repetidos
);

CREATE TABLE Medico (
    id_profissional INT,
    crm VARCHAR(20) NOT NULL, -- Numero de registro profissional obrigatorio de um medico
    id_supervisor INT, -- Campo opcional, não possui "NOT NULL" que guardará o ID do medico, criando hierarquia
    CONSTRAINT pk_medico PRIMARY KEY (id_profissional), -- Definem a chave
    CONSTRAINT uq_medico_crm UNIQUE (crm), -- Bloqueia CRMs duplicados
    CONSTRAINT fk_medico_profissional FOREIGN KEY (id_profissional) REFERENCES Profissional_Saude(id_profissional) ON DELETE CASCADE ON UPDATE CASCADE, -- Cria a ligação com a tabela "pai". "ON DELETE CASCADE" serve caso o cadastro base do profissional seja apagado, os dados de especialização de medico sumirão junto para não deixar residuos ou dados orfãos.
    CONSTRAINT fk_medico_supervisor FOREIGN KEY (id_supervisor) REFERENCES Medico(id_profissional) ON DELETE SET NULL ON UPDATE CASCADE -- Resolve o requisito de autorrelacionamento, "ON DELETE SET NULL" protege o banco, se o supervisor for excluido o campo do residente fica em branco, mas dai não é apagado acidentalmente.
);

CREATE TABLE Enfermeiro (
    id_profissional INT,
    coren VARCHAR(20) NOT NULL, -- O registro do conselho de enfermagem, exigido para salvar a linha
    CONSTRAINT pk_enfermeiro PRIMARY KEY (id_profissional), -- Nomeia a chave primaria com o prefico pk_
    CONSTRAINT uq_enfermeiro_coren UNIQUE (coren), -- Bloqueia a duplicidade do coren com o prefixo uq_
    CONSTRAINT fk_enfermeiro_profissional FOREIGN KEY (id_profissional) -- Amarração da herança, sendo assim, "ON DELETE CASCADE" vai garantir a exclusão do cadastro principal do profissional, apagando o registro de enfermeiro associado
        REFERENCES Profissional_Saude(id_profissional) 
        ON DELETE CASCADE ON UPDATE CASCADE
);