import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * Custom Exception for insufficient balance.
 */
class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

/**
 * Represents a simplified Bank Account with deposit, withdraw, transfer, and transaction history.
 */
class Account {
    private String accountNumber;
    private String accountHolderName;
    private double balance;
    private List<String> transactions;

    public Account(String accountNumber, String accountHolderName, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        this.transactions.add("Account opened with initial deposit: $" + String.format("%.2f", initialDeposit));
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public double getBalance() { return balance; }


    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println(" Error: Deposit amount must be positive.");
            return;
        }
        balance += amount;
        transactions.add("Deposited: $" + String.format("%.2f", amount) + " | Balance: $" + String.format("%.2f", balance));
        System.out.println(" Successfully deposited $" + String.format("%.2f", amount));
    }

    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount <= 0) {
            System.out.println(" Error: Withdrawal amount must be positive.");
            return;
        }
        if (amount > balance) {
            throw new InsufficientBalanceException("Insufficient funds! Available: $" + String.format("%.2f", balance));
        }
        balance -= amount;
        transactions.add("Withdrew: $" + String.format("%.2f", amount) + " | Balance: $" + String.format("%.2f", balance));
        System.out.println(" Successfully withdrew $" + String.format("%.2f", amount));
    }

    public void transfer(Account targetAccount, double amount) throws InsufficientBalanceException {
        this.withdraw(amount);
        targetAccount.deposit(amount);
        transactions.add("Transferred $" + String.format("%.2f", amount) + " to Acc: " + targetAccount.getAccountNumber());
        System.out.println(" Successfully transferred $" + String.format("%.2f", amount) + " to " + targetAccount.getAccountHolderName());
    }

    public void printStatement() {
        System.out.println("\n-------------------------------------------");
        System.out.println("ACCOUNT STATEMENT: " + accountNumber + " (" + accountHolderName + ")");
        System.out.println("-------------------------------------------");
        for (String txn : transactions) {
            System.out.println("• " + txn);
        }
        System.out.println("Current Available Balance: $" + String.format("%.2f", balance));
        System.out.println("-------------------------------------------");
    }
}


/**
 * Main application class providing a CLI interactive menu.
 */
public class BankingInformationSystem {
    private static Map<String, Account> bankDatabase = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Seed initial sample account data for quick testing
        bankDatabase.put("1001", new Account("1001", "Alice Smith", 1200.00));
        bankDatabase.put("1002", new Account("1002", "Bob Jones", 800.00));

        boolean running = true;
        while (running) {
            System.out.println("\n=================================");
            System.out.println("   BANKING INFORMATION SYSTEM    ");
            System.out.println("=================================");
            System.out.println("1. Create New Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Funds");
            System.out.println("5. View Account Statement");
            System.out.println("6. Exit");
            System.out.print("Select an option (1-6): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": createAccount(); break;
                case "2": handleDeposit(); break;
                case "3": handleWithdrawal(); break;
                case "4": handleTransfer(); break;
                case "5": handleStatement(); break;
                case "6":
                    System.out.println("Thank you for using the Banking System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println(" Invalid option. Try again.");
            }
        }
    }


    private static void createAccount() {
        System.out.print("Enter Account Number: ");
        String accNo = scanner.nextLine().trim();
        if (bankDatabase.containsKey(accNo)) {
            System.out.println(" Error: Account number already exists!");
            return;
        }
        System.out.print("Enter Account Holder Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Initial Deposit: ");
        try {
            double deposit = Double.parseDouble(scanner.nextLine().trim());
            bankDatabase.put(accNo, new Account(accNo, name, deposit));
            System.out.println(" Account created successfully!");
        } catch (NumberFormatException e) {
            System.out.println(" Error: Invalid number format.");
        }
    }

    private static void handleDeposit() {
        Account acc = findAccount();
        if (acc == null) return;
        System.out.print("Enter Deposit Amount: ");
        try {
            double amt = Double.parseDouble(scanner.nextLine().trim());
            acc.deposit(amt);
        } catch (NumberFormatException e) {
            System.out.println(" Error: Invalid numeric amount.");
        }
    }

    private static void handleWithdrawal() {
        Account acc = findAccount();
        if (acc == null) return;
        System.out.print("Enter Withdrawal Amount: ");
        try {
            double amt = Double.parseDouble(scanner.nextLine().trim());
            acc.withdraw(amt);
        } catch (InsufficientBalanceException e) {
            System.out.println(" Failed: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println(" Error: Invalid numeric amount.");
        }
    }

    private static void handleTransfer() {
        System.out.print("[Source] ");
        Account source = findAccount();
        if (source == null) return;

        System.out.print("[Target] ");
        Account target = findAccount();
        if (target == null) return;

        if (source.getAccountNumber().equals(target.getAccountNumber())) {
            System.out.println(" Error: Cannot transfer to the same account!");
            return;
        }

        System.out.print("Enter Transfer Amount: ");
        try {
            double amt = Double.parseDouble(scanner.nextLine().trim());
            source.transfer(target, amt);
        } catch (InsufficientBalanceException e) {
            System.out.println(" Failed: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println(" Error: Invalid numeric amount.");
        }
    }

    private static void handleStatement() {
        Account acc = findAccount();
        if (acc != null) {
            acc.printStatement();
        }
    }

    private static Account findAccount() {
        System.out.print("Enter Account Number: ");
        String accNo = scanner.nextLine().trim();
        Account acc = bankDatabase.get(accNo);
        if (acc == null) {
            System.out.println(" Error: Account not found!");
        }
        return acc;
    }
}