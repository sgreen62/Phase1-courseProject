package courseproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class BankGuiApp extends JFrame {
    // storage
    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, Account> accounts = new HashMap<>();

    // customer fields
    private final JTextField tfCustId = new JTextField("C1001", 10);
    private final JTextField tfFirst = new JTextField("Shawn", 10);
    private final JTextField tfLast = new JTextField("Green", 10);
    private final JTextField tfStreet = new JTextField("123 Main St", 12);
    private final JTextField tfCity = new JTextField("Philly", 10);
    private final JComboBox<String> cbState = new JComboBox<>(new String[]{"PA","NJ","NY","DE","MD","VA"});
    private final JTextField tfZip = new JTextField("19103", 6);
    private final JTextField tfPhone = new JTextField("2155551234", 12);
    private final JTextField tfSSN = new JTextField("123456789", 12);

    // account type
    private final JRadioButton rbChecking = new JRadioButton("Checking", true);
    private final JRadioButton rbSavings = new JRadioButton("Savings");

    // opening deposit
    private final JTextField tfOpenAmt = new JTextField("500.00", 8);

    // transaction
    private final JComboBox<String> cbTxn = new JComboBox<>(new String[]{"Deposit","Withdraw","Add Interest"});
    private final JTextField tfAmt = new JTextField("100.00", 8);

    // output
    private final JLabel lblStatus = new JLabel("status...");
    private final JTextArea taResult = new JTextArea(6, 50);

    public BankGuiApp() {
        super("Bank GUI (Phase 4)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));

        // top: customer + account panel
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        addRow(top, g, r++, new JLabel("Customer ID:"), tfCustId, new JLabel("First:"), tfFirst, new JLabel("Last:"), tfLast);
        addRow(top, g, r++, new JLabel("Street:"), tfStreet, new JLabel("City:"), tfCity, new JLabel("State:"), cbState);
        addRow(top, g, r++, new JLabel("ZIP:"), tfZip, new JLabel("Phone:"), tfPhone, new JLabel("SSN:"), tfSSN);

        ButtonGroup grp = new ButtonGroup();
        grp.add(rbChecking); grp.add(rbSavings);
        JPanel acctType = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        acctType.add(new JLabel("Account:"));
        acctType.add(rbChecking);
        acctType.add(rbSavings);
        acctType.add(new JLabel("Opening Deposit:"));
        acctType.add(tfOpenAmt);

        g.gridx = 0; g.gridy = r++; g.gridwidth = 6;
        top.add(acctType, g);

        add(top, BorderLayout.NORTH);

        // center: transaction panel
        JPanel mid = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        mid.add(new JLabel("Transaction:"));
        mid.add(cbTxn);
        mid.add(new JLabel("Amount:"));
        mid.add(tfAmt);

        JButton btnAdd = new JButton("Add New Customer and Account");
        JButton btnShow = new JButton("Display Customer and Account Data");
        JButton btnDo = new JButton("Perform Transaction");
        JButton btnClear = new JButton("Clear");

        mid.add(btnAdd);
        mid.add(btnShow);
        mid.add(btnDo);
        mid.add(btnClear);
        add(mid, BorderLayout.CENTER);

        // bottom: status + result
        JPanel bottom = new JPanel(new BorderLayout(6,6));
        lblStatus.setForeground(new Color(30, 90, 30));
        bottom.add(lblStatus, BorderLayout.NORTH);
        taResult.setEditable(false);
        taResult.setLineWrap(true);
        taResult.setWrapStyleWord(true);
        bottom.add(new JScrollPane(taResult), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // actions
        btnAdd.addActionListener(this::onAdd);
        btnShow.addActionListener(this::onShow);
        btnDo.addActionListener(this::onDo);
        btnClear.addActionListener(e -> clearFields());

        pack();
        setLocationRelativeTo(null);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, Component... comps) {
        int col = 0;
        for (Component c : comps) {
            g.gridx = col++; g.gridy = row; g.gridwidth = 1;
            p.add(c, g);
        }
    }

    private void onAdd(ActionEvent e) {
        String id = tfCustId.getText().trim();
        if (id.isEmpty()) { setStatus("Enter Customer ID."); return; }

        Customer c = new Customer();
        c.setCustomerID(id);
        c.setFirstName(tfFirst.getText().trim());
        c.setLastName(tfLast.getText().trim());
        c.setStreet(tfStreet.getText().trim());
        c.setCity(tfCity.getText().trim());
        c.setState(cbState.getSelectedItem().toString());
        c.setZip(tfZip.getText().trim());
        c.setPhone(tfPhone.getText().trim());
        c.setSsn(tfSSN.getText().trim());

        Account acc = rbChecking.isSelected() ? new CheckingAccount() : new SavingsAccount();

        // optional opening deposit
        double open = parseAmt(tfOpenAmt.getText());
        if (open > 0) acc.deposit(open);

        customers.put(id, c);
        accounts.put(id, acc);

        taResult.setText("Created " + id + " => " + acc.getAccountNumber() + " (" + acc.getAccountType() + ")\n"
                + "Opening deposit: $" + String.format("%.2f", Math.max(0, open)) + " | Note: " + acc.getLastNote()
                + "\nBalance: $" + String.format("%.2f", acc.balance()));
        setStatus("Customer and account added.");
    }

    private void onShow(ActionEvent e) {
        String id = tfCustId.getText().trim();
        Customer c = customers.get(id);
        Account a = accounts.get(id);
        if (c == null || a == null) { setStatus("Not found. Add customer first."); return; }
        taResult.setText(
            "Customer: " + c + "\n" +
            "Account#: " + a.getAccountNumber() + " | Type: " + a.getAccountType() + "\n" +
            "Balance: $" + String.format("%.2f", a.balance())
        );
        setStatus("Shown.");
    }

    private void onDo(ActionEvent e) {
        String id = tfCustId.getText().trim();
        Account a = accounts.get(id);
        if (a == null) { setStatus("No account for that Customer ID."); return; }

        String action = cbTxn.getSelectedItem().toString();
        if ("Add Interest".equals(action)) {
            a.addInterest();
            taResult.append("\n\nTX: ADD_INTEREST | Note: " + a.getLastNote()
                    + "\nNew Balance: $" + String.format("%.2f", a.balance()));
            setStatus(a.wasLastSuccess() ? "Interest applied." : "Interest skipped.");
            return;
        }

        double amt = parseAmt(tfAmt.getText());
        if (amt <= 0) { setStatus("Enter amount > 0"); return; }

        if ("Deposit".equals(action)) {
            a.deposit(amt);
        } else { // Withdraw
            a.withdrawal(amt);
        }

        taResult.append("\n\nTX: " + action.toUpperCase() + " $" + String.format("%.2f", amt)
                + " | Note: " + a.getLastNote()
                + "\nNew Balance: $" + String.format("%.2f", a.balance()));
        setStatus(a.wasLastSuccess() ? "Transaction ok." : "Transaction denied.");
    }

    private void clearFields() {
        tfCustId.setText("");
        tfFirst.setText("");
        tfLast.setText("");
        tfStreet.setText("");
        tfCity.setText("");
        cbState.setSelectedIndex(0);
        tfZip.setText("");
        tfPhone.setText("");
        tfSSN.setText("");
        tfOpenAmt.setText("0.00");
        tfAmt.setText("0.00");
        taResult.setText("");
        setStatus("Cleared.");
    }

    private double parseAmt(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception ex) { return 0; }
    }

    private void setStatus(String s) { lblStatus.setText(s); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankGuiApp().setVisible(true));
    }
}
