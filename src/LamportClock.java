/**
 * Implementação do algoritmo de relógio lógico de Lamport
 * 
 * Regras do algoritmo:
 * 1. Cada processo mantém um contador L(p), começando em 0
 * 2. Incrementa L(p) a cada evento interno
 * 3. Ao enviar uma mensagem, envia o valor de L(p)
 * 4. Ao receber, L(p) = max(L(recebido), L(p)) + 1
 */
public class LamportClock {
    private int logicalTime;
    private final int processId;
    
    public LamportClock(int processId) {
        this.processId = processId;
        this.logicalTime = 0;
    }
    
    /**
     * Incrementa o relógio lógico para um evento interno
     */
    public int tick() {
        logicalTime++;
        return logicalTime;
    }
    
    /**
     * Obtém o valor atual do relógio lógico
     */
    public int getTime() {
        return logicalTime;
    }
    
    /**
     * Processa o recebimento de uma mensagem
     * L(p) = max(L(recebido), L(p)) + 1
     */
    public int receive(int receivedTime) {
        logicalTime = Math.max(logicalTime, receivedTime) + 1;
        return logicalTime;
    }
    
    /**
     * Obtém o ID do processo
     */
    public int getProcessId() {
        return processId;
    }
    
    @Override
    public String toString() {
        return "Processo " + processId + " (L=" + logicalTime + ")";
    }
} 