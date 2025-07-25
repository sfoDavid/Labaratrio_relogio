import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Parte 2: Simulação usando relógios lógicos de Lamport
 * Demonstra a consistência causal entre eventos
 */
public class LamportSimulation {
    private static final int NUM_PROCESSES = 3;
    private static final int NUM_MESSAGES = 5;
    private static final AtomicInteger messageCounter = new AtomicInteger(0);
    
    public static void main(String[] args) {
        System.out.println("=== PARTE 2: SIMULAÇÃO DE RELÓGIO LÓGICO DE LAMPORT ===\n");
        System.out.println("Demonstrando consistência causal entre eventos...\n");
        
        // Criar executor para gerenciar as threads
        ExecutorService executor = Executors.newFixedThreadPool(NUM_PROCESSES);
        
        // Criar e iniciar os processos
        for (int i = 0; i < NUM_PROCESSES; i++) {
            executor.submit(new LamportProcess(i));
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
        
        System.out.println("\n=== FIM DA SIMULAÇÃO DE LAMPORT ===");
        System.out.println("Observe como os relógios lógicos garantem");
        System.out.println("a consistência causal entre eventos!\n");
    }
    
    /**
     * Processo que simula um nó distribuído usando relógio lógico de Lamport
     */
    static class LamportProcess implements Runnable {
        private final LamportClock clock;
        private final BlockingQueue<LamportMessage> messageQueue;
        
        public LamportProcess(int processId) {
            this.clock = new LamportClock(processId);
            this.messageQueue = new LinkedBlockingQueue<>();
        }
        
        @Override
        public void run() {
            try {
                // Simular eventos internos e envio de mensagens
                for (int i = 0; i < NUM_MESSAGES; i++) {
                    // Evento interno
                    int logicalTime = clock.tick();
                    logEvent("EVENTO INTERNO", logicalTime, 
                        "Processo " + clock.getProcessId() + " executando evento interno " + (i + 1));
                    
                    // Pequeno delay aleatório para simular processamento
                    Thread.sleep((long) (Math.random() * 100));
                    
                    // Enviar mensagem para outro processo
                    int targetProcess = (clock.getProcessId() + 1) % NUM_PROCESSES;
                    logicalTime = clock.tick();
                    LamportMessage msg = new LamportMessage(
                        clock.getProcessId(), 
                        targetProcess, 
                        "Mensagem " + messageCounter.incrementAndGet(), 
                        logicalTime
                    );
                    logEvent("ENVIANDO", logicalTime, 
                        "Processo " + clock.getProcessId() + " -> Processo " + targetProcess + 
                        ": " + msg.content + " (L=" + logicalTime + ")");
                    
                    // Simular delay de rede
                    Thread.sleep((long) (Math.random() * 200));
                    
                    // Simular recebimento da mensagem
                    logicalTime = clock.receive(msg.logicalTime);
                    logEvent("RECEBENDO", logicalTime, 
                        "Processo " + clock.getProcessId() + " recebeu: " + msg.content + 
                        " (enviado com L=" + msg.logicalTime + ", agora L=" + logicalTime + ")");
                    
                    // Delay antes do próximo ciclo
                    Thread.sleep((long) (Math.random() * 150));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private void logEvent(String eventType, int logicalTime, String description) {
            System.out.printf("[%d] %s - %s\n", 
                clock.getProcessId(), eventType, description);
        }
    }
    
    /**
     * Classe para representar mensagens com relógio lógico de Lamport
     */
    static class LamportMessage {
        final int senderId;
        final int receiverId;
        final String content;
        final int logicalTime;
        
        public LamportMessage(int senderId, int receiverId, String content, int logicalTime) {
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.content = content;
            this.logicalTime = logicalTime;
        }
    }
} 