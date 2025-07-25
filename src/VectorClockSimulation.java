import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;

/**
 * Parte 3: Simulação usando relógios vetoriais
 * Demonstra como determinar a concorrência entre eventos usando vetores
 */
public class VectorClockSimulation {
    private static final int NUM_PROCESSES = 3;
    private static final int NUM_MESSAGES = 5;
    private static final AtomicInteger messageCounter = new AtomicInteger(0);
    
    public static void main(String[] args) {
        System.out.println("=== PARTE 3: SIMULAÇÃO DE RELÓGIOS VETORIAIS ===\n");
        System.out.println("Demonstrando como determinar concorrência entre eventos...\n");
        
        // Criar executor para gerenciar as threads
        ExecutorService executor = Executors.newFixedThreadPool(NUM_PROCESSES);
        
        // Criar e iniciar os processos
        for (int i = 0; i < NUM_PROCESSES; i++) {
            executor.submit(new VectorClockProcess(i));
        }
        
        // Aguardar um tempo para as simulações terminarem
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        
        System.out.println("\n=== FIM DA SIMULAÇÃO DE RELÓGIOS VETORIAIS ===");
        System.out.println("Observe como os relógios vetoriais permitem");
        System.out.println("determinar se eventos são concorrentes!\n");
    }
    
    /**
     * Processo que simula um nó distribuído usando relógio vetorial
     */
    static class VectorClockProcess implements Runnable {
        private final VectorClock clock;
        private final BlockingQueue<VectorMessage> messageQueue;
        
        public VectorClockProcess(int processId) {
            this.clock = new VectorClock(processId, NUM_PROCESSES);
            this.messageQueue = new LinkedBlockingQueue<>();
        }
        
        @Override
        public void run() {
            try {
                // Simular eventos internos e envio de mensagens
                for (int i = 0; i < NUM_MESSAGES; i++) {
                    // Evento interno
                    int[] vector = clock.tick();
                    logEvent("EVENTO INTERNO", vector, 
                        "Processo " + clock.getProcessId() + " executando evento interno " + (i + 1));
                    
                    // Pequeno delay aleatório para simular processamento
                    Thread.sleep((long) (Math.random() * 100));
                    
                    // Enviar mensagem para outro processo
                    int targetProcess = (clock.getProcessId() + 1) % NUM_PROCESSES;
                    vector = clock.tick();
                    VectorMessage msg = new VectorMessage(
                        clock.getProcessId(), 
                        targetProcess, 
                        "Mensagem " + messageCounter.incrementAndGet(), 
                        vector
                    );
                    logEvent("ENVIANDO", vector, 
                        "Processo " + clock.getProcessId() + " -> Processo " + targetProcess + 
                        ": " + msg.content);
                    
                    // Simular delay de rede
                    Thread.sleep((long) (Math.random() * 200));
                    
                    // Simular recebimento da mensagem
                    vector = clock.receive(msg.vector);
                    logEvent("RECEBENDO", vector, 
                        "Processo " + clock.getProcessId() + " recebeu: " + msg.content + 
                        " (enviado com " + Arrays.toString(msg.vector) + ")");
                    
                    // Demonstrar comparação de vetores
                    demonstrateVectorComparison(msg.vector, vector);
                    
                    // Delay antes do próximo ciclo
                    Thread.sleep((long) (Math.random() * 150));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private void logEvent(String eventType, int[] vector, String description) {
            System.out.printf("[%d] %s - %s [%s]\n", 
                clock.getProcessId(), eventType, description, Arrays.toString(vector));
        }
        
        private void demonstrateVectorComparison(int[] sentVector, int[] receivedVector) {
            int comparison = VectorClock.compare(sentVector, receivedVector);
            String relation;
            
            if (comparison == -1) {
                relation = "aconteceu antes";
            } else if (comparison == 1) {
                relation = "aconteceu depois";
            } else {
                relation = "é concorrente com";
            }
            
            System.out.printf("    [COMPARAÇÃO] Vetor enviado %s vetor recebido (%s)\n", 
                relation, Arrays.toString(receivedVector));
        }
    }
    
    /**
     * Classe para representar mensagens com relógio vetorial
     */
    static class VectorMessage {
        final int senderId;
        final int receiverId;
        final String content;
        final int[] vector;
        
        public VectorMessage(int senderId, int receiverId, String content, int[] vector) {
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.content = content;
            this.vector = Arrays.copyOf(vector, vector.length);
        }
    }
} 