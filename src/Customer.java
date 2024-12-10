class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int retrievalRate;

    public Customer(TicketPool ticketPool, int retrievalRate) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
    }

    //Customer thread behaviour
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

                ticketPool.removeTickets(retrievalRate);
                Thread.sleep(8000); // Simulate the delay between ticket retrievals
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
