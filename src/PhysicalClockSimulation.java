import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Parte 1: Simulação de eventos distribuídos usando relógios físicos
 * Demonstra como a ordem dos eventos pode parecer incorreta sem controle lógico
 */
public class PhysicalClockSimulation {
    private static final int NUM_PROCESSES = 3;
    private static final int NUM_MESSAGES = 5;
    private static final AtomicInteger messageCounter = new AtomicInteger(0);
    
    public static void main(String[] args) {
        System.out.println("=== PARTE 1: SIMULAÇÃO DE RELÓGIOS FÍSICOS ===\n");
        System.out.println("Demonstrando inconsistências na ordem dos eventos...\n");
        
        // Criar executor para gerenciar as threads
        ExecutorService executor = Executors.newFixedThreadPool(NUM_PROCESSES);
        
        // Criar e iniciar os processos
        for (int i = 0; i < NUM_PROCESSES; i++) {
            executor.submit(new PhysicalClockProcess(i));
        }
        
        // Aguardar um tempo para as simulações terminarem
        try {
            Thread.sleep(10000);
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
        
        System.out.println("\n=== FIM DA SIMULAÇÃO DE RELÓGIOS FÍSICOS ===");
        System.out.println("Observe como os timestamps físicos podem mostrar");
        System.out.println("inconsistências na ordem dos eventos!\n");
    }
    
    /**
     * Processo que simula um nó distribuído usando relógio físico
     */
    static class PhysicalClockProcess implements Runnable {
        private final int processId;
        private final BlockingQueue<Message> messageQueue;
        
        public PhysicalClockProcess(int processId) {
            this.processId = processId;
            this.messageQueue = new LinkedBlockingQueue<>();
        }
        
        @Override
        public void run() {
            try {
                // Simular eventos internos e envio de mensagens
                for (int i = 0; i < NUM_MESSAGES; i++) {
                    // Evento interno
                    long timestamp = System.currentTimeMillis();
                    logEvent("EVENTO INTERNO", timestamp, "Processo " + processId + " executando evento interno " + (i + 1));
                    
                    // Pequeno delay aleatório para simular processamento
                    Thread.sleep((long) (Math.random() * 100));
                    
                    // Enviar mensagem para outro processo
                    int targetProcess = (processId + 1) % NUM_PROCESSES;
                    timestamp = System.currentTimeMillis();
                    Message msg = new Message(processId, targetProcess, "Mensagem " + messageCounter.incrementAndGet(), timestamp);
                    logEvent("ENVIANDO", timestamp, "Processo " + processId + " -> Processo " + targetProcess + ": " + msg.content);
                    
                    // Simular delay de rede
                    Thread.sleep((long) (Math.random() * 200));
                    
                    // Simular recebimento da mensagem
                    timestamp = System.currentTimeMillis();
                    logEvent("RECEBENDO", timestamp, "Processo " + processId + " recebeu: " + msg.content + " (enviado em " + msg.timestamp + ")");
                    
                    // Delay antes do próximo ciclo
                    Thread.sleep((long) (Math.random() * 150));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private void logEvent(String eventType, long timestamp, String description) {
            System.out.printf("[%d] %s - %s (timestamp: %d)\n", 
                processId, eventType, description, timestamp);
        }
    }
    
    /**
     * Classe para representar mensagens entre processos
     */
    static class Message {
        final int senderId;
        final int receiverId;
        final String content;
        final long timestamp;
        
        public Message(int senderId, int receiverId, String content, long timestamp) {
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.content = content;
            this.timestamp = timestamp;
        }
    }
} 