# PROJETO DE BLOCO - DOCUMENTO DO PROJETO

## Informações do Projeto

**Nome do Projeto:** Agenda para Clínicas de Estética

**Membros:**
- Matheus Zilli: [matheus.zilli@al.infnet.edu.br](mailto:matheus.zilli@al.infnet.edu.br)
- Paulo Eduardo Zamboni: [Paulo.zamboni@al.infnet.edu.br](mailto:Paulo.zamboni@al.infnet.edu.br)

**Link GitHub:** [https://github.com/matheuszilli/agenda](https://github.com/matheuszilli/agenda)

**Linguagem do Projeto:** JAVA

## Síntese do Sistema

O Sistema Agenda para Clínicas de Estética é uma aplicação desenvolvida em Java, com o objetivo de centralizar e automatizar o fluxo de atendimento aos clientes, sem perder o aspecto humanizado do serviço. Ele permite que os clientes realizem seus próprios agendamentos de forma autônoma, enquanto recepcionistas e profissionais têm acesso facilitado às informações de agenda.

A solução oferece um gerenciamento completo de agendamentos para clínicas e consultórios, controlando desde a estrutura organizacional (empresas e filiais) até os agendamentos individuais. Estão incluídas funcionalidades como: gestão de profissionais, pacientes, serviços, recursos físicos (salas e cadeiras), horários de funcionamento, disponibilidade, confirmações de agendamento, controle de comanda e caixa, registro de documentos e prontuários médicos. Dessa forma, garante-se uma operação eficiente, organizada e adaptada às necessidades do dia a dia da clínica.

### Principais características:

- Suporte a múltiplas empresas e filiais
- Controle de horários de funcionamento flexível
- Verificação de disponibilidade em tempo real
- Gestão de recursos físicos (salas/cadeiras)
- Controle de pagamentos e pré-pagamentos
- Prontuários médicos eletrônicos
- API REST para integração
- Controle de usuários e permissões

## Cenário a ser explorado

Nossa clínica de estética precisa de uma solução que facilite o dia a dia de agendamentos e atenda tanto aos clientes quantos aos profissionais que trabalham conosco. Atualmente, lidamos com duas formas principais de marcação: mensagens enviadas via Instagram e WhatsApp (que gostaríamos de integrar por meio da API da Meta ao nosso sistema) e também agendamentos feito via telefone (sistema URA).

Queremos que esse sistema ofereça aos nossos colaboradores uma forma interna de registrar e confirmar horários, assim como permita que os próprios clientes realizem o agendamento por conta própria. Cada tipo de usuário deve ter acesso às informações relevantes para sua função, porém pode ser customizada para algum usuário especifica alguma função a mais ou a menos, ficando a critério da clínica. - por exemplo, um recepcionista pode ver a agenda de todos os profissionais para encaixar clientes, enquanto o profissional só precisa visualizar seus próprios horários e o histórico dos clientes que vai atender

### A experiência ideal na clínica seria a seguinte:

1. **Contato inicial:** O cliente nos procura pelo WhatsApp, Instagram ou Telefones. Assim que ele demonstra interesse, podemos rapidamente verificar a disponibilidade de horários e fazer o agendamento

2. **Agendamento e Confirmação:** Queremos que, 48 horas antes do atendimento (*regra especifica para quando esse contato cair em fim de semana), o sistema envie ao cliente uma mensagem solicitando a confirmação do agendamento. Ao responder, positivamente, o agendamento fica oficialmente confirmado.

3. **Anamnese e contrato:** Antes de realizar o procedimentos, é importante sabermos se o cliente tem alguma condição ou restrição. Por isso precisamos que preencha uma ficha de anamnese que ficará anexada em seu cadastro. Além disso, o cliente assina um contrato com nossos termos e condições.

4. **Dia do atendimento:** No dia marcado, é feita a abertura de uma comanda para registrar os serviços que serão prestados e os valores envolvidos. Quando o cliente chega, a recepção faz uma especie de 'check-in', confirma se toda a documentação está preenchida pelo cliente, e notifica o profissional responsável de que o cliente está aguardando. Caso o cliente tenha agendamento com mais de um profissional, ambos os profissionais serão notificados, e cada um saberá com qual outro profissional a cliente esta passando/passará.

5. **Execução do serviço:** Queremos que o profissional consiga registrar, durante o procedimento informações relevantes e até mesmo fazer upload de fotos de antes e depois, que ficarão registradas no histórico do cliente para facilitar o acompanhamento posterior. Além disso o profissional poderá anotar informações como: produtos utilizados, pontos importantes do atendimento.

6. **Pagamento e fechamento de comanda:** Ao final do atendimento, o valor é acertado e registramos a forma de pagamento. Seria muito interessante integrar os meios de pagamento no formato TEF, porém precisamos também de uma forma de registrar manualmente para os casos POS, ou caso cliente pague em dinheiro ou outro método.

7. **Pós-Vendas:** Se o procedimento precisar de um retorno - por exemplo um retoque em uma micropgimentação - ja podemos deixar um retorno pré-agendado. Além disso gostaríamos de registrar a avaliação do cliente no histórico e ter opção de enviar um resumo do que foi feito, bem como reforçar orientações e cuidados pós-procedimento.

Em termos de serviço, nossa clínica hoje oferece bastante foco em micropigmentação (sobrancelhas, lábios, olhos, capilar, etc.) mas desejamos que o sistema seja flexível para cadastrar novos serviços conforme a clínica desejar. Também é fundamental ter a possibilidade de operar com várias unidades da clínica, e cada frente ter seu próprio cadastro para emissão de suas NF, além disso alguns profissionais podem trabalhar em mais de uma clínica, e nesses casos o sistema deverá sempre gerar um alerta quando houver agendamentos em horários conflitantes para o mesmo profissional em clínicas diferentes, e caso seja na mesma clínica não há problema, um profissional pode ter mais de um agendamento na mesma hora.

Além de tudo isso, nossa intenção é que cada cliente tenha uma ficha de cadastro completa, contendo suas informações para emissão de NF, dados de contatos e dados que serão utilizados para marketing. Sempre que um atendimento for finalizado, gostaríamos de gerar registros financeiros confiáveis (referente a fluxo de caixa, e de competência) ajudando a compor relatórios que mostrem o que foi agendado, faturado e, eventualmente se há pendências de pagamento para algum cliente. O sistema será integrado com algum emissor de nota fiscal, e se houver erro ou impossibilidade de emissão da nota de algum serviço, o sistema deverá sempre notificar o usuário do caixa responsável sobre o ocorrido (devemos evitar complicações com a receita federal).

Por fim, vale complementar que não há interesse da clínica no momento em gerenciar estoque de produtos, pois muitos dos profissionais utilizam produtos próprios fora do controle da clínica. O que realmente importa, no momento, é tornar nosso fluxo de agendamentos e atendimentos confiáveis e fáceis de consultar, além de termos um sistema que consiga conectar com outros sistemas utilizados pela clínica (ERP por exemplo).

## Status Atual de Implementação

### Funcionalidades Implementadas:

- Gestão completa de empresas e filiais com CNPJs independentes
- Sistema robusto de agendamentos com verificação de disponibilidade em tempo real
- Controle de conflitos automático entre clínicas para o mesmo profissional
- Cadastro flexível de serviços e configurações por profissional
- Gestão de salas/cadeiras com atribuições de profissionais
- Sistema de contas de clientes com controle de saldos e transações
- Ordens de serviço (comandas) para registrar serviços e valores
- Prontuários eletrônicos com suporte a anotações e anexos
- Controle de pagamentos com múltiplas formas
- Sistema de usuários com diferentes perfis de acesso
- Horários de funcionamento flexíveis e recorrentes

### Em Desenvolvimento/Planejamento:

- Integrações com WhatsApp/Instagram via API Meta
- Sistema URA para telefone
- Portal do cliente para autoagendamento
- Confirmações automáticas 48h antes
- Anamnese digital e contratos eletrônicos
- Sistema de check-in na recepção
- Integração TEF para pagamentos
- Emissão automática de notas fiscais
- Relatórios financeiros avançados
- Sistema de avaliações pós-atendimento

## Atores do Sistema

### Atores Humanos:

**Cliente:** Pessoa que busca os serviços da clínica, realiza agendamentos, consulta informações e fornece dados (anamnese, contrato, pagamento).

**Recepcionista:** Funcionário(a) da clínica responsável por gerenciar agendamentos, fazer check-in, gerenciar comandas, processar pagamentos e interagir com o cliente e profissionais.

**Profissional:** Especialista que realiza o procedimento nos clientes, consulta sua agenda, acessa histórico do cliente, registra info de atendimento, e controla o próprio estoque, registro de prontuários médicos

**Estabelecimento:** Usuário com visão gerencial sobre a operação de uma unidade (relatórios, configurações)

**Master (ADM):** Usuário com acesso total para configuração do sistema, gerenciamento de usuários, permissões e cadastros gerais.

### Atores Externos

**API Meta:** Sistema externo (WhatsApp, Instagram) que fornece solicitações de agendamento ou mensagens de clientes.

**Sistema URA:** Sistema de telefonia que interage com o cliente para agendamento/confirmações, trocando informações com o sistema de agendamento

**Sistema Emissor NF:** Sistema externo que recebe dados do sistema de agendamento para emissão de NFS-e

**Sistema de Pagamentos (TEF/POS):** Sistema externo que processa o registro de pagamento do cliente.

## Funcionalidades/Comportamentos Principais

1. **Gerenciamento de Agendamentos:** Agendamento online (Cliente), registro manual (Recepcionista), processamento de solicitações (URA/ API), envio de lembretes, registro de confirmações, alerta/impedimento de possíveis conflitos, cancelamento, edição e remarcação

2. **Gestão de Clientes:** Cadastro, gestão de Fichas de Anamnese, armazenamento de contratos, histórico de atendimentos, controle de contas e saldos

3. **Gestão do Atendimento:** Check-in, notificação da chegada do cliente ao profissional, gestão de comandas, gestão de dados para caixa, relatórios, integração com emissão de NF

4. **Gestão de Caixa:** Controle Financeiro, relatórios de fechamento, registros de pagamento, transações de contas de clientes, ajustes financeiros.

5. **Gestão de Cadastros Gerais:** Gerenciamento de serviços, unidades, profissionais, usuários do sistema e permissão de acesso

6. **Pós-Vendas:** Agendamento de retornos, registro de avaliação do cliente, envio de resumo de pós-vendas.

## Entidades e Propriedades (Substantivos)

### 1. Company
- Empresa principal com CNPJ
- Endereço completo
- Telefone de contato

### 2. Subsidiary
- Filiais da empresa
- CNPJ próprio para emissão de N.F.
- Horários de funcionamento
- Endereço específico

### 3. User (Usuários)
- Usuários do sistema com diferentes perfis
- Roles: ADMIN, PROFESISONAL, RECEPCIONIST, etc.
- Vinculados a empresa

### 4. Professionals
- Especialistas que realizam procedimento
- Configurações personalizadas de serviço
- Comissões e preços customizáveis
- Horários de atendimento

### 5. Customer (Cliente)
- Clientes da clínica
- Dados completos para N.F.
- Histórico de atendimento
- Conta financeira

### 6. Item (Serviço)
- Serviços ofertados pela clínica
- Preço padrão
- Duração em minutos
- Exigências de pré-pagamento

### 7. Appointment
- Data e horário
- Status (pendente, não confirmado, confirmado, em atendimento, concluído)
- Profissional e clientes vinculados
- Sala/ cadeira designido

### 8. ChairRoom (Sala / Cadeira)
- Recursos físicos da clínica
- Capacidade e localização
- Horários de disponibilidade
- Atribuição de profissionais

### 9. Service Order
- Registro dos serviços prestados
- Valores envolvidos
- Status da ordem
- Multiplos items

### 10. MedicalRecord (Prontuário)
- Registros médicos por atendimento
- Anotações do profissional
- Fotos antes/depois
- Status de finalização

### 11. ClientAccount (Conta do Cliente)
- Saldo livre e retido
- Histórico de transações
- Controle financeiro

### 12. ProfessionalServiceCfg (Configuração de Serviço)
- Preços customizados por profissional
- Comissões específicas
- Durações personalizadas

## Casos de Uso (Verbos)

1. Cadastrar um agendamento
2. Mudar o repasse por um período pré-definido
3. Cadastrar cliente
4. Cadastrar profissional
5. Cadastrar cadeira
6. Faturar pedido de venda
7. Cadastrar empresa
8. Cadastrar serviços
9. Visualizar a agenda
10. Cadastrar Estabelecimento
11. Receber pagamento
12. Registrar prontuário de atendimento
13. Indicar a disponibilidade de agenda
14. Iniciar um atendimento
15. Encerrar um atendimento
16. Definir a data limite para o retorno
17. Aumentar a data limite do retorno
18. Selecionar Cliente
19. Realizar check-in de cliente
20. Verificar conflitos de agendamento
21. Gerenciar horários de funcionamento
22. Controlar conta do cliente
23. Atribuir profissional a uma sala ou cadeira
24. Cancelar agendamento
25. Confirmar agendamento
26. Atualizar agendamento
27. Registrar operações financeiras

## Requisitos do Sistema

### O sistema deve permitir:

#### Gestão de Cadastro Básico
1. Ao Administrador cadastrar uma empresa
2. Ao Administrador cadastrar uma subsidiaria
3. Ao Administrador cadastrar uma sala ou cadeira
4. Ao Gerente cadastrar um profissional
5. Ao Comercial cadastrar um Serviço
6. À Recepcionista cadastrar um cliente
7. À Atendente cadastrar um agendamento

#### Gestão de Agendamentos
1. À Atendente cadastrar um agendamento
2. Ao profissional visualizar sua agenda
3. A Atendente visualizar agenda da subsidiaria
4. Ao Sistema verificar conflitos de agendamento automaticamente
5. Ao Sistema verificar disponibilidade em tempo real
6. A Atendente cancelar um agendamento
7. A Atendente atualizar um agendamento

#### Gestão de Atendimentos
1. À Recepcionista realizar check-in de atendimento
2. À Recepcionista iniciar um atendimento
3. Ao Profissional iniciar um atendimento
4. À Recepcionista finalizar um atendimento
5. Ao Profissional finalizar um atendimento
6. Ao Profissional realizar um registro no prontuário do atendimento
7. Ao Profissional configurar sua disponibilidade de agenda
8. Ao Sistema registrar informações no histórico da cliente

#### Gestão Financeira
1. À Recepcionista fechar uma ordem de serviço
2. Ao Sistema gerar uma N.F.
3. A Atendente receber um pagamento
4. Ao Sistema controlar a conta corrente do cliente
5. Ao Administrador ajustar a conta corrente do cliente
6. Ao Estabelecimento visualizar relatórios financeiros
7. Ao Sistema fechar o valor de repasse do profissional

#### Gestão de Regras
1. Ao Gerente mudar o repasse por um período pré-definido
2. Ao Sistema definir a data limite de retorno de um atendimento
3. Ao Profissional aumentar a data limite de um retorno
4. À Atendente solicitar um aumento de data limite de um retorno
5. Ao Estabelecimento gerenciar horários de funcionamento
6. Ao Estabelecimento atribuir um profissional a uma cadeira / sala
7. Ao Administrador gerenciar Usuários e Permissões.

## Descrições de Casos de Uso

### 1. Cadastrar Agendamento

**Atores:** Recepcionista, Atendente, Estabelecimento  
**Meta (GO):** Marcar um horário de um cliente vinculado a um profissional e serviço na agenda  
**Pré-condição:** Cliente, Profissional, Cadeira/Sala e Serviço cadastrados no sistema. Horário da sala disponível  
**Pós-condição:** Serviço registrado na agenda, em uma cadeira com o profissional em um horário, com status 'a confirmar agendamento'

#### Fluxo Principal:

1. Ator abre a página de agendamento
2. Ator filtra a agenda conforme necessidades do cliente
3. Sistema lista horários vagos
4. Ator seleciona o horário desejado
5. Sistema abre o formulário de agendamento, com as informações de horário selecionadas e informação do cliente
6. Ator clica em 'Agendar'
7. Sistema registra o agendamento no banco de dados
8. Sistema dispara um pop-up de confirmação
9. Retorna para a página de agendamento

#### Fluxo Alternativo: 
**8. Sistema dispara pop-up:**
- 8.1. 'Cliente {nome} agendado com sucesso!'
  - 8.1.1. Continua para o passo 9
- 8.2. 'Falha ao agendar cliente'
  - 8.2.1. Retorna ao passo 6

### 2. Iniciar um Atendimento

**Atores:** Profissional, Recepcionista  
**Meta (GO):** Iniciar o registro de informações de um atendimento agendado  
**Pré-condição:** Ter um agendamento cadastrado; Ter realizado check-in da cliente no estabelecimento  
**Pós-condição:** Atendimento iniciado e disponível para registro de informações

#### Fluxo Principal:

1. Com a cliente em sala, o profissional clica em 'Iniciar Atendimento'
2. Sistema direciona para página com histórico e observações de atendimentos anteriores do cliente
3. Sistema exibe formulário para registro de informações do atendimento
4. Profissional registra informações necessárias durante o atendimento
5. Sistema salva informações em tempo real

#### Fluxo Alternativo: 
**2. Sistema direciona para página:**
- 2.1. Se cliente possui histórico: exibe informações anteriores
- 2.2. Se cliente é novo: exibe formulário em branco

### 3. Cadastrar Cliente

**Atores:** Recepcionista, Atendente, Estabelecimento  
**Meta (GO):** Registrar dados do cliente no sistema  
**Pré-condição:** O ator estar registrado no sistema  
**Pós-condição:** Registro completo de dados de um cliente na base de clientes

#### Fluxo Principal:

1. Ator clica em 'Cadastrar Cliente' na barra de tarefas
2. Sistema direciona para página requisitando dados a serem preenchidos
3. Ator preenche todos os dados conforme informado pela cliente (Dados necessários: documento, endereço, e-mail)
4. Ator clica em 'Salvar' no final do formulário
5. Sistema dispara um pop-up
6. Retorna à página home com o cliente criado e selecionado

#### Fluxo Alternativo: 
**5. Sistema dispara pop-up:**
- 5.1. 'Cliente {nome} registrado com sucesso'
  - 5.1.1. Continua para o passo 6
- 5.2. 'Falha ao cadastrar o cliente'
  - 5.2.1. Retorna ao passo 3
- 5.3. 'Falha ao cadastrar pois campo {nomeCampo} inválido'
  - 5.3.1. Retorna ao passo 3

### 4. Cadastrar Profissional

**Atores:** Gerente  
**Meta (GO):** Registrar dados do profissional no sistema  
**Pré-condição:** O ator estar registrado no sistema  
**Pós-condição:** Registro completo de dados de um profissional na base de profissionais

#### Fluxo Principal:

1. Ator clica em 'Cadastrar Profissional' na barra de tarefas
2. Sistema direciona para página requisitando dados a serem preenchidos
3. Ator preenche todos os dados do Profissional (Dados necessários: Documento, endereço, e-mail)
4. Ator clica em 'Salvar' no final do formulário
5. Sistema dispara um pop-up
6. Retorna à página home

#### Fluxo Alternativo: 
**5. Sistema dispara pop-up:**
- 5.1. 'Profissional {nome} registrado com sucesso'
  - 5.1.1. Continua para o passo 6
- 5.2. 'Falha ao cadastrar o profissional'
  - 5.2.1. Retorna ao passo 3
- 5.3. 'Falha ao cadastrar pois campo {nomeCampo} inválido'
  - 5.3.1. Retorna ao passo 3

### 5. Finalizar Atendimento

**Atores:** Profissional, Recepcionista  
**Meta (GO):** Encerrar o atendimento e registrar informações finais  
**Pré-condição:** Atendimento iniciado  
**Pós-condição:** Atendimento finalizado com todas as informações registradas

#### Fluxo Principal:

1. Profissional clica em 'Finalizar Atendimento'
2. Sistema exibe resumo do atendimento realizado
3. Profissional confirma serviços realizados e valores
4. Sistema solicita registro de prontuário
5. Profissional registra informações do prontuário (opcional)
6. Sistema finaliza o atendimento
7. Sistema dispara pop-up de confirmação

#### Fluxo Alternativo: 
**5. Registro de prontuário:**
- 5.1. Profissional registra prontuário completo
  - 5.1.1. Continua para passo 6
- 5.2. Profissional pula registro de prontuário
  - 5.2.1. Continua para passo 6

### 6. Realizar Check-in

**Atores:** Recepcionista  
**Meta (GO):** Confirmar chegada do cliente e preparar para atendimento  
**Pré-condição:** Cliente possui agendamento confirmado  
**Pós-condição:** Cliente registrado como presente, profissional notificado

#### Fluxo Principal:

1. Recepcionista acessa lista de agendamentos do dia
2. Localiza o cliente na lista
3. Clica em 'Fazer Check-in'
4. Sistema registra horário de chegada
5. Sistema notifica profissional responsável
6. Status do agendamento muda para 'Cliente Presente'

#### Fluxo Alternativo: 
**2. Cliente não encontrado na lista:**
- 2.1. Recepcionista verifica agendamento em outras datas
- 2.2. Se não encontrado, oferece reagendamento
- 2.3. Retorna ao passo 1

## Classes do Sistema

### 1. Entidades Organizacionais

#### Company (Empresa)

**Responsabilidade:** Entidade jurídica principal que possui as clínicas  
**Justificativa:** Necessária para controle multi-empresa e emissão de NF  
**Atributos:**
- id: UUID
- name: String
- documentNumber: String (CNPJ)
- address: Address (embedded)
- phone: String
- isActive: Boolean

#### Subsidiary (Subsidiária)

**Responsabilidade:** Unidades físicas da empresa  
**Justificativa:** Cada unidade tem CNPJ próprio e funcionamento independente  
**Atributos:**
- id: UUID
- name: String
- documentNumber: String (CNPJ filial)
- address: Address (embedded)
- openTime: LocalTime
- closeTime: LocalTime
- company: Company

### 2. Entidades de Pessoas

#### User (Usuário) 
**Responsabilidade:** Usuários do sistema com diferentes perfis  
**Justificativa:** Controle de acesso e permissões  
**Atributos:**
- id: UUID
- username: String
- email: String
- role: UserRole (ADMIN, RECEPTIONIST, PROFESSIONAL, etc.)
- firstName: String
- lastName: String
- company: Company

#### Professional (Profissional) 
**Responsabilidade:** Especialistas que executam procedimentos  
**Justificativa:** Ator principal do processo de atendimento  
**Atributos:**
- id: UUID
- firstName: String
- lastName: String
- fullName: String (derivado)
- documentNumber: String
- email: String
- phone: String
- address: Address (embedded)
- subsidiary: Subsidiary
- user: User (opcional)

#### Customer (Cliente) 
**Responsabilidade:** Clientes que recebem os serviços  
**Justificativa:** Ator principal do processo de agendamento  
**Atributos:**
- id: UUID
- firstName: String
- lastName: String
- fullName: String (derivado)
- email: String
- phone: String
- documentNumber: String
- address: Address (embedded)
- dateOfBirth: LocalDate
- company: Company

### 3. Entidades de Negócio

#### Item (Serviço) 
**Responsabilidade:** Serviços oferecidos pela clínica  
**Justificativa:** Core business - micropigmentação e outros procedimentos  
**Atributos:**
- id: UUID
- name: String
- description: String
- price: BigDecimal
- durationMinutes: Integer
- requiresPrePayment: Boolean
- active: Boolean
- company: Company

#### Appointment (Agendamento) 
**Responsabilidade:** Agendamentos de serviços  
**Justificativa:** Processo central do sistema  
**Atributos:**
- id: UUID
- date: LocalDate
- startTime: LocalDateTime
- endTime: LocalDateTime
- status: AppointmentStatus
- notes: String
- professional: Professional
- customer: Customer
- chairRoom: ChairRoom
- item: Item
- subsidiary: Subsidiary

#### ChairRoom (Sala/Cadeira) 
**Responsabilidade:** Recursos físicos onde ocorrem atendimentos  
**Justificativa:** Controle de disponibilidade de recursos  
**Atributos:**
- id: UUID
- name: String
- description: String
- capacity: Integer
- roomNumber: String
- isAvailable: Boolean
- subsidiary: Subsidiary

### 4. Entidades de Controle Financeiro

#### ServiceOrder (Ordem de Serviço) 
**Responsabilidade:** Comanda do atendimento  
**Justificativa:** Controle financeiro e registro de serviços prestados  
**Atributos:**
- id: UUID
- totalPrice: BigDecimal
- discount: BigDecimal
- status: ServiceOrderStatus
- customer: Customer
- professional: Professional
- items: List<Item>

#### Payment (Pagamento)
**Responsabilidade:** Registros de pagamentos  
**Justificativa:** Controle financeiro obrigatório  
**Atributos:**
- id: UUID
- paymentMethod: PaymentMethod
- status: PaymentStatus
- amount: BigDecimal
- installments: Integer
- paymentDate: LocalDateTime
- serviceOrder: ServiceOrder

#### ClientAccount (Conta do Cliente) 
**Responsabilidade:** Controle de saldos e créditos  
**Justificativa:** Sistema de pré-pagamento implementado  
**Atributos:**
- id: UUID
- freeBalance: BigDecimal
- heldBalance: BigDecimal
- totalBalance: BigDecimal
- customer: Customer

### 5. Entidades de Registro Médico

#### MedicalRecord (Prontuário) 
**Responsabilidade:** Registros médicos dos atendimentos  
**Justificativa:** Requisito legal e técnico  
**Atributos:**
- id: UUID
- description: String
- photosBefore: List<String>
- photosAfter: List<String>
- finalized: Boolean
- appointment: Appointment
- customer: Customer
- createdBy: User

### 6. Entidades de Configuração 

#### ProfessionalServiceCfg (Configuração de Serviço) 
**Responsabilidade:** Preços e comissões customizadas por profissional  
**Justificativa:** Flexibilidade de precificação  
**Atributos:**
- id: UUID
- customPrice: BigDecimal
- customDurationMinutes: Integer
- commissionPct: BigDecimal
- commissionFixed: BigDecimal
- professional: Professional
- service: Item

#### SubsidiaryScheduleEntry (Horário da Subsidiária) 
**Responsabilidade:** Horários de funcionamento das unidades  
**Justificativa:** Controle de disponibilidade organizacional  
**Atributos:**
- id: UUID
- date: LocalDate
- openTime: LocalTime
- closeTime: LocalTime
- closed: Boolean
- subsidiary: Subsidiary

#### ProfessionalScheduleEntry (Horário do Profissional) 
**Responsabilidade:** Horários de trabalho dos profissionais  
**Justificativa:** Controle de disponibilidade individual  
**Atributos:**
- id: UUID
- date: LocalDate
- startTime: LocalTime
- endTime: LocalTime
- professional: Professional

### 7. Classes de Valor (Value Objects)

#### Address (Endereço) 
**Responsabilidade:** Representar endereços (Embedded Object)  
**Justificativa:** Reutilização e consistência  
**Atributos:**
- street: String
- number: String
- complement: String
- city: String
- state: String
- zipCode: String

#### AccountTransaction (Transação Financeira) 
**Responsabilidade:** Movimentações financeiras  
**Justificativa:** Auditoria e controle  
**Atributos:**
- id: UUID
- transactionType: AccountTransactionType
- amount: BigDecimal
- description: String
- clientAccount: ClientAccount

## Dicionário de Dados - Sistema de Agendamento Clínica

### DEFINIÇÕES DE CLASSES

#### Entidades Organizacionais

**Company:** Uma entidade jurídica que possui uma ou mais **subsidiárias** onde são prestados serviços de estética. Cada **empresa** possui CNPJ próprio e é responsável pelos aspectos legais e fiscais do negócio. Por exemplo, "Clínica X Ltda." seria uma **empresa** que pode ter filiais em diferentes bairros, cidades, e estados. Nosso sistema ainda não está sendo parametrizado para outros países.

**Subsidiary:** Uma unidade física de atendimento pertencente a uma **empresa**, com CNPJ próprio para emissão de notas fiscais. Cada **subsidiária** opera com horários de funcionamento específicos e possui seus próprios recursos físicos (**salas/cadeiras**) e **profissionais**. Por exemplo, "Clínica X - Unidade Shopping" seria uma **subsidiária** da empresa principal.

#### Entidades de Pessoas

**User:** Uma pessoa autorizada a utilizar o sistema, com perfil específico de acesso e permissões. Um **usuário** pode assumir diferentes papéis (administrador, recepcionista, profissional) e está sempre vinculado a uma **empresa**. Por exemplo, "Maria Silva" pode ser um **usuário** com perfil de recepcionista.

**Professional:** Um especialista qualificado que executa procedimentos estéticos nos **clientes**. Cada **profissional** está vinculado a uma **subsidiária**, possui horários de trabalho específicos e pode ter configurações personalizadas de preços e comissões para diferentes **serviços**. Por exemplo, "Dr. João Santos, micropigmentador" seria um **profissional**.

**Customer:** Uma pessoa que busca e recebe serviços estéticos da clínica. Cada **cliente** possui dados completos para emissão de nota fiscal, histórico de **agendamentos** e pode manter uma **conta corrente** para pagamentos antecipados. Por exemplo, "Ana Costa" seria uma **cliente** que faz procedimentos de micropigmentação.

#### Entidades de Negócio

**Item:** Um serviço específico oferecido pela clínica, com preço, duração e características próprias. Um **serviço** pode exigir pré-pagamento e estar disponível em múltiplas **subsidiárias**. Por exemplo, "Micropigmentação de Sobrancelhas" seria um **serviço** com duração de 120 minutos e preço de R$ 350,00.

**Appointment:** O agendamento de um **cliente** para receber um **serviço** específico, executado por um **profissional** em uma **sala/cadeira** durante um período determinado. Cada **agendamento** possui status (pendente, confirmado, em atendimento, concluído) e pode gerar uma **ordem de serviço**. Por exemplo, "Ana Costa agendada para micropigmentação com Dr. João em 15/12/2024 às 14h00".

**ChairRoom:** Um recurso físico onde são realizados os atendimentos, localizado em uma **subsidiária** específica. Cada **sala/cadeira** possui capacidade, disponibilidade e pode ser atribuída a **profissionais** específicos em determinados horários. Por exemplo, "Sala 1 - Micropigmentação" seria uma **sala/cadeira** com capacidade para 1 cliente.

#### Entidades de Controle Financeiro

**ServiceOrder:** Uma comanda que registra todos os **serviços** prestados a um **cliente** em um atendimento específico, incluindo valores, descontos e **profissional** responsável. A **ordem de serviço** serve como base para cobrança e emissão de nota fiscal. Por exemplo, uma comanda contendo "Micropigmentação de Sobrancelhas + Retoque" no valor total de R$ 400,00.

**Payment:** O registro de pagamento de uma **ordem de serviço**, incluindo forma de pagamento, parcelas e status. Um **pagamento** pode ser em dinheiro, cartão, PIX ou outras modalidades. Por exemplo, "Pagamento de R$ 400,00 em cartão de crédito, 2x sem juros".

**ClientAccount:** Uma conta financeira mantida para cada **cliente**, controlando saldo livre, saldo retido e histórico de transações. A **conta do cliente** permite pré-pagamentos e controle de créditos. Por exemplo, uma conta com saldo livre de R$ 200,00 para futuros atendimentos.

#### Entidades de Registro Médico

**MedicalRecord:** Um prontuário eletrônico vinculado a um **agendamento** específico, contendo observações do **profissional**, fotos antes/depois e informações relevantes sobre o procedimento realizado. O **prontuário** é fundamental para acompanhamento e pode ser exigido legalmente. Por exemplo, um prontuário com fotos do antes/depois de uma micropigmentação e anotações sobre a técnica utilizada.

#### Entidades de Configuração

**ProfessionalServiceCfg:** Configurações específicas de um **profissional** para um **serviço**, incluindo preços customizados, durações diferentes e percentuais de comissão. Permite flexibilidade na precificação por especialista. Por exemplo, "Dr. João cobra R$ 380,00 por micropigmentação (diferente do preço padrão) e recebe 60% de comissão".

**SubsidiaryScheduleEntry:** O horário de funcionamento de uma **subsidiária** em uma data específica, incluindo horário de abertura, fechamento e possíveis exceções (fechado, horário especial). Por exemplo, "Subsidiária Shopping aberta das 9h às 18h em 15/12/2024".

**ProfessionalScheduleEntry:** O horário de trabalho de um **profissional** em uma data específica, definindo quando está disponível para atendimentos. Por exemplo, "Dr. João disponível das 14h às 20h em 15/12/2024".

### DEFINIÇÕES DE RELACIONAMENTOS

**Possui** (uma **Empresa** possui **Subsidiárias**): Uma empresa pode operar através de múltiplas unidades físicas, cada uma com CNPJ próprio para questões fiscais, mas todas sob a mesma gestão principal.

**Emprega** (uma **Subsidiária** emprega **Profissionais**): Cada unidade possui sua equipe de especialistas, que podem trabalhar exclusivamente nesta unidade ou em múltiplas unidades da mesma empresa.

**Atende** (um **Profissional** atende **Clientes**): O relacionamento principal do negócio, onde especialistas prestam serviços aos clientes através de **agendamentos**.

**Agenda** (um **Cliente** agenda **Serviços**): O processo pelo qual clientes solicitam atendimentos, resultando em **agendamentos** confirmados.

**Utiliza** (um **Agendamento** utiliza **Sala/Cadeira**): Cada atendimento ocorre em um recurso físico específico, que deve estar disponível no horário solicitado.

**Gera** (um **Agendamento** gera **Ordem de Serviço**): Quando um atendimento é iniciado, cria-se uma comanda financeira para controle dos serviços prestados.

**Recebe** (uma **Ordem de Serviço** recebe **Pagamento**): O fechamento financeiro do atendimento através do registro do pagamento.

**Mantém** (um **Cliente** mantém **Conta Corrente**): Sistema de créditos que permite pré-pagamentos e controle financeiro avançado.

**Registra** (um **Agendamento** registra **Prontuário**): Documentação médica obrigatória dos procedimentos realizados.

**Configura** (um **Profissional** configura **Serviços**): Personalização de preços e condições específicas por especialista.

### DEFINIÇÕES DE ATRIBUTOS

#### Atributos de Identificação

**Company.documentNumber:** O CNPJ da empresa no formato XX.XXX.XXX/XXXX-XX, usado para identificação fiscal e emissão de notas fiscais.

**Subsidiary.documentNumber:** O CNPJ específico da filial, geralmente derivado do CNPJ da empresa principal, mas com numeração sequencial diferente.

**Professional.documentNumber:** CPF do profissional no formato XXX.XXX.XXX-XX, usado para controle fiscal de comissões e repasses.

**Customer.documentNumber:** CPF do cliente no formato XXX.XXX.XXX-XX, obrigatório para emissão de nota fiscal.

#### Atributos Temporais

**Appointment.startTime:** Data e hora de início do agendamento no formato LocalDateTime (ex: 2024-12-15T14:00:00).

**Appointment.endTime:** Data e hora de término previsto do agendamento, calculado automaticamente com base na duração do **serviço**.

**SubsidiaryScheduleEntry.openTime:** Horário de abertura da subsidiária no formato LocalTime (ex: 08:00).

**SubsidiaryScheduleEntry.closeTime:** Horário de fechamento da subsidiária no formato LocalTime (ex: 18:00).

#### Atributos Financeiros

**Item.price:** Preço padrão do serviço em BigDecimal para precisão monetária (ex: 350.00 para R$ 350,00).

**Item.requiresPrePayment:** Indicador booleano se o serviço exige pagamento antecipado para confirmação do agendamento.

**ProfessionalServiceCfg.customPrice:** Preço personalizado que o profissional cobra por um serviço específico, podendo diferir do preço padrão.

**ProfessionalServiceCfg.commissionPct:** Percentual de comissão que o profissional recebe sobre o valor do serviço (ex: 60.00 para 60%).

**ClientAccount.freeBalance:** Saldo disponível na conta do cliente para futuros atendimentos.

**ClientAccount.heldBalance:** Saldo retido temporariamente para garantia de agendamentos confirmados.

#### Atributos de Status

**Appointment.status:** Status atual do agendamento (PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED).

**Payment.status:** Status do pagamento (PENDING, COMPLETED, FAILED, REFUNDED).

**MedicalRecord.finalized:** Indicador booleano se o prontuário foi finalizado pelo profissional.

#### Atributos Operacionais

**Item.durationMinutes:** Duração estimada do serviço em minutos (ex: 120 para 2 horas).

**ChairRoom.capacity:** Capacidade máxima da sala em número de pessoas (geralmente 1 para salas de micropigmentação).

**ChairRoom.isAvailable:** Indicador booleano se a sala está disponível para uso ou em manutenção.

**Subsidiary.openTime / closeTime:** Horários padrão de funcionamento da subsidiária, usado quando não há agendamento específico de data.

## Mapeamento de Atores para Casos de Uso

| # | Caso de Uso | Cliente | Recepcionista | Profissional | Gerente | Master | Sistemas Externos |
|---|-------------|---------|---------------|--------------|---------|--------|-------------------|
| 1 | Realizar agendamento online | P | - | - | - | - | - |
| 2 | Registrar agendamento manual (recepcionista) | - | P | - | - | - | - |
| 3 | Enviar lembrete de confirmação | C | - | - | - | - | (API da Meta) |
| 4 | Registrar confirmação de agendamento | P | S | - | - | - | (API da Meta) |
| 5 | Cadastrar ficha de anamnese | P | - | - | - | - | - |
| 6 | Consultar ficha de anamnese | - | S | P | S | - | - |
| 7 | Registrar contrato assinado | P | - | - | - | - | - |
| 8 | Consultar contrato assinado | - | S | S | S | - | - |
| 9 | Realizar check-in da cliente | - | P | - | - | - | - |
| 10 | Registrar informações de atendimento | - | S | P | - | - | - |
| 11 | Registrar produtos utilizados no atendimento | - | - | P | - | - | - |
| 12 | Registrar histórico do atendimento | - | S / C | P / C | C | - | - |
| 13 | Fechar comanda e registrar pagamento | C | P | - | S | - | TEF (S) |
| 14 | Validar dados de emissão de NF | - | S | - | P | - | - |
| 15 | Emitir NF | - | S | - | S | - | Emissor NF (P) |
| 16 | Alertar sobre conflito de agenda | - | - | S | S | - | - |
| 17 | Gerenciar cadastro de cliente | - | P | - | S | - | - |
| 18 | Gerenciar cadastro de serviço | - | - | - | S | P | - |
| 19 | Gerenciar cadastro de unidade | - | - | - | S | P | - |
| 20 | Gerenciar cadastro de profissional | - | - | - | S | P | - |
| 21 | Gerenciar usuários e permissões | - | - | - | - | P | - |
| 22 | Gerar relatórios financeiros | - | - | - | P | - | - |
| 23 | Agendar retorno | S | P | - | - | - | - |
| 24 | Registrar avaliações pós-atendimento | P | - | - | - | - | - |
| 25 | Consultar agenda | S | P | P | S | - | - |
| 26 | Cancelar agendamento | S | P | - | - | - | - |
| 27 | Remarcar agendamento | S | P | - | - | - | - |
| 28 | Gerenciar disponibilidade do profissional | - | - | P | S | - | - |
| 29 | Consultar histórico do cliente | - | S | P | S | - | - |
| 30 | Enviar resumo pós-atendimento | C | - | P | - | - | (API ou e-mail) |
| 31 | Gerenciar estoque de produtos | - | - | P | S | - | - |

**Legenda:**
- **P** = Ator Principal (executa a ação)
- **S** = Ator Secundário (pode executar ou visualizar)
- **C** = Ator que Consulta (apenas visualiza)
- **-** = Não tem acesso/participação