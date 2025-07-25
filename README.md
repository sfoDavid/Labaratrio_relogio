# Laboratório de Relógios Físicos e Lógicos

Este laboratório demonstra a implementação e observação da diferença entre relógios físicos e lógicos em sistemas distribuídos.

## Objetivos
- Implementar e observar a diferença entre relógios físicos e lógicos
- Simular o algoritmo de relógio lógico de Lamport
- Implementar relógios vetoriais

## Estrutura do Projeto

```
Laboratorio_Tempo/
├── src/                           # Código fonte Java
│   ├── PhysicalClockSimulation.java  # Parte 1 - Relógios Físicos
│   ├── LamportClock.java          # Implementação do relógio de Lamport
│   ├── LamportSimulation.java     # Parte 2 - Simulação de Lamport
│   ├── VectorClock.java           # Implementação de relógios vetoriais
│   └── VectorClockSimulation.java # Parte 3 - Simulação de vetores
├── target/                        # Arquivos compilados (.class)
├── README.md                      # Este arquivo
```

### Parte 1 - Simulando Eventos Distribuídos
- `PhysicalClockSimulation.java`: Simula 3 processos que trocam mensagens usando relógios físicos
- Demonstra como a ordem dos eventos pode parecer incorreta sem controle lógico

### Parte 2 - Relógio Lógico de Lamport
- `LamportClock.java`: Implementação do algoritmo de relógio lógico de Lamport
- `LamportSimulation.java`: Simulação usando relógios lógicos de Lamport

### Parte 3 - Relógios Vetoriais
- `VectorClock.java`: Implementação de relógios vetoriais
- `VectorClockSimulation.java`: Simulação usando relógios vetoriais

## Como Executar


```bash
# Compilar
javac -d target src/*.java

# Executar programa principal
java -cp target Main

# Executar partes individuais
java -cp target PhysicalClockSimulation
java -cp target LamportSimulation
java -cp target VectorClockSimulation

## Conceitos Demonstrados

### Relógios Físicos
- Usam `System.currentTimeMillis()` para timestamps
- Podem mostrar inconsistências na ordem dos eventos
- Não garantem causalidade

### Relógio Lógico de Lamport
- Cada processo mantém um contador L(p)
- Garante que se evento A → evento B, então L(A) < L(B)
- Não garante que se L(A) < L(B), então A → B

### Relógios Vetoriais
- Cada processo mantém um vetor V[i] de tamanho N
- Permitem determinar se eventos são concorrentes
- Garantem tanto causalidade quanto concorrência

## Limpeza

Para limpar os arquivos compilados:
rm -rf target/*
```
