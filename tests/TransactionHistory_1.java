package com.ga.cmdbank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Record, read or search through transaction history.
 */
public class TransactionHistory {
    int userId;
    int accountId;
    LocalDateTime dateTime;
    String transactionType;
    double transactionAmount;
    int transferToAccountId; // Optional field: only for transfer from other account transactions
    boolean isOwnAccountTransfer; // Optional field: only for transfer transactions
    final Path filePath = Paths.get("data/transaction_history.txt");

    public TransactionHistory(int userId, int accountId, String transactionType, double transactionAmount) {
        this.userId = userId;
        this.accountId = accountId;
        this.dateTime = LocalDateTime.now();
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
    }

    public TransactionHistory(int userId, int accountId, String transactionType, double transactionAmount, int transferToAccountId, boolean isOwnAccountTransfer) {
        this.userId = userId;
        this.accountId = accountId;
        this.dateTime = LocalDateTime.now();
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transferToAccountId = transferToAccountId;
        this.isOwnAccountTransfer = isOwnAccountTransfer;
    }

    public TransactionHistory() {}

    /**
     * Save a transaction into transaction_history.txt file.
     * Transaction row structure: userId;bankAccountId;datetime;transactionType;transactionAmount;receivedFromAccountId
     * receivedFromAccountId element is optional, it is 0 if not set.
     */
    void recordTransaction() {
        String elementBreak = ";";
        String record = userId + elementBreak
                + accountId + elementBreak
                + dateTime + elementBreak
                + transactionType + elementBreak
                + transactionAmount + elementBreak
                + transferToAccountId + elementBreak
                + isOwnAccountTransfer;

        try {
            Files.writeString(filePath, record + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());

        }
    }

    /**
     * Get the entire transaction history on the system as a List of strings, each string a row.
     * @return List Data rows as strings, each a transaction, element break with ; symbol.
     * @throws IOException Files IO exception handling.
     */
    List<String> getTransactionHistory() throws IOException {

        return Files.readAllLines(filePath);
    }

    /**
     * Get the entire transaction history of a single account of the chosen transaction type.
     * @param accountId int Bank account ID
     * @param transactionType Type of transaction to retrieve: deposit, withdraw, transfer, all (for entire history including all the transaction types).
     * @return List Data rows as strings, each a transaction, each a transaction, element break with ; symbol.
     */
    List<String> getAccountTransactionHistory(int accountId, String transactionType) throws IOException {
        BankAccount bankAccount = new BankAccount();
        boolean accountExists = bankAccount.exists(accountId);

        if (!accountExists) throw new IOException("No bank account with account ID " + accountId + " was found.");

        List<String> transactionHistory = getTransactionHistory();
        HashMap<Integer, String> accountTransactionHistory = new HashMap<>();
        int index = 0;

        for (String transaction : transactionHistory) {
            String[] transactionData = transaction.split(";");
            int transactionAccountId = Integer.parseInt(transactionData[1]);

            if (transactionAccountId != accountId) continue; // Skip row if it doesn't belong to this account

            String storedTransactionType = transactionData[3];

            switch (transactionType.strip().toLowerCase()) {
                case "deposit":
                    // add deposit transactions belonging to this account
                    if (!String.valueOf(storedTransactionType).equals("deposit")) continue;

                    accountTransactionHistory.put(index, transaction);
                    index++;

                    break;
                case "withdraw":
                    // add withdraw transactions belonging to this account
                    if (!String.valueOf(storedTransactionType).equals("withdraw")) continue;

                    accountTransactionHistory.put(index, transaction);
                    index++;
                    break;
                case "transfer":
                    // add transfer transactions belonging to this account
                    if (!String.valueOf(storedTransactionType).equals("transfer")) continue;

                    accountTransactionHistory.put(index, transaction);
                    index++;
                    break;
                case "all":
                    // add all transactions belonging to this account
                    accountTransactionHistory.put(index, transaction);
                    index++;
                    break;

                default:
                    throw new IOException("Please choose transaction type of deposit, withdraw, transfer, or all only.");
            }
        }
        
        return new ArrayList<>(accountTransactionHistory.values());
    }

    /**
     * Get list of transactions by type that happened on a specific date.
     * @param accountId int Bank account ID
     * @param transactionType Type of transaction to retrieve: deposit, withdraw, transfer, all (for including all transaction types).
     * @param date The specific calendar date of the transactions.
     * @return List Data rows as strings, each a transaction, each a transaction, element break with ; symbol.
     * @throws IOException IO Exception handling
     */
    List<String> getAccountTransactionHistoryByDate(int accountId, String transactionType, LocalDate date) throws IOException {
        List<String> accountTransactionHistory = getAccountTransactionHistory(accountId, transactionType);
        HashMap<Integer, String> transactions = new HashMap<>();
        int index = 0;

        for (String transaction : accountTransactionHistory) {
            String[] transactionData = transaction.split(";");
            String transactionDate = transactionData[2];
            LocalDateTime localDateTime = LocalDateTime.parse(transactionDate);
            LocalDate localDate = localDateTime.toLocalDate();

            if (!Objects.equals(localDate, date)) continue;

            transactions.put(index, transaction);
            index++;
        }

        return new ArrayList<>(transactions.values());
    }

    /**
     * Get total of transaction amounts conducted on a specific day by type of transaction. For 1 account.
     * @param accountId int Bank account ID
     * @param transactionType Type of transaction to retrieve: deposit, withdraw, transfer, all (for including all transaction types).
     * @param date The specific calendar date of the transactions.
     * @return double Sum of transaction amounts
     * @throws IOException Input exception handling
     */
    double sumOfTransactionAmountOnDateByType(int accountId, String transactionType, LocalDate date) throws IOException {
        List<String> transactions = getAccountTransactionHistoryByDate(accountId, transactionType, date);
        double sum = 0.0;


        for (String transaction : transactions) {
            String[] transactionData = transaction.split(";");

            double transactionAmount = Double.parseDouble(transactionData[4]);

            sum += transactionAmount;
        }

        return sum;
    }

    /**
     * Get total of transaction amounts conducted on a specific day by type of transaction. For 1 account.
     * @param accountId int Bank account ID
     * @param transactionType Type of transaction to retrieve: deposit, withdraw, transfer, all (for including all transaction types).
     * @param date The specific calendar date of the transactions.
     * @param isOwnAccountTransfer Is transfer to own account or not.
     * @return double Sum of transaction amounts
     * @throws IOException Input exception handling
     */
    double sumOfTransactionAmountOnDateByType(int accountId, String transactionType, LocalDate date, boolean isOwnAccountTransfer) throws IOException {
        List<String> transactions = getAccountTransactionHistoryByDate(accountId, transactionType, date);
        double sum = 0.0;


        for (String transaction : transactions) {
            String[] transactionData = transaction.split(";");
            boolean isOwnTransactionTransfer = Boolean.parseBoolean(transactionData[6]);

            if (isOwnTransactionTransfer != isOwnAccountTransfer) continue; // Skip account rows if looking for other type transfers.

            double transactionAmount = Double.parseDouble(transactionData[4]);

            sum += transactionAmount;
        }

        return sum;
    }
}
