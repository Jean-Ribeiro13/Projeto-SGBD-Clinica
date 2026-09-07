
CREATE DATABASE clinica_medica;
USE clinica_medica;

CREATE TABLE Profissional_Saude (
    id_profissional INT AUTO_INCREMENT, -- Define um numero inteiro que cresce sozinho a cada novo cadastro, sendo a base de identificação
    nome VARCHAR(100) NOT NULL, -- Texto de até 100 caracteres. O "NOT NULL" impede que o usuario salve o registro sem preencher o nome
    cpf CHAR(11) NOT NULL, -- Como o dominio de CPF exige exatos 11 digitos numericos, "CHAR(11)" é mais eficiente que VARCHAR
    data_nascimento DATE NOT NULL, -- "DATE" guarda a data no formato AAAA-MM-DD
    
    CONSTRAINT pk_profissional PRIMARY KEY (id_profissional), -- Nomeia explicitamente a restrição de chave primária usando o prefixo pk_ exigido
    CONSTRAINT uq_profissional_cpf UNIQUE (cpf), -- Usa o prefixo uq_ para garantir a nível de banco de dados que não existirão CPFs repetidos
    CONSTRAINT chk_profissional_cpf CHECK (cpf REGEXP '^[0-9]{11}$') -- Garante que o CPF possua exatamente 11 caracteres numéricos
);

CREATE TABLE Medico (
    id_profissional INT, -- Atua simultaneamente como PK e FK, não usei "AUTO_INCREMENT" aqui, o ID vai ser inserido manualmente copiando o ID gerado na tabela pai ('Profissional_Saude') para amarrar a herança 1:1 | Linha que faltava na primeira versão
    crm VARCHAR(20) NOT NULL, -- Numero de registro profissional obrigatorio de um medico
    id_supervisor INT, -- Campo opcional, não possui "NOT NULL" que guardará o ID do medico, criando hierarquia
    CONSTRAINT pk_medico PRIMARY KEY (id_profissional), -- Definem a chave
    CONSTRAINT uq_medico_crm UNIQUE (crm), -- Bloqueia CRMs duplicados
    CONSTRAINT fk_medico_profissional FOREIGN KEY (id_profissional) REFERENCES Profissional_Saude(id_profissional) ON DELETE CASCADE ON UPDATE CASCADE, -- Cria a ligação com a tabela "pai". "ON DELETE CASCADE" serve caso o cadastro base do profissional seja apagado, os dados de especialização de medico sumirão junto para não deixar residuos ou dados orfãos.
    CONSTRAINT fk_medico_supervisor FOREIGN KEY (id_supervisor) REFERENCES Medico(id_profissional) ON DELETE SET NULL ON UPDATE CASCADE -- Resolve o requisito de autorrelacionamento, "ON DELETE SET NULL" protege o banco, se o supervisor for excluido o campo do residente fica em branco, mas dai não é apagado acidentalmente.
);

CREATE TABLE Enfermeiro (
    id_profissional INT, -- Exatamente como no 'Medico', esse campo não é "AUTO_INCREMENT". Ele recebe o mesmo numero gerado na tabela 'Profissional_Saude' para garantir a relação 1:1 de herança | Linha que faltava na primeira versão
    coren VARCHAR(20) NOT NULL, -- O registro do conselho de enfermagem, exigido para salvar a linha
    CONSTRAINT pk_enfermeiro PRIMARY KEY (id_profissional), -- Nomeia a chave primaria com o prefico pk_
    CONSTRAINT uq_enfermeiro_coren UNIQUE (coren), -- Bloqueia a duplicidade do coren com o prefixo uq_
    CONSTRAINT fk_enfermeiro_profissional FOREIGN KEY (id_profissional) -- Amarração da herança, sendo assim, "ON DELETE CASCADE" vai garantir a exclusão do cadastro principal do profissional, apagando o registro de enfermeiro associado
        REFERENCES Profissional_Saude(id_profissional) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Paciente (
    id_paciente INT AUTO_INCREMENT, -- Identificador unico do paciente, gerado automaticamente a cada novo cadastro
    nome VARCHAR(100) NOT NULL, -- Nome completo do paciente, obrigatorio para permitir o cadastro
    cpf CHAR(11) NOT NULL, -- Armazena o CPF com tamanho fixo de 11 caracteres
    data_nascimento DATE, -- Guarda a data de nascimento do paciente no formato de data do banco

    CONSTRAINT pk_paciente -- Define id_paciente como chave primaria da tabela
        PRIMARY KEY (id_paciente),

    CONSTRAINT uq_paciente_cpf -- Impede que dois pacientes sejam cadastrados com o mesmo CPF
        UNIQUE (cpf)
);

CREATE TABLE Contato_Emergencia (
    id_paciente INT NOT NULL, -- Identifica a qual paciente este contato de emergencia pertence
    nome VARCHAR(100) NOT NULL, -- Nome do contato de emergencia, de preenchimento obrigatorio
    telefone VARCHAR(20) NOT NULL, -- Telefone do contato, usado tambem como parte da chave primaria composta
    parentesco VARCHAR(50), -- Informa o grau de parentesco ou relacao do contato com o paciente

    CONSTRAINT pk_contato_emergencia -- Cria uma chave primaria composta por paciente e telefone, evitando repeticao do mesmo contato para o paciente
        PRIMARY KEY (id_paciente, telefone),

    CONSTRAINT fk_contato_paciente -- Liga o contato ao paciente correspondente; ao excluir o paciente, seus contatos tambem sao apagados
        FOREIGN KEY (id_paciente)
        REFERENCES Paciente(id_paciente)
        ON DELETE CASCADE
);

CREATE TABLE Prontuario (
    id_prontuario INT AUTO_INCREMENT, -- Identificador unico do prontuario, gerado automaticamente
    id_paciente INT NOT NULL, -- FK que associa o prontuario a um paciente especifico
    tipo_sanguineo VARCHAR(3), -- Armazena o tipo sanguineo do paciente, limitado aos valores aceitos pelo CHECK

    CONSTRAINT pk_prontuario -- Define o identificador do prontuario como chave primaria
        PRIMARY KEY (id_prontuario),

    CONSTRAINT uq_prontuario_paciente -- Garante a relacao 1:1, permitindo apenas um prontuario para cada paciente
        UNIQUE (id_paciente),

    CONSTRAINT fk_prontuario_paciente -- Cria a ligacao entre prontuario e paciente
        FOREIGN KEY (id_paciente)
        REFERENCES Paciente(id_paciente),

    CONSTRAINT chk_tipo_sanguineo -- Restringe o tipo sanguineo aos oito valores validos listados
        CHECK (tipo_sanguineo IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'))
);

CREATE TABLE Evolucao_Clinica (
    id_evolucao INT AUTO_INCREMENT, -- Identificador unico de cada registro de evolucao clinica
    id_prontuario INT NOT NULL, -- Indica em qual prontuario a evolucao sera registrada
    id_profissional INT NOT NULL, -- Identifica o profissional de saude responsavel pelo registro
    data_ocorrencia DATETIME NOT NULL, -- Guarda a data e a hora em que a evolucao clinica ocorreu
    descricao TEXT NOT NULL, -- Texto obrigatorio com a descricao detalhada da evolucao do paciente

    CONSTRAINT pk_evolucao_clinica -- Define id_evolucao como chave primaria
        PRIMARY KEY (id_evolucao),

    CONSTRAINT fk_evolucao_prontuario -- Relaciona a evolucao ao prontuario; ao apagar o prontuario, suas evolucoes sao apagadas junto
        FOREIGN KEY (id_prontuario)
        REFERENCES Prontuario(id_prontuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_evolucao_profissional -- Relaciona a evolucao ao profissional e impede excluir um profissional que ainda possua evolucoes registradas
        FOREIGN KEY (id_profissional)
        REFERENCES Profissional_Saude(id_profissional)
        ON DELETE RESTRICT
);

CREATE TABLE Consulta (
    id_consulta INT AUTO_INCREMENT, -- Identificador unico da consulta, gerado automaticamente
    id_paciente INT NOT NULL, -- Identifica o paciente atendido na consulta
    id_medico INT NOT NULL, -- Identifica o medico responsavel pelo atendimento
    data_hora DATETIME NOT NULL, -- Armazena a data e o horario agendados para a consulta
    valor DECIMAL(10,2) NOT NULL, -- Valor monetario da consulta, com duas casas decimais e preenchimento obrigatorio

    CONSTRAINT pk_consulta -- Define id_consulta como chave primaria
        PRIMARY KEY (id_consulta),

    CONSTRAINT fk_consulta_paciente -- Liga cada consulta ao paciente atendido
        FOREIGN KEY (id_paciente)
        REFERENCES Paciente(id_paciente),

    CONSTRAINT fk_consulta_medico -- Liga a consulta a um registro existente na especializacao Medico
        FOREIGN KEY (id_medico)
        REFERENCES Medico(id_profissional),

    CONSTRAINT uq_medico_data_hora -- Impede que o mesmo medico possua duas consultas marcadas exatamente no mesmo horario
        UNIQUE (id_medico, data_hora),

    CONSTRAINT chk_consulta_valor -- Impede o cadastro de consultas com valor negativo
        CHECK (valor >= 0)
);

CREATE TABLE Medicamento (
    id_medicamento INT AUTO_INCREMENT, -- Identificador unico do medicamento, gerado automaticamente
    nome VARCHAR(100) NOT NULL, -- Nome do medicamento, obrigatorio para o cadastro

    CONSTRAINT pk_medicamento -- Define id_medicamento como chave primaria
        PRIMARY KEY (id_medicamento)
);

CREATE TABLE Prescricao (
    id_prescricao INT AUTO_INCREMENT, -- Identificador unico de cada prescricao, gerado automaticamente
    id_consulta INT NOT NULL, -- Identifica em qual consulta o medicamento foi prescrito
    id_medicamento INT NOT NULL, -- Identifica qual medicamento faz parte da prescricao
    dosagem VARCHAR(100) NOT NULL, -- Informa a quantidade ou concentracao prescrita, sendo de preenchimento obrigatorio
    frequencia VARCHAR(100) NOT NULL, -- Informa a periodicidade de uso do medicamento, tambem obrigatoria

    CONSTRAINT pk_prescricao -- Define id_prescricao como chave primaria
        PRIMARY KEY (id_prescricao),

    CONSTRAINT fk_prescricao_consulta -- Relaciona a prescricao a consulta; ao excluir a consulta, suas prescricoes sao apagadas junto
        FOREIGN KEY (id_consulta)
        REFERENCES Consulta(id_consulta)
        ON DELETE CASCADE,

    CONSTRAINT fk_prescricao_medicamento -- Relaciona cada prescricao ao medicamento correspondente
        FOREIGN KEY (id_medicamento)
        REFERENCES Medicamento(id_medicamento)
);
