# Guia de Implementação do Sistema de Agendamento

## Visão Geral

O sistema proposto possui a seguinte hierarquia:

1. **Company** (Empresa) - Entidade principal centralizadora
2. **Subsidiary** (Subsidiária) - Filiais da empresa
3. **Professional** (Profissional) - Pessoas que atendem clientes
4. **ChairRoom** (Cadeira/Sala) - Recursos físicos que podem ser utilizados
5. **Service** (Serviço) - Serviços oferecidos
6. **Appointment** (Agendamento) - Marcação de horário para atendimento

## Regras de Negócio Principais

1. Subsidiárias definem seus horários de funcionamento
2. Cadeiras/salas respeitam os horários das subsidiárias, porém não é obrigatório essa implementação, se pode ser implementado parcialmente
3. Profissionais respeitam:
   - Se vinculados a uma cadeira/sala: o horário da cadeira
   - Se não vinculados: o horário da subsidiária
4. Agendamentos podem ocorrer em qualquer dia do ano
5. Horários podem ser configurados de forma recorrente (por dia da semana)
6. Horários específicos podem ser customizados ou bloqueados

## Estado Atual da Implementação

Atualmente, o sistema já possui:

- Estrutura básica de Company, Subsidiary, Professional e ChairRoom
- Entidades para controle de agendas (ScheduleEntry para Subsidiary, Professional e ChairRoom)
- Serviço para criação de horários recorrentes (RecurringScheduleService)
- Interface frontend básica para cadastro de Company e Subsidiary

## Próximos Passos para Implementação Completa
### 4. Implementar Frontend

#### 4.1 Componentes para Gestão de Serviços

- ServiceForm
- ServiceList
- ServiceManagement

#### 4.2 Componentes para Agendamento

- Calendar (visualização de calendário)
- AppointmentForm
- AppointmentList
- AppointmentManagement

#### 4.3 Visualizações de Disponibilidade

- AvailabilityCalendar (visão de calendário com slots disponíveis)
- WeekView (visão semanal de horários)
- DayView (visão detalhada do dia)

### 5. Fluxos de Agendamento

#### 5.1 Criar Horários de Funcionamento Recorrentes

1. Definir horários de funcionamento padrão para subsidiárias (por dia da semana)
2. Definir horários de funcionamento padrão para cadeiras/salas (por dia da semana)
3. Definir horários de trabalho padrão para profissionais (por dia da semana)

#### 5.2 Customizar Horários Específicos

1. Permitir customizar horários para dias específicos
2. Permitir bloquear dias específicos (feriados, férias, etc.)

#### 5.3 Criar Agendamento

1. Selecionar subsidiária
2. Selecionar serviço
3. Selecionar profissional
4. Selecionar cadeira/sala (opcional, dependendo da configuração)
5. Ver slots disponíveis
6. Selecionar data e horário
7. Confirmar agendamento

## Regras de Validação Importantes

1. Um agendamento só pode ser criado dentro do horário de funcionamento da subsidiária
2. Um agendamento com cadeira/sala só pode ser criado dentro do horário de disponibilidade da cadeira/sala
3. Um agendamento só pode ser criado dentro do horário de trabalho do profissional
4. Um profissional não pode ter dois agendamentos no mesmo horário
5. Uma cadeira/sala não pode ter dois agendamentos no mesmo horário
6. Se o profissional estiver vinculado a uma cadeira/sala, o agendamento deve usar essa cadeira/sala

## Próximos Passos

1. Implementar as entidades faltantes
2. Desenvolver os serviços de negócio
3. Implementar os endpoints da API
4. Desenvolver os componentes de frontend
5. Implementar testes automatizados
6. Documentar API e regras de negócio

Este plano de implementação reflete a hierarquia e regras de negócio descritas, permitindo a criação de um sistema completo de agendamento com suporte a empresas, subsidiárias, profissionais, cadeiras/salas e agendamentos.