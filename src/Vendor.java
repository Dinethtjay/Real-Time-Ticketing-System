class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int releaseRate;

    public Vendor(TicketPool ticketPool, int releaseRate) {
        this.ticketPool = ticketPool;
        this.releaseRate = releaseRate;
    }

    //Vendor thread behaviour
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

                ticketPool.addTickets(releaseRate);
                Thread.sleep(8000); // Simulate the delay between ticket additions
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

