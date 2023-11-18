package com.bank;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int accountNumber;

    public Client(String name, int accountNumber) {
        this.name = name;
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public int getAccountNumber() {
        return accountNumber;
    }
}

class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private int accountNumber;
    private double balance;
    private List<Transaction> transactions;

    public Account(int accountNumber) {
        this.accountNumber = accountNumber;
        this.balance = 0;
        this.transactions = new ArrayList<>();
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void deposit(double amount) {
        balance += amount;
        transactions.add(new Transaction("Deposit", amount));
    }

    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction("Withdrawal", amount));
        } else {
            System.out.println("Brak wystarczających środków na koncie!");
        }
    }

    public void transfer(Account recipient, double amount) {
        if (amount <= balance) {
            balance -= amount;
            recipient.deposit(amount);
            transactions.add(new Transaction("Przelew do " + recipient.getAccountNumber(), amount));
        } else {
            System.out.println("Brak wystarczających środków na koncie!");
        }
    }
}

class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private double amount;
    private Date date;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.date = new Date();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }
}

class Bank implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Client> clients;
    private List<Account> accounts;

    public Bank() {
        this.clients = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    public void addClient(String name, int accNumber) {
        Account existingAccount = getAccount(accNumber);
        if (existingAccount == null) {
            Client client = new Client(name, accNumber);
            Account account = new Account(accNumber);
            clients.add(client);
            accounts.add(account);
            System.out.println("Konto klienta utworzone!");
            System.out.print("Twój numer konta to: ");
            System.out.print(accNumber);
        } else {
            System.out.println("Konto o takim numerze już istnieje!");
        }
    }

    public Client getClient(int accountNumber) {
        for (Client client : clients) {
            if (client.getAccountNumber() == accountNumber) {
                return client;
            }
        }
        return null;
    }

    public Account getAccount(int accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }
        return null;
    }

    public void displayAccountInfo(int accountNumber) {
        Account account = getAccount(accountNumber);
        if (account != null) {
            System.out.println("Numer konta: " + account.getAccountNumber());
            System.out.println("Imię: " + getClient(accountNumber).getName());
            System.out.println("Stan konta: $" + account.getBalance());
            System.out.println("Transakcje:");
            for (Transaction transaction : account.getTransactions()) {
                System.out.println(transaction.getType() + " - Wartość: $" + transaction.getAmount() +
                        " - Data: " + transaction.getDate());
            }
        } else {
            System.out.println("Konto nie zostało znalezione!");
        }
    }

    public void displayTransactionsInPeriod(int accountNumber, Date startDate, Date endDate) {
        Account account = getAccount(accountNumber);
        if (account != null) {
            System.out.println("Transakcje w podanym okresie:");
            for (Transaction transaction : account.getTransactions()) {
                if (transaction.getDate().after(startDate) && transaction.getDate().before(endDate)) {
                    System.out.println(transaction.getType() + " - Wartość: $" + transaction.getAmount() +
                            " - Data: " + transaction.getDate());
                }
            }
        } else {
            System.out.println("Konto nie zostało znalezione!");
        }
    }
}

public class Main {

    public static void main(String[] args) {
        Bank bank = loadBankData();

        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        while (true) {
            System.out.println("\nProsta aplikacja bankowa:");
            System.out.println("1. Dodaj klienta");
            System.out.println("2. Wpłata");
            System.out.println("3. Wypłata");
            System.out.println("4. Przelew");
            System.out.println("5. Wyświetl informacje o koncie");
            System.out.println("6. Historia konta w danym okresie");
            System.out.println("7. Zapisz i wyjdź");

            System.out.print("Twój wybór: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Wprowadź imię klienta: ");
                    scanner.nextLine();
                    String name = scanner.nextLine();
                    System.out.print("Wprowadź numer konta klienta: ");
                    int accNumber = scanner.nextInt();
                    bank.addClient(name, accNumber);
                    break;
                case 2:
                    performTransaction(scanner, bank, "deposit");
                    break;
                case 3:
                    performTransaction(scanner, bank, "withdraw");
                    break;
                case 4:
                    performTransfer(scanner, bank);
                    break;
                case 5:
                    System.out.print("Podaj numer konta: ");
                    int accountNumber = scanner.nextInt();
                    bank.displayAccountInfo(accountNumber);
                    break;
                case 6:
                    System.out.print("Podaj numer konta: ");
                    int accNum = scanner.nextInt();
                    System.out.print("Podaj datę początkową (yyyy-MM-dd): ");
                    scanner.nextLine();
                    String startDateStr = scanner.nextLine();
                    System.out.print("Podaj datę końcową (yyyy-MM-dd): ");
                    String endDateStr = scanner.nextLine();
                    try {
                        Date startDate = BankUtils.parseDate(startDateStr);
                        Date endDate = BankUtils.parseDate(endDateStr);
                        bank.displayTransactionsInPeriod(accNum, startDate, endDate);
                    } catch (Exception e) {
                        System.out.println("Zły format daty!");
                    }
                    break;
                case 7:
                    saveBankData(bank);
                    System.out.println("Zapisano. Zamykam...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Zły wybór. Wybierz opcję od 1 do 7.");
            }
        }
    }

    private static final String BANK_DATA_FILE = "bank_data.dat";

    private static void performTransfer(Scanner scanner, Bank bank) {
        System.out.print("Podaj numer konta nadawcy: ");
        int senderAccountNumber = scanner.nextInt();
    
        System.out.print("Podaj numer konta odbiorcy: ");
        int recipientAccountNumber = scanner.nextInt();
    
        System.out.print("Ile chcesz przelać?: ");
        double amount = scanner.nextDouble();
    
        Account senderAccount = bank.getAccount(senderAccountNumber);
        Account recipientAccount = bank.getAccount(recipientAccountNumber);
    
        if (senderAccount != null && recipientAccount != null) {
            senderAccount.transfer(recipientAccount, amount);
            System.out.println("Przelew został zlecony!");
        } else {
            System.out.println("Któreś z podanych kont nie zostało znalezionych!");
        }
    }

    private static Bank loadBankData() {
        Bank bank = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BANK_DATA_FILE))) {
            bank = (Bank) ois.readObject();
            System.out.println("Dane wczytane z pliku.");
        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono pliku z danymi banku. Tworzę nowy bank.");
            bank = new Bank();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Wystąpił błąd podczas wczytywania danych z pliku: " + e.getMessage());
        }
        return bank;
    }

    private static void saveBankData(Bank bank) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BANK_DATA_FILE))) {
            oos.writeObject(bank);
            System.out.println("Dane zostały zapisane do pliku");
        } catch (IOException e) {
            System.out.println("Wystąpił błąd podczas zapisywania danych do pliku: " + e.getMessage());
        }
    }

    private static void performTransaction(Scanner scanner, Bank bank, String transactionType) {
        System.out.print("Podaj numer konta: ");
        int accountNumber = scanner.nextInt();
    
        Account account = bank.getAccount(accountNumber);
    
        if (account != null) {
            System.out.print("Podaj wartość do " + transactionType + ": ");
            double amount = scanner.nextDouble();
    
            if ("deposit".equals(transactionType)) {
                account.deposit(amount);
                System.out.println("Wpłata dokonana!");
            } else if ("withdraw".equals(transactionType)) {
                account.withdraw(amount);
                System.out.println("Wypłata dokonana!");
            } else {
                System.out.println("Błędny typ transkacji.");
            }
        } else {
            System.out.println("Nie udało się znaleźć konta!");
        }
    }
}