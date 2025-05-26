# Implementações e Refatorações Recomendadas: Lógica de Negócio do Frontend para Backend

## Contexto
Este documento identifica lógicas de negócio que atualmente estão implementadas no frontend mas deveriam estar no backend, de acordo com as melhores práticas de arquitetura de software.

## ChairRoomForm.tsx: Lógica que deve ser movida para o backend

### 1. Validação de Regras de Negócio
- **Situação atual**: A validação do formulário de salas/cadeiras é feita no frontend (linhas 121-132)
- **Problema**: Validações de negócio essenciais como capacidade mínima, campos obrigatórios estão apenas no frontend
- **Recomendação**: Mover para validações no backend usando Jakarta Validation ou validadores personalizados

### 2. Verificação de Conflitos de Horários
- **Situação atual**: A lógica de detecção de conflitos (linhas 271-291) está no frontend
- **Problema**: Verificação de conflitos é lógica de negócio crítica que deve ser verificada no backend
- **Recomendação**: Implementar endpoint no backend para verificar e resolver conflitos de horários

### 3. Gestão de Exceções de Agendamento
- **Situação atual**: A lógica de exceções de agendamento (linhas 330-364) está no frontend
- **Problema**: Regras de exceção são críticas para o negócio e devem ser validadas pelo backend
- **Recomendação**: Criar endpoint específico no backend para gerenciar exceções de agendamento

### 4. Aplicação de Horários Recorrentes
- **Situação atual**: A lógica de aplicação de horários recorrentes (linhas 244-292) é parcialmente tratada no frontend
- **Problema**: O frontend precisa construir manualmente o padrão de recorrência e verificar conflitos
- **Recomendação**: Ampliar funcionalidade do método `createRecurring` no backend para aceitar todos os parâmetros necessários

### 5. Tratamento Incompleto no Backend
- **Situação atual**: O método `createRecurring` (linha 291) no `ChairRoomScheduleService.java` está implementado apenas parcialmente
- **Problema**: Lança exceção de método não implementado, forçando o frontend a usar soluções alternativas
- **Recomendação**: Implementar completamente este método conforme o padrão de outros serviços recorrentes

## Inconsistências entre Frontend e Backend

### 1. Formato de Dias da Semana
- **Situação atual**: O frontend usa formato de 0-6 para dias da semana (Domingo=0), enquanto o backend usa 1-7 (Segunda=1)
- **Problema**: Necessidade de conversão manual nos dias da semana (linhas 264-268)
- **Recomendação**: Padronizar o formato ou implementar conversão automática no backend

### 2. API para Horários Recorrentes
- **Situação atual**: Frontend chama endpoint `/api/chair-room-schedules/recurring` (linha 152) que está parcialmente implementado no backend
- **Problema**: O endpoint `/recurring-advanced` está sendo usado como alternativa pelo frontend
- **Recomendação**: Consolidar em um único endpoint bem implementado que aceite todos os formatos necessários

### 3. Tratamento de Exceções
- **Situação atual**: Erros de validação são capturados no frontend e exibidos ao usuário
- **Problema**: Falta consistência no tratamento de erros entre frontend e backend
- **Recomendação**: Padronizar respostas de erro no backend com códigos HTTP apropriados e mensagens informativas

## Melhorias Gerais Recomendadas

### 1. Criação de DTOs Específicos
- Criar DTOs específicos para operações de agendamento recorrente, separando claramente a estrutura de dados entre:
  - Agendamento único
  - Agendamento recorrente
  - Exceções de agendamento

### 2. Serviço Unificado de Gestão de Conflitos
- Implementar um serviço no backend específico para detecção e resolução de conflitos de agendamento
- Centralizar a lógica de verificação de disponibilidade e conflitos

### 3. Validação Robusta no Backend
- Implementar validação completa no backend com mensagens de erro claras
- Utilizar anotações de validação nos DTOs e validação personalizada nos serviços

### 4. Endpoints Simplificados
- Redesenhar a API para oferecer endpoints mais intuitivos:
  - POST `/api/chair-room-schedules/recurring` para horários recorrentes
  - POST `/api/chair-room-schedules/exceptions` para exceções
  - GET `/api/chair-room-schedules/conflicts` para verificar conflitos potenciais

### 5. Documentação Swagger/OpenAPI
- Documentar completamente a API para facilitar o entendimento e uso correto pelos clientes frontend

## Priorização das Refatorações

1. **Alta Prioridade**: Implementar completamente o método `createRecurring` no backend
2. **Alta Prioridade**: Mover lógica de validação de conflitos para o backend
3. **Média Prioridade**: Criar endpoints específicos para exceções
4. **Média Prioridade**: Padronizar formato de dias da semana
5. **Baixa Prioridade**: Refatorar para DTOs mais específicos