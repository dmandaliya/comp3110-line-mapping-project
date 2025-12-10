package com.ga.cmdbank;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BankAccountTransaction extends BankAccount {
    /**
     * Deposit money amount into specified bank account. Returns true if successful, otherwise false.
     * @param bankAccount Object bank Account to deposit into
     * @param amount double Amount of money in USD to deposit
     * @return boolean
     * @throws IOException Exception handling
     */
    boolean deposit(BankAccount bankAccount, double amount) throws IOException {
        List<String> accountsData = Files.readAllLines(filepath);

        bankAccount.balance += amount;

        for (int _i = 0; _i < accountsData.size(); _i++) {
            String row = accountsData.get(_i);

            if (row.startsWith(String.valueOf(bankAccount.bankAccountID))) { // Update account record balance
                String[] rowData = row.split(";");
                double sum = Double.parseDouble(rowData[5]) + amount;
                String newRowData = rowData[0] + ";"
                        + rowData[1] + ";"
                        + rowData[2] + ";"
                        + rowData[3] + ";"
                        + rowData[4] + ";"
                        + sum;

                accountsData.set(_i, newRowData);
                Files.write(filepath, accountsData);

                return true;
            }
        }

        return false;
    }

    /**
     * Display deposit into account balance menu
     * @param inputScanner Scanner
     * @param user Object
     * @throws IOException Input exception handling
     */
    void displayDeposit(Scanner inputScanner, UserRead user) throws IOException {
        System.out.println("DEPOSIT INTO ACCOUNT:");
        System.out.print("Account ID: ");
        int accountId = Integer.parseInt(inputScanner.nextLine().strip());
        BankAccount account = getAccount(accountId);

        try {
            // Check user has authorization to deposit into this account
            if (String.valueOf(user.userRole).equals("customer")) {
                if (account.userCPR != user.cpr) throw new IOException("You are not authorized to deposit into another person's account.");
            }

            System.out.println(" ");

            System.out.println("Current Balance: $" + account.balance);

            System.out.print("Deposit Amount (USD): ");
            double amount = Double.parseDouble(inputScanner.nextLine().strip());

            TransactionHistory transactionHistory = new TransactionHistory();
            double transactionAmountToday = transactionHistory.sumOfTransactionAmountOnDateByType(accountId, "deposit", LocalDateTime.now().toLocalDate());
            double theoreticalTransactionAmount = transactionAmountToday + amount;

            // Check deposit amount does not exceed their card's limit
            switch (account.cardType) {
                case "DebitMastercard":
                    DebitMastercard debitMastercard = new DebitMastercard(account.debitCardId);
                    if (theoreticalTransactionAmount > debitMastercard.depositLimitDaily)
                        throw new IOException("You cannot deposit more than your card's daily limit of $" + debitMastercard.depositLimitDaily + ". Current total withdrawals for today is $"+ transactionAmountToday);
                    break;

                case "DebitMastercardTitanium":
                    DebitMastercardTitanium debitMastercardTitanium = new DebitMastercardTitanium(account.debitCardId);
                    if (theoreticalTransactionAmount > debitMastercardTitanium.depositLimitDaily)
                        throw new IOException("You cannot deposit more than your card's daily limit of $" + debitMastercardTitanium.depositLimitDaily + ". Current total withdrawals for today is $"+ transactionAmountToday);
                    break;

                case "DebitMastercardPlatinum":
                    DebitMastercardPlatinum debitMastercardPlatinum = new DebitMastercardPlatinum(account.debitCardId);
                    if (theoreticalTransactionAmount > debitMastercardPlatinum.depositLimitDaily)
                        throw new IOException("You cannot deposit more than your card's daily limit of $" + debitMastercardPlatinum.depositLimitDaily + ". Current total withdrawals for today is $"+ transactionAmountToday);
                    break;
            }

            if (deposit(account, amount)) {
                // Add transaction in a transaction history file
                TransactionHistory record = new TransactionHistory(user.cpr, account.bankAccountID, "deposit", amount);
                record.recordTransaction();

                System.out.println("Amount of $" + amount + " successfully deposited.");
                System.out.println(" ");
                System.out.println("New Account Balance: $" + account.balance);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            displayDeposit(inputScanner, user);
        }
    }

    /**
     * Withdraw money from specified bank account based on amount. Returns true if successful, otherwise false.
     * @param bankAccount Object bank Account to withdraw from
     * @param amount double Amount of money in USD to withdraw
     * @return boolean
     * @throws IOException Exception handling
     */
    boolean withdraw(BankAccount bankAccount, double amount) throws IOException {
        List<String> accountsData = Files.readAllLines(filepath);

        // Check amount does not exceed balance
        if (bankAccount.balance < amount) throw new IOException("Withdraw amount cannot exceed balance.");
        bankAccount.balance -= amount;

        for (int _i = 0; _i < accountsData.size(); _i++) {
            String row = accountsData.get(_i);

            if (row.startsWith(String.valueOf(bankAccount.bankAccountID))) { // Update account record balance
                String[] rowData = row.split(";");
                double subtract = Double.parseDouble(rowData[5]) - amount;
                String newRowData = rowData[0] + ";"
                        + rowData[1] + ";"
                        + rowData[2] + ";"
                        + rowData[3] + ";"
                        + rowData[4] + ";"
                        + subtract;

                accountsData.set(_i, newRowData);
                Files.write(filepath, accountsData);

                return true;
            }
        }

        return false;
    }

    /**
     * Display withdraw from account balance menu
     * @param inputScanner Scanner
     * @param user Object
     * @throws IOException Input exception handling
     */
    void displayWithdraw(Scanner inputScanner, UserRead user) throws IOException {
        System.out.println("WITHDRAW FROM ACCOUNT:");
        System.out.print("Account ID: ");
        int accountId = Integer.parseInt(inputScanner.nextLine().strip());

        try {
            BankAccount account = getAccount(accountId);

            // Check user has authorization to withdraw into this account
            if (String.valueOf(user.userRole).equals("customer")) {
                if (account.userCPR != user.cpr) throw new IOException("You are not authorized to withdraw from another person's account.");
            }

            System.out.println(" ");

            System.out.println("Current Balance: $" + account.balance);

            System.out.print("Withdraw Amount (USD): ");
            double amount = Double.parseDouble(inputScanner.nextLine().strip());

            TransactionHistory transactionHistory = new TransactionHistory();
            double transactionAmountToday = transactionHistory.sumOfTransactionAmountOnDateByType(accountId, "withdraw", LocalDateTime.now().toLocalDate());
            double theoreticalTransactionAmount = transactionAmountToday + amount;

            // Check withdraw amount does not exceed their card's limit
            switch (account.cardType) {
                case "DebitMastercard":
                    DebitMastercard debitMastercard = new DebitMastercard(account.debitCardId);
                    if (theoreticalTransactionAmount > debitMastercard.withdrawLimitDaily)
                        throw new IOException("You cannot withdraw more than your card's daily limit of $" + debitMastercard.withdrawLimitDaily + ". Current total withdrawals for today is $"+ transactionAmountToday);
                    break;

                case "DebitMastercardTitanium":
                    DebitMastercardTitanium debitMastercardTitanium = new DebitMastercardTitanium(account.debitCardId);
                    if (theoreticalTransactionAmount > debitMastercardTitanium.withdrawLimitDaily)
                        throw new IOException("You cannot withdraw more than your card's daily limit of $" + debitMastercardTitanium.withdrawLimitDaily + ". Current total withdrawals for today is $"+ transactionAmountToday);
                    break;

                case "DebitMastercardPlatinum":
                    DebitMastercardPlatinum debitMastercardPlatinum = new DebitMastercardPlatinum(account.debitCardId);
                    if (theoreticalTransactionAmount > debitMastercardPlatinum.withdrawLimitDaily)
                        throw new IOException("You cannot withdraw more than your card's daily limit of $" + debitMastercardPlatinum.withdrawLimitDaily + ". Current total withdrawals for today is $"+ transactionAmountToday);
                    break;
            }

            // Check withdraw amount does not exceed their account's balance
            // TODO: Change it into overdraw warning and support with penalty for overdraft system
            if (account.balance < amount) throw new IOException("Your account balance is not enough to withdraw $" + amount + ". Current total withdrawals for today is $" + transactionAmountToday);

            if (withdraw(account, amount)) {
                // Add transaction in a transaction history file
                TransactionHistory record = new TransactionHistory(user.cpr, account.bankAccountID, "withdraw", amount);
                record.recordTransaction();

                System.out.println("Amount of $" + amount + " successfully withdrawn.");
                System.out.println(" ");
                System.out.println("New Account Balance: $" + account.balance);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            displayWithdraw(inputScanner, user);
        }
    }

    /**
     * Transfer money from specified bank account based on amount. Can transfer between own accounts or to other registered accounts.
     * @param withdrawBankAccount Object bank Account to withdraw from.
     * @param depositBankAccount Object Bank account to transfer to.
     * @param amount double Amount of money in USD to withdraw
     * @return boolean Returns true if successful transfer, otherwise false.
     * @throws IOException Exception handling
     */
    boolean transfer(BankAccount withdrawBankAccount, BankAccount depositBankAccount, double amount) throws IOException {
        List<String> accountsData = Files.readAllLines(filepath);

        // Check amount does not exceed balance
        if (withdrawBankAccount.balance < amount) throw new IOException("Transfer amount cannot exceed balance.");

        withdrawBankAccount.balance -= amount;
        depositBankAccount.balance += amount;

        Boolean[] accountsUpdated = {false, false};

        for (int _i = 0; _i < accountsData.size(); _i++) {
            String row = accountsData.get(_i);

            if (row.startsWith(String.valueOf(withdrawBankAccount.bankAccountID))) { // Update account record balance
                String[] rowData = row.split(";");
                double subtract = Double.parseDouble(rowData[5]) - amount;
                String newRowData = rowData[0] + ";"
                        + rowData[1] + ";"
                        + rowData[2] + ";"
                        + rowData[3] + ";"
                        + rowData[4] + ";"
                        + subtract;

                accountsData.set(_i, newRowData);
                Files.write(filepath, accountsData);

                accountsUpdated[0] = true;
            } else if (row.startsWith(String.valueOf(depositBankAccount.bankAccountID))) {
                String[] rowData = row.split(";");
                double sum = Double.parseDouble(rowData[5]) + amount;
                String newRowData = rowData[0] + ";"
                        + rowData[1] + ";"
                        + rowData[2] + ";"
                        + rowData[3] + ";"
                        + rowData[4] + ";"
                        + sum;

                accountsData.set(_i, newRowData);
                Files.write(filepath, accountsData);

                accountsUpdated[1] = true;
            }
        }

        return accountsUpdated[0] && accountsUpdated[1];
    }

    /**
     * Display transfer from account balance menu
     * @param inputScanner Scanner
     * @param user Object
     * @throws IOException Input exception handling
     */
    void displayTransfer(Scanner inputScanner, UserRead user) throws IOException {
        System.out.println("TRANSFER FROM ACCOUNT:");
        System.out.print("Account ID: ");
        int accountId = Integer.parseInt(inputScanner.nextLine().strip());

        try {
            BankAccount account = getAccount(accountId);

            // Check user has authorization to transfer from this account
            if (String.valueOf(user.userRole).equals("customer")) {
                if (account.userCPR != user.cpr) throw new IOException("You are not authorized to transfer from another person's account.");
            }

            System.out.println(" ");

            System.out.println("Current Balance: $" + account.balance);

            System.out.print("Transfer Amount (USD): ");
            double amount = Double.parseDouble(inputScanner.nextLine().strip());
            System.out.println(" ");

            // Check transfer amount does not exceed their account's balance
            if (account.balance < amount) throw new IOException("Your account balance is not enough to transfer $" + amount);

            System.out.print("Transfer to Account ID: ");
            int transferAccountId = Integer.parseInt(inputScanner.nextLine().strip());
            BankAccount transferAccount = getAccount(transferAccountId);

            // Check user didn't input same account
            if (account.bankAccountID == transferAccount.bankAccountID) throw new IOException("The account to transfer from must be different than the account to transfer to.");

            // Check if transfer account belongs to the user or someone else
            boolean isOwnAccount = account.userCPR == transferAccount.userCPR;
            TransactionHistory transactionHistory = new TransactionHistory();
            double transactionAmountToday = transactionHistory.sumOfTransactionAmountOnDateByType(accountId, "transfer", LocalDateTime.now().toLocalDate(), isOwnAccount);
            double theoreticalTransactionAmount = transactionAmountToday + amount;

            if (isOwnAccount) {
                // Check transfer amount does not exceed their card's limit
                switch (account.cardType) {
                    case "DebitMastercard":
                        DebitMastercard debitMastercard = new DebitMastercard(account.debitCardId);
                        if (theoreticalTransactionAmount > debitMastercard.transferLimitOwnAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercard.transferLimitOwnAccountDaily + ". Your current total transfers for today is $" + transactionAmountToday);
                        break;

                    case "DebitMastercardTitanium":
                        DebitMastercardTitanium debitMastercardTitanium = new DebitMastercardTitanium(account.debitCardId);
                        if (theoreticalTransactionAmount > debitMastercardTitanium.transferLimitOwnAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercardTitanium.transferLimitOwnAccountDaily + ". Your current total transfers for today is $" + transactionAmountToday);
                        break;

                    case "DebitMastercardPlatinum":
                        DebitMastercardPlatinum debitMastercardPlatinum = new DebitMastercardPlatinum(account.debitCardId);
                        if (theoreticalTransactionAmount > debitMastercardPlatinum.transferLimitOwnAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercardPlatinum.transferLimitOwnAccountDaily + ". Your current total transfers for today is $" + transactionAmountToday);
                        break;
                }
            } else {
                // Check transfer amount does not exceed their card's limit
                switch (account.cardType) {
                    case "DebitMastercard":
                        DebitMastercard debitMastercard = new DebitMastercard(account.debitCardId);
                        if (theoreticalTransactionAmount > debitMastercard.transferLimitOtherAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercard.transferLimitOtherAccountDaily + ". Your current total transfers for today is $" + transactionAmountToday);
                        break;

                    case "DebitMastercardTitanium":
                        DebitMastercardTitanium debitMastercardTitanium = new DebitMastercardTitanium(account.debitCardId);
                        if (theoreticalTransactionAmount > debitMastercardTitanium.transferLimitOtherAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercardTitanium.transferLimitOtherAccountDaily + ". Your current total transfers for today is $" + transactionAmountToday);
                        break;

                    case "DebitMastercardPlatinum":
                        DebitMastercardPlatinum debitMastercardPlatinum = new DebitMastercardPlatinum(account.debitCardId);
                        if (theoreticalTransactionAmount > debitMastercardPlatinum.transferLimitOtherAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercardPlatinum.transferLimitOtherAccountDaily + ". Your current total transfers for today is $" + transactionAmountToday);
                        break;
                }
            }

            if (transfer(account, transferAccount, amount)) {
                // Add transaction in a transaction history file
                TransactionHistory record = new TransactionHistory(user.cpr, account.bankAccountID, "transfer", amount, transferAccount.bankAccountID, isOwnAccount);
                record.recordTransaction();

                System.out.println("Amount of $" + amount + " successfully transferred.");
                System.out.println(" ");
                System.out.println("New Account Balance: $" + account.balance);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage() + " TRACE: " + Arrays.toString(e.getStackTrace()));
            displayTransfer(inputScanner, user);
        }
    }
}
