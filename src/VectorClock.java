import java.util.Arrays;

/**
 * Implementação de relógios vetoriais
 * 
 * Cada processo tem um vetor V[i] de tamanho N (número de processos)
 * - Evento local: V[i][i]++
 * - Envio de mensagem: envia cópia de V[i]
 * - Recepção: V[i][j] = max(V[i][j], Vmsg[j]) para todo j, depois V[i][i]++
 */
public class VectorClock {
    private int[] vector;
    private final int processId;
    private final int numProcesses;
    
    public VectorClock(int processId, int numProcesses) {
        this.processId = processId;
        this.numProcesses = numProcesses;
        this.vector = new int[numProcesses];
        // Inicializar todos os elementos com 0
        Arrays.fill(vector, 0);
    }
    
    /**
     * Incrementa o relógio vetorial para um evento local
     * V[i][i]++
     */
    public int[] tick() {
        vector[processId]++;
        return getVector();
    }
    
    /**
     * Obtém uma cópia do vetor atual
     */
    public int[] getVector() {
        return Arrays.copyOf(vector, vector.length);
    }
    
    /**
     * Processa o recebimento de uma mensagem
     * V[i][j] = max(V[i][j], Vmsg[j]) para todo j, depois V[i][i]++
     */
    public int[] receive(int[] receivedVector) {
        // V[i][j] = max(V[i][j], Vmsg[j]) para todo j
        for (int i = 0; i < numProcesses; i++) {
            vector[i] = Math.max(vector[i], receivedVector[i]);
        }
        // V[i][i]++
        vector[processId]++;
        return getVector();
    }
    
    /**
     * Obtém o ID do processo
     */
    public int getProcessId() {
        return processId;
    }
    
    /**
     * Compara dois vetores para determinar a relação causal
     * Retorna: -1 se this < other, 0 se concorrentes, 1 se this > other
     */
    public static int compare(int[] vector1, int[] vector2) {
        boolean less = false;
        boolean greater = false;
        
        for (int i = 0; i < vector1.length; i++) {
            if (vector1[i] < vector2[i]) {
                less = true;
            } else if (vector1[i] > vector2[i]) {
                greater = true;
            }
        }
        
        if (less && !greater) {
            return -1; // vector1 < vector2
        } else if (greater && !less) {
            return 1;  // vector1 > vector2
        } else {
            return 0;  // concorrentes
        }
    }
    
    /**
     * Verifica se dois eventos são concorrentes
     */
    public static boolean areConcurrent(int[] vector1, int[] vector2) {
        return compare(vector1, vector2) == 0;
    }
    
    /**
     * Verifica se um evento aconteceu antes de outro
     */
    public static boolean happenedBefore(int[] vector1, int[] vector2) {
        return compare(vector1, vector2) == -1;
    }
    
    @Override
    public String toString() {
        return "Processo " + processId + " " + Arrays.toString(vector);
    }
} 