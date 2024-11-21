import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketPool {
    private final List<String> tickets;
    private final int maxCapacity;
    private final BufferedWriter logWriter;

    public TicketPool(int initialTickets, int maxCapacity, String logFileName) throws IOException {
        this.tickets = Collections.synchronizedList(new ArrayList<>());
        this.maxCapacity = maxCapacity;

        // Initialize log file
        this.logWriter = new BufferedWriter(new FileWriter(logFileName, true)); // Append mode

        // Pre-fill the ticket pool
        for (int i = 0; i < initialTickets; i++) {
            tickets.add("Ticket: " + (i + 1));
        }
        log("Initialized with " + initialTickets + " tickets. Max Capacity: " + maxCapacity);
    }

    public void addTickets(int count) {
        synchronized (tickets) {
            while (tickets.size() + count > maxCapacity) {
                try {
                    String message = Thread.currentThread().getName() + ": Total tickets reached max capacity. Vendor waiting to add more tickets.";
                    System.out.println(message);
                    Configuration.logToFile(message);
                    tickets.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            for (int i = 0; i < count; i++) {
                tickets.add("Ticket: " + (tickets.size() + 1));
            }

            String message = Thread.currentThread().getName() + " added " + count + " tickets. Total ticket count: " + tickets.size();
            System.out.println(message);
            Configuration.logToFile(message);
            tickets.notifyAll();
        }
    }

    public void removeTickets(int count) throws InterruptedException {
        synchronized (tickets) {
            while (tickets.isEmpty()) {
                try {
                    String message = Thread.currentThread().getName() + ": Waiting to buy tickets...";
                    System.out.println(message);
                    Configuration.logToFile(message);
                    tickets.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            /*for (int i = 0; i < count; i++) {
                if (!tickets.isEmpty()) { // Ensure the list is not empty before removing
                    tickets.remove(0);
                    String message = Thread.currentThread().getName() + " purchased a ticket. Current ticket count in the pool: " + tickets.size();
                    System.out.println(message);
                    Configuration.logToFile(message);
                } else {
                    break; // Exit the loop if the list becomes empty
                }
            }*/
            tickets.remove(0);
            String message = Thread.currentThread().getName() + " purchased a ticket. Current ticket count in the pool: " + tickets.size();
            System.out.println(message);
            Configuration.logToFile(message);

            tickets.notifyAll(); // Notify other threads after the removal
        }
    }



    public int getTicketCount() {
        synchronized (tickets) {
            return tickets.size();
        }
    }

    public void closeLog() {
        try {
            logWriter.close();
        } catch (IOException e) {
            System.err.println("Error closing log file: " + e.getMessage());
        }
    }

    private void log(String message) {
        try {
            logWriter.write(message);
            logWriter.newLine();
            logWriter.flush();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
