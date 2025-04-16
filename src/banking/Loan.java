package banking;

public class Loan extends Transaction {
    private boolean isApproved;
    private boolean isPaidOff;
    private double amountPaid;

    public Loan(double amount, String description) {
        super(amount, description);
        this.isApproved = false;
        this.isPaidOff = false;
        this.amountPaid = 0.0;
    }

    public Loan(double amount, String description, String transactionID) {
        super(amount, description, transactionID);
        this.isApproved = false;
        this.isPaidOff = false;
        this.amountPaid = 0.0;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public boolean isPaidOff() {
        return isPaidOff;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void approve() {
        this.isApproved = true;
    }
}
