import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Configuration config = null;
        TicketPool ticketPool = null;
        File configFile = new File("config.txt");

        while (true) {
            System.out.print("Load previous configuration? (yes/no): ");
            String loadConfig = scanner.nextLine().trim().toLowerCase();
            if (loadConfig.equals("yes")) {
                config = Configuration.loadFromFile(configFile.getName());
                if (config == null) {
                    config = createConfiguration(scanner, configFile.getName());
                }
                break;
            } else if (loadConfig.equals("no")) {
                config = createConfiguration(scanner, configFile.getName());
                break;
            } else {
                System.out.println("Invalid command. Please enter 'yes' or 'no'.");
            }
        }

        //holding all the threads including customers and vendors
        List<Thread> threads = new ArrayList<>();

        //Flag to indicate the system in running state or not
        boolean isRunning = false;

        try {
            ticketPool = new TicketPool(config.getTotalTickets(), config.getMaxTicketCapacity(), "log.txt");

            int numVendors = getInput(scanner, "Enter the number of vendors: ");
            int numCustomers = getInput(scanner, "Enter the number of customers: ");

            while (true) {
                System.out.println("Enter command (start/stop/exit): ");
                String command = scanner.nextLine().toLowerCase();

                switch (command) {
                    case "start":
                        if (!isRunning) {
                            String startMessage = "Starting ticket handling operations...";
                            System.out.println(startMessage);
                            Configuration.logToFile(startMessage);
                            threads.clear(); // Clear old threads

                            //Create new Vendor threads
                            for (int i = 0; i < numVendors; i++) {
                                threads.add(new Thread(new Vendor(ticketPool, config.getTicketReleaseRate()), "Vendor " + (i + 1)));
                            }

                            //Create new Customer threads
                            for (int i = 0; i < numCustomers; i++) {
                                threads.add(new Thread(new Customer(ticketPool, config.getCustomerRetrievalRate()), "Customer " + (i + 1)));
                            }

                            //Start all threads
                            threads.forEach(Thread::start);
                            isRunning = true;
                        } else {
                            String alreadyRunningMessage = "Operations are already running.";
                            System.out.println(alreadyRunningMessage);
                            Configuration.logToFile(alreadyRunningMessage);
                        }
                        break;

                    case "stop":
                        if (isRunning) {
                            String stopMessage = "Stopping all operations...";
                            System.out.println(stopMessage);
                            Configuration.logToFile(stopMessage);
                            threads.forEach(Thread::interrupt);
                            threads.forEach(thread -> {
                                try {
                                    thread.join();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            });
                            isRunning = false;
                        } else {
                            String notRunningMessage = "Operations are not running.";
                            System.out.println(notRunningMessage);
                            Configuration.logToFile(notRunningMessage);
                        }
                        break;

                    case "exit":
                        String exitMessage = "Exiting the system...";
                        System.out.println(exitMessage);
                        Configuration.logToFile(exitMessage);
                        if (isRunning) {
                            threads.forEach(Thread::interrupt);
                            threads.forEach(thread -> {
                                try {
                                    thread.join();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            });
                        }
                        if (ticketPool != null) {
                            ticketPool.closeLog();
                        }
                        System.exit(0);

                    default:
                        String invalidCommandMessage = "Invalid command. Please enter start, stop, or exit.";
                        System.out.println(invalidCommandMessage);
                        Configuration.logToFile(invalidCommandMessage);
                }
            }

        } catch (IOException e) {
            System.err.println("Error initializing the system: " + e.getMessage());
        }
    }

    //Create a new configuration using inputs by user
    private static Configuration createConfiguration(Scanner scanner, String configFileName) {
        Configuration config = new Configuration();
        config.setMaxTicketCapacity(getInput(scanner, "Enter max ticket capacity: "));
        while (true) {
            config.setTotalTickets(getInput(scanner, "Enter total tickets: "));
            if (config.getTotalTickets() <= config.getMaxTicketCapacity()) {
                break;
            } else {
                System.out.println("Max ticket capacity exceeded. Please enter a value less than or equal to max ticket capacity.");
            }
        }
        config.setTicketReleaseRate(getInput(scanner, "Enter ticket release rate: "));
        config.setCustomerRetrievalRate(getInput(scanner, "Enter customer retrieval rate: "));
        config.saveToFile(configFileName);
        return config;
    }

    //Validated integer inputs
    private static int getInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}

