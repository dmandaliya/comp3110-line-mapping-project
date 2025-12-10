package com.ga.cmdbank;

import java.io.IOException;
import java.nio.file.Files;
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

                // TODO: Add a transaction in a transaction history file
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

            // Check deposit amount does not exceed their card's limit
            switch (account.cardType) {
                case "DebitMastercard":
                    DebitMastercard debitMastercard = new DebitMastercard(account.debitCardId);
                    if (amount > debitMastercard.depositLimitDaily)
                        throw new IOException("You cannot deposit more than your card's daily limit of $" + debitMastercard.depositLimitDaily);
                    break;

                case "DebitMastercardTitanium":
                    DebitMastercardTitanium debitMastercardTitanium = new DebitMastercardTitanium(account.debitCardId);
                    if (amount > debitMastercardTitanium.depositLimitDaily)
                        throw new IOException("You cannot deposit more than your card's daily limit of $" + debitMastercardTitanium.depositLimitDaily);
                    break;

                case "DebitMastercardPlatinum":
                    DebitMastercardPlatinum debitMastercardPlatinum = new DebitMastercardPlatinum(account.debitCardId);
                    if (amount > debitMastercardPlatinum.depositLimitDaily)
                        throw new IOException("You cannot deposit more than your card's daily limit of $" + debitMastercardPlatinum.depositLimitDaily);
                    break;
            }

            // TODO: Add transaction in transaction history
            // TODO: Deposit amount limit should add up all transactions done on the same day for this account

            if (deposit(account, amount)) {
                System.out.println("Amount of $" + amount + " successfully deposited.");
                System.out.println(" ");
                System.out.println("New Account Balance: $" + (account.balance + amount));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

                // TODO: Add a transaction in a transaction history file
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

            // Check withdraw amount does not exceed their card's limit
            switch (account.cardType) {
                case "DebitMastercard":
                    DebitMastercard debitMastercard = new DebitMastercard(account.debitCardId);
                    if (amount > debitMastercard.withdrawLimitDaily)
                        throw new IOException("You cannot withdraw more than your card's daily limit of $" + debitMastercard.withdrawLimitDaily);
                    break;

                case "DebitMastercardTitanium":
                    DebitMastercardTitanium debitMastercardTitanium = new DebitMastercardTitanium(account.debitCardId);
                    if (amount > debitMastercardTitanium.withdrawLimitDaily)
                        throw new IOException("You cannot withdraw more than your card's daily limit of $" + debitMastercardTitanium.withdrawLimitDaily);
                    break;

                case "DebitMastercardPlatinum":
                    DebitMastercardPlatinum debitMastercardPlatinum = new DebitMastercardPlatinum(account.debitCardId);
                    if (amount > debitMastercardPlatinum.withdrawLimitDaily)
                        throw new IOException("You cannot withdraw more than your card's daily limit of $" + debitMastercardPlatinum.withdrawLimitDaily);
                    break;
            }

            // Check withdraw amount does not exceed their account's balance
            // TODO: Change it into overdraw warning and support with penalty for overdraft system
            // TODO: Withdraw amount limit should add up all transactions done on the same day for this account
            if (account.balance < amount) throw new IOException("Your account balance is not enough to withdraw $" + amount);

            // TODO: Add transaction in transaction history

            if (withdraw(account, amount)) {
                System.out.println("Amount of $" + amount + " successfully withdrawn.");
                System.out.println(" ");
                System.out.println("New Account Balance: $" + (account.balance - amount));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

                // TODO: Add a transaction in a transaction history file
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

                // TODO: Add a transaction in a transaction history file
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
            // TODO: Transfer amount limit should add up all transactions done on the same day for this account
            if (account.balance < amount) throw new IOException("Your account balance is not enough to transfer $" + amount);

            System.out.print("Transfer to Account ID: ");
            int transferAccountId = Integer.parseInt(inputScanner.nextLine().strip());
            BankAccount transferAccount = getAccount(transferAccountId);

            // Check user didn't input same account
            if (account.bankAccountID == transferAccount.bankAccountID) throw new IOException("The account to transfer from must be different than the account to transfer to.");

            // Check if transfer account belongs to the user or someone else
            boolean isOwnAccount = account.userCPR == transferAccount.userCPR;

            if (isOwnAccount) {
                // Check transfer amount does not exceed their card's limit
                switch (account.cardType) {
                    case "DebitMastercard":
                        DebitMastercard debitMastercard = new DebitMastercard(account.debitCardId);
                        if (amount > debitMastercard.transferLimitOwnAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercard.transferLimitOwnAccountDaily);
                        break;

                    case "DebitMastercardTitanium":
                        DebitMastercardTitanium debitMastercardTitanium = new DebitMastercardTitanium(account.debitCardId);
                        if (amount > debitMastercardTitanium.transferLimitOwnAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercardTitanium.transferLimitOwnAccountDaily);
                        break;

                    case "DebitMastercardPlatinum":
                        DebitMastercardPlatinum debitMastercardPlatinum = new DebitMastercardPlatinum(account.debitCardId);
                        if (amount > debitMastercardPlatinum.transferLimitOwnAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercardPlatinum.transferLimitOwnAccountDaily);
                        break;
                }
            } else {
                // Check transfer amount does not exceed their card's limit
                switch (account.cardType) {
                    case "DebitMastercard":
                        DebitMastercard debitMastercard = new DebitMastercard(account.debitCardId);
                        if (amount > debitMastercard.transferLimitOtherAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercard.transferLimitOtherAccountDaily);
                        break;

                    case "DebitMastercardTitanium":
                        DebitMastercardTitanium debitMastercardTitanium = new DebitMastercardTitanium(account.debitCardId);
                        if (amount > debitMastercardTitanium.transferLimitOtherAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercardTitanium.transferLimitOtherAccountDaily);
                        break;

                    case "DebitMastercardPlatinum":
                        DebitMastercardPlatinum debitMastercardPlatinum = new DebitMastercardPlatinum(account.debitCardId);
                        if (amount > debitMastercardPlatinum.transferLimitOtherAccountDaily)
                            throw new IOException("You cannot transfer more than your card's daily limit of $" + debitMastercardPlatinum.transferLimitOtherAccountDaily);
                        break;
                }
            }

            // TODO: Add transaction in transaction history

            if (transfer(account, transferAccount, amount)) {
                System.out.println("Amount of $" + amount + " successfully transferred.");
                System.out.println(" ");
                System.out.println("New Account Balance: $" + (account.balance - amount));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            displayTransfer(inputScanner, user);
        }
    }
}
