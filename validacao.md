# Plano de Implementação: Migração de Lógica de Negócio do Frontend para Backend

## 1. Estrutura do Projeto e Padrões a Implementar

### Padrões de Design
- **Repository Pattern**: Já implementado, manter estrutura atual
- **Service Layer Pattern**: Fortalecer com validações e regras de negócio
- **DTO Pattern**: Expandir com DTOs específicos para operações complexas
- **Factory Method**: Para criação de objetos complexos como agendamentos recorrentes
- **Strategy Pattern**: Para diferentes estratégias de validação de conflitos

### Arquitetura Limpa
- Manter separação clara entre:
  - Controllers (API endpoints)
  - Services (regras de negócio)
  - Repositories (acesso a dados)
  - DTOs (transferência de dados)
  - Mappers (conversão entre entidades e DTOs)

## 2. Mudanças Específicas

### 2.1 Novos DTOs
| Novo DTO | Propósito | Campos |
|----------|-----------|--------|
| `RecurringScheduleDTO` | Representar configuração de horários recorrentes | `chairRoomId`, `weekSchedule` (Map<Integer, DayScheduleConfig>), `startDate`, `endDate`, `replaceExisting` |
| `DayScheduleConfigDTO` | Configuração para um dia da semana | `open` (boolean), `openTime`, `closeTime` |
| `ExceptionScheduleDTO` | Representar exceções de horário | `chairRoomId`, `date`, `openTime`, `closeTime`, `closed` |
| `ConflictCheckRequestDTO` | Verificar conflitos potenciais | `chairRoomId`, `dates` (List<LocalDate>), `daysOfWeek` (List<Integer>) |
| `ConflictCheckResponseDTO` | Resultado da verificação de conflitos | `hasConflicts`, `conflictingDates` (List<LocalDate>) |

### 2.2 Novos Endpoints

#### ChairRoomScheduleController
```java
// Verificar conflitos antes de aplicar horários recorrentes
@PostMapping("/conflicts-check")
public ResponseEntity<ConflictCheckResponseDTO> checkConflicts(@Valid @RequestBody ConflictCheckRequestDTO request)

// Criar agendamentos recorrentes com configuração complexa
@PostMapping("/recurring")
public ResponseEntity<List<ChairRoomScheduleEntryResponse>> createRecurring(@Valid @RequestBody RecurringScheduleDTO request)

// Criar exceção para um dia específico
@PostMapping("/exception")
public ResponseEntity<ChairRoomScheduleEntryResponse> createException(@Valid @RequestBody ExceptionScheduleDTO request)
```

### 2.3 Implementação de Serviços

#### RecurringScheduleService (Ampliar)
- Implementar método `createRecurringChairRoomScheduleAdvanced` que aceite configurações complexas
- Adicionar validação de conflitos como parte do fluxo
- Implementar opções para resolução de conflitos (substituir ou manter exceções)

#### Nova Classe: ScheduleConflictService
```java
@Service
public class ScheduleConflictService {
    // Verificar conflitos com horários existentes
    public ConflictCheckResponseDTO checkConflicts(UUID chairRoomId, List<LocalDate> dates, boolean includeCustomized)
    
    // Verificar conflitos para padrão recorrente
    public ConflictCheckResponseDTO checkRecurringConflicts(UUID chairRoomId, List<Integer> daysOfWeek, 
                                                       LocalDate startDate, LocalDate endDate)
}
```

### 2.4 Validações no Backend
- Implementar validações usando anotações do Jakarta Validation nos DTOs
- Criar validadores personalizados para regras complexas:
  - `@ValidTimeRange` - Garantir que horário final > horário inicial
  - `@ValidSubsidiaryHours` - Verificar se horário está dentro do horário da subsidiária
  - `@ValidRecurringSchedule` - Validar consistência de configuração recorrente

## 3. Mudanças no Frontend

### 3.1 ChairRoomForm.tsx
- Remover lógica de validação duplicada
- Utilizar novos endpoints para verificação de conflitos
- Simplificar código de agendamento recorrente para utilizar o novo endpoint unificado

### 3.2 chairRoomService.ts
- Adicionar novos métodos correspondentes aos endpoints criados
- Remover lógica de negócio e tratamentos especiais
- Focar apenas na comunicação com a API

## 4. Plano de Implementação

### Fase 1: Backend
1. Criar novos DTOs para representar operações complexas
2. Implementar validadores e anotações de validação
3. Criar/expandir serviços para tratar lógica de negócio
4. Implementar endpoints na Controller para expor funcionalidades
5. Adicionar testes unitários para validar regras de negócio

### Fase 2: Frontend
1. Atualizar serviço de API para utilizar novos endpoints
2. Remover lógica de negócio redundante
3. Simplificar componente ChairRoomForm.tsx
4. Manter tratamento de erros e exibição ao usuário

## 5. Benefícios Esperados
- **Segurança**: Regras de negócio validadas no servidor
- **Consistência**: Lógica centralizada, evitando duplicação
- **Manutenibilidade**: Separação clara de responsabilidades
- **Performance**: Redução de processamento no cliente
- **Experiência de usuário**: Respostas mais precisas sobre conflitos

## 6. Pontos de Atenção
- Manter compatibilidade com código existente durante a transição
- Assegurar que validações no frontend continuem fornecendo feedback rápido ao usuário
- Considerar tratamento de erros e mensagens claras de falha
- Documentar novos endpoints e DTOs