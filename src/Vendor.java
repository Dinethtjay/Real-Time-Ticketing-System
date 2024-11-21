class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int releaseRate;

    public Vendor(TicketPool ticketPool, int releaseRate) {
        this.ticketPool = ticketPool;
        this.releaseRate = releaseRate;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

//                String message = Thread.currentThread().getName() + " is adding " + releaseRate + " tickets.";
//                System.out.println(message);
//                Configuration.logToFile(message);
                ticketPool.addTickets(releaseRate);
                Thread.sleep(8000); // Simulate the delay between ticket additions
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

