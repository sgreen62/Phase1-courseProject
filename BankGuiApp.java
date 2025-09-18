package courseproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class BankGuiApp extends JFrame {

    // simple in-memory store keyed by customer ID
    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, Account>  accounts  = new HashMap<>();

    // customer fields
    private final JTextField tfCustId = new JTextField(8);
    private final JTextField tfFirst  = new JTextField(10);
    private final JTextField tfLast   = new JTextField(12);
    private final JTextField tfStreet = new JTextField(12);
    private final JTextField tfCity   = new JTextField(10);
    private final JComboBox<String> cbState = new JComboBox<>(new String[]{"NY","NJ","PA","MD","VA"});
    private final JTextField tfZip    = new JTextField(5);
    private final JTextField tfPhone  = new JTextField(10);

    // account fields
    private final JTextField tfAcctNo = new JTextField(6);
    private final JRadioButton rbChecking = new JRadioButton("Checking", true);
    private final JRadioButton rbSavings  = new JRadioButton("Savings");

    // transaction controls
    private final JTextField tfTxDate   = new JTextField(10); // yyyy-MM-dd
    private final JTextField tfTxAmount = new JTextField(8);
    private final JRadioButton rbDep = new JRadioButton("Deposit", true);
    private final JRadioButton rbWth = new JRadioButton("Withdraw");
    private final JRadioButton rbInt = new JRadioButton("Add Interest");

    // output
    private final JLabel lblStatus = new JLabel(" ");
    private final JLabel lblResult = new JLabel(" ");

    public BankGuiApp() {
        super("Course Project - GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        ((JComponent)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.anchor = GridBagConstraints.WEST;

        // customer panel
        JPanel pCust = new JPanel(new GridBagLayout());
        pCust.setBorder(BorderFactory.createTitledBorder("Customer"));
        int r = 0;
        addRow(pCust, r++, "Customer ID:", tfCustId);
        addRow(pCust, r++, "First Name:",  tfFirst);
        addRow(pCust, r++, "Last Name:",   tfLast);
        addRow(pCust, r++, "Street:",      tfStreet);
        addRow(pCust, r++, "City:",        tfCity);
        addRow(pCust, r++, "State:",       cbState);
        addRow(pCust, r++, "Zip (5 digits):",   tfZip);
        addRow(pCust, r++, "Phone (10 digits):", tfPhone);

        // account panel
        JPanel pAcct = new JPanel(new GridBagLayout());
        pAcct.setBorder(BorderFactory.createTitledBorder("Account"));
        ButtonGroup bgType = new ButtonGroup();
        bgType.add(rbChecking);
        bgType.add(rbSavings);
        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        typeRow.add(rbChecking);
        typeRow.add(rbSavings);

        int ar = 0;
        addRow(pAcct, ar++, "Account # (max 5):", tfAcctNo);
        addRow(pAcct, ar++, "Account Type:",      typeRow);

        // transaction panel
        JPanel pTx = new JPanel(new GridBagLayout());
        pTx.setBorder(BorderFactory.createTitledBorder("Transaction"));
        ButtonGroup bgTx = new ButtonGroup();
        bgTx.add(rbDep); bgTx.add(rbWth); bgTx.add(rbInt);
        JPanel txTypeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        txTypeRow.add(rbDep); txTypeRow.add(rbWth); txTypeRow.add(rbInt);

        int tr = 0;
        addRow(pTx, tr++, "Date (yyyy-MM-dd):", tfTxDate);
        addRow(pTx, tr++, "Amount (>0):",       tfTxAmount);
        addRow(pTx, tr++, "Transaction Type:",  txTypeRow);

        // layout sections
        g.gridx = 0; g.gridy = 0; form.add(pCust, g);
        g.gridx = 1; g.gridy = 0; form.add(pAcct, g);
        g.gridx = 0; g.gridy = 1; g.gridwidth = 2; form.add(pTx, g);
        add(form, BorderLayout.CENTER);

        // buttons
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnAdd      = new JButton("Add New Customer and Account");
        JButton btnDisplay  = new JButton("Display Customer and Account Data");
        JButton btnTransact = new JButton("Perform Transaction");
        JButton btnClear    = new JButton("Clear");
        pBtns.add(btnAdd); pBtns.add(btnDisplay); pBtns.add(btnTransact); pBtns.add(btnClear);
        add(pBtns, BorderLayout.NORTH);

        // bottom labels
        JPanel pBottom = new JPanel(new GridLayout(2,1,6,6));
        lblStatus.setForeground(new Color(20,90,20));
        pBottom.add(wrap("Status: ", lblStatus));
        pBottom.add(wrap("Result: ", lblResult));
        add(pBottom, BorderLayout.SOUTH);

        // actions
        btnAdd.addActionListener(this::onAdd);
        btnDisplay.addActionListener(this::onDisplay);
        btnTransact.addActionListener(this::onTransact);
        btnClear.addActionListener(e -> clearAll());

        setSize(900, 520);
        setLocationRelativeTo(null);
    }

    // ===== actions =====
    private void onAdd(ActionEvent e) {
        try {
            String id    = mustLen(tfCustId.getText(), 1, 5,  "Customer ID");
            String first = mustLen(tfFirst.getText(),  1, 15, "First Name");
            String last  = mustLen(tfLast.getText(),   1, 20, "Last Name");
            String street= mustLen(tfStreet.getText(), 1, 20, "Street");
            String city  = mustLen(tfCity.getText(),   1, 20, "City");
            String state = (String) cbState.getSelectedItem();
            String zip   = mustDigits(tfZip.getText(),   5, "Zip");
            String phone = mustDigits(tfPhone.getText(), 10, "Phone");
            String acctNo= mustLen(tfAcctNo.getText(), 1, 5, "Account #");

            Customer c = new Customer();
            c.setCustomerID(id);
            c.setFirstName(first);
            c.setLastName(last);
            c.setStreet(street);
            c.setCity(city);
            c.setState(state);
            c.setZip(zip);
            c.setPhone(phone);

            Account acct = rbChecking.isSelected() ? new CheckingAccount() : new SavingsAccount();
            acct.setAccountNumber(acctNo);

            customers.put(id, c);
            accounts.put(id, acct);

            setStatus("saved customer/account for ID " + id, true);
            lblResult.setText(" ");
        } catch (IllegalArgumentException ex) {
            setStatus(ex.getMessage(), false);
        }
    }

    private void onDisplay(ActionEvent e) {
        String id = tfCustId.getText().trim();
        if (!customers.containsKey(id)) {
            setStatus("no customer for ID " + id, false);
            return;
        }
        Customer c = customers.get(id);
        Account a = accounts.get(id);
        setStatus("displaying " + id, true);
        lblResult.setText(String.format(
            "ID=%s  Acct#=%s  Type=%s  Balance=%.2f  Name=%s %s  City=%s  State=%s",
            id, a.getAccountNumber(), a.getAccountType(), a.balance(),
            c.getFirstName(), c.getLastName(), c.getCity(), c.getState()
        ));
    }

    private void onTransact(ActionEvent e) {
        try {
            String id = mustLen(tfCustId.getText(), 1, 5, "Customer ID");
            if (!customers.containsKey(id) || !accounts.containsKey(id)) {
                throw new IllegalArgumentException("customer/account not found for ID " + id);
            }
            Account acct = accounts.get(id);

            // date
            LocalDate date;
            try {
                date = LocalDate.parse(tfTxDate.getText().trim());
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("enter date like yyyy-MM-dd");
            }

            // interest path
            if (rbInt.isSelected()) {
                if (acct instanceof CheckingAccount) {
                    ((CheckingAccount) acct).applyInterest();
                    lblResult.setText(formatLine(id, acct, date, "INT", 0.0, "add 2%"));
                } else {
                    ((SavingsAccount) acct).applyInterest();
                    lblResult.setText(formatLine(id, acct, date, "INT", 0.0, "add 5%"));
                }
                setStatus("interest added", true);
                return;
            }

            // deposit/withdraw path
            double amt  = parsePositive(tfTxAmount.getText().trim(), "Amount");
            String type = rbDep.isSelected() ? "DEP" : "WTH";
            String note;

            if (acct instanceof CheckingAccount) {
                CheckingAccount ca = (CheckingAccount) acct;
                ca.setTransaction(date, type, amt);
                if ("DEP".equals(type)) {
                    ca.deposit(); note = "fee 0.50";
                } else {
                    double preview = ca.balance() - amt - ca.getServiceFee();
                    boolean overdraft = preview < 0;
                    ca.withdrawal();
                    note = overdraft ? "fee 0.50 + overdraft 30.00" : "fee 0.50";
                }
                lblResult.setText(formatLine(id, acct, date, type, amt, note));
                setStatus("transaction complete", true);

            } else { // SavingsAccount
                SavingsAccount sa = (SavingsAccount) acct;
                sa.setTransaction(date, type, amt);
                if ("DEP".equals(type)) {
                    sa.deposit(); note = "fee 0.25";
                } else {
                    double before = sa.balance();
                    sa.withdrawal();
                    note = (sa.balance() == before) ? "denied" : "fee 0.25";
                }
                lblResult.setText(formatLine(id, acct, date, type, amt, note));
                setStatus("transaction processed", true);
            }

        } catch (IllegalArgumentException ex) {
            setStatus(ex.getMessage(), false);
        }
    }

    private void clearAll() {
        for (JTextField tf : new JTextField[]{tfCustId, tfFirst, tfLast, tfStreet, tfCity, tfZip, tfPhone, tfAcctNo, tfTxDate, tfTxAmount}) {
            tf.setText("");
        }
        rbChecking.setSelected(true);
        rbDep.setSelected(true);
        cbState.setSelectedIndex(0);
        lblStatus.setText(" ");
        lblResult.setText(" ");
        tfCustId.requestFocus();
    }

    // ===== helpers =====
    private static void addRow(JPanel p, int row, String label, Component field) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = row; p.add(new JLabel(label), g);
        g.gridx = 1; g.gridy = row; p.add(field, g);
    }

    private static JPanel wrap(String prefix, JComponent comp) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.add(new JLabel(prefix));
        panel.add(comp);
        return panel;
    }

    private static String mustLen(String s, int min, int max, String name) {
        String v = s == null ? "" : s.trim();
        if (v.length() < min || v.length() > max) throw new IllegalArgumentException(name + " length " + min + "-" + max);
        return v;
    }

    private static String mustDigits(String s, int len, String name) {
        String v = s == null ? "" : s.trim();
        if (v.length() != len || !v.matches("\\d+")) throw new IllegalArgumentException(name + " must be " + len + " digits");
        return v;
    }

    private static double parsePositive(String s, String name) {
        try {
            double v = Double.parseDouble(s);
            if (v <= 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(name + " must be > 0");
        }
    }

    private static String formatLine(String id, Account acct, LocalDate d, String txType, double amt, String note) {
        return String.format(
            "ID=%s  Acct#=%s  Type=%s  Date=%s  Tx=%s  Amount=%.2f  Note=%s  Balance=%.2f",
            id, acct.getAccountNumber(), acct.getAccountType(),
            d == null ? "-" : d.toString(),
            txType, amt, note, acct.balance()
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankGuiApp().setVisible(true));
    }
}
