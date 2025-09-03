package courseproject;

import java.time.LocalDate;

public class CheckingAccount extends Account implements AccountInterface {

    // transaction data (validated in BankAcctApp)
    private LocalDate transactionDate;
    private String transactionType;    // DEP or WTH
    private double transactionAmount;  // > 0

    // required by spec: fees and rules
    // service fee: $0.50 each transaction
    // overdraft fee: $30 if balance goes below 0 after withdrawal
    // interest rate: 2%
    public CheckingAccount() {
        setAccountType("CHK");
        setServiceFee(0.50);
        setOverdraftFee(30.00);
        setInterestRate(2.0);
    }

    // setters for transaction info
    public void setTransaction(LocalDate date, String type, double amount) {
        this.transactionDate = date;
        this.transactionType = type;
        this.transactionAmount = amount;
    }

    public LocalDate getTransactionDate() { return transactionDate; }
    public String getTransactionType() { return transactionType; }
    public double getTransactionAmount() { return transactionAmount; }

    @Override
    public void withdrawal() {
        double newBal = getBalanceValue() - transactionAmount - getServiceFee();
        // overdraft rule
        if (newBal < 0) {
            newBal -= getOverdraftFee();
        }
        setBalanceValue(newBal);
    }

    @Override
    public void deposit() {
        double newBal = getBalanceValue() + transactionAmount - getServiceFee();
        setBalanceValue(newBal);
    }

    @Override
    public double balance() {
        return getBalanceValue();
    }

    // helper to add interest (simple add per instruction step)
    public void applyInterest() {
        double earned = getBalanceValue() * (getInterestRate() / 100.0);
        setBalanceValue(getBalanceValue() + earned);
    }
}
