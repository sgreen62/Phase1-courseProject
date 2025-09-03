package courseproject;

import java.time.LocalDate;

public class SavingsAccount extends Account implements AccountInterface {

    // transaction data (validated in BankAcctApp)
    private LocalDate transactionDate;
    private String transactionType;    // DEP or WTH
    private double transactionAmount;  // > 0

    // required by spec
    // service fee: $0.25 each transaction
    // NO negative balances allowed; deny withdrawal if not enough funds
    // interest rate: 5%
    public SavingsAccount() {
        setAccountType("SAV");
        setServiceFee(0.25);
        setOverdraftFee(0.00); // not used
        setInterestRate(5.0);
    }

    public void setTransaction(java.time.LocalDate date, String type, double amount) {
        this.transactionDate = date;
        this.transactionType = type;
        this.transactionAmount = amount;
    }

    public LocalDate getTransactionDate() { return transactionDate; }
    public String getTransactionType() { return transactionType; }
    public double getTransactionAmount() { return transactionAmount; }

    @Override
    public void withdrawal() {
        double preview = getBalanceValue() - transactionAmount - getServiceFee();
        if (preview < 0) {
            // deny per spec
            System.out.println("Savings withdrawal denied: insufficient funds.");
            return;
        }
        setBalanceValue(preview);
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

    public void applyInterest() {
        double earned = getBalanceValue() * (getInterestRate() / 100.0);
        setBalanceValue(getBalanceValue() + earned);
    }
}
