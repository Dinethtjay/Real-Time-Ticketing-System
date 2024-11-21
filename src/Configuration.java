import java.io.*;

public class Configuration {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    // Log file path (same as the configuration file)
    private static final String CONFIG_FILE_PATH = "log.txt";


    // Getters and setters for all parameters
    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    // Save configuration to a text file and log the event
    public void saveToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("totalTickets=" + totalTickets + "\n");
            writer.write("ticketReleaseRate=" + ticketReleaseRate + "\n");
            writer.write("customerRetrievalRate=" + customerRetrievalRate + "\n");
            writer.write("maxTicketCapacity=" + maxTicketCapacity + "\n");

            // Log the save action directly to the console
            System.out.println("Configuration saved to file: " + fileName);
            logToFile("Configuration saved to file: " + fileName);

        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
            logToFile("Error saving configuration: " + e.getMessage());
        }
    }

    // Load configuration from a text file and log the event
    public static Configuration loadFromFile(String fileName) {
        Configuration config = new Configuration();

        File configFile = new File(fileName);

        // Check if the file exists and is not empty
        if(!configFile.exists() || configFile.length()==0){
            System.out.println("No previous configurations found. Please configure the system");
            return null;
        }

        String[] parameters = new String[4];
        int index = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null && index < 4) {
                parameters[index++] = line.split("=")[1].trim();
            }

            // Set the values from the file if present
            config.setTotalTickets(Integer.parseInt(parameters[0]));
            config.setTicketReleaseRate(Integer.parseInt(parameters[1]));
            config.setCustomerRetrievalRate(Integer.parseInt(parameters[2]));
            config.setMaxTicketCapacity(Integer.parseInt(parameters[3]));

            // Log the successful load
            System.out.println("Configuration loaded successfully from file: " + fileName);
            logToFile("Configuration loaded successfully from file: " + fileName);

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading configuration. Please ensure the file is valid.");
            logToFile("Error loading configuration: " + e.getMessage());
        }

        return config;
    }

    // Log method to append log entries directly to the configuration file
    static void logToFile(String message) {
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(CONFIG_FILE_PATH, true))) {
            String logEntry = System.currentTimeMillis() + " - " + message;
            logWriter.write(logEntry);
            logWriter.newLine();
        } catch (IOException e) {
            System.err.println("Error logging message: " + e.getMessage());
        }
    }
}
