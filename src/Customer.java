class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int retrievalRate;

    public Customer(TicketPool ticketPool, int retrievalRate) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
//                String message = Thread.currentThread().getName() + " is trying to purchase " + retrievalRate + " tickets.";
//                System.out.println(message);
//                Configuration.logToFile(message);
                ticketPool.removeTickets(retrievalRate);
                Thread.sleep(8000); // Simulate the delay between ticket retrievals
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
