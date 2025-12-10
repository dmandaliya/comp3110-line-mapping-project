package com.ga.cmdbank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Scanner;

/**
 * Create new users. Privilege allowed to banker accounts only.
 */
public class UserCreate extends User implements IPassword {
    /**
     * Default constructor to access class methods.
     */
    public UserCreate() {}

    /**
     * New user constructor.
     * @param cprInput String user's CPR number. Unique user identifier and login ID.
     * @param firstName String user's first name.
     * @param lastName String user's last name.
     * @param userRole String user's role, 1 of 2 options: [banker, customer].
     * @param hashedPassword String User's hashed password
     * @param passwordSalt String User's password's unique salt value.
     */
    public UserCreate(String cprInput, String firstName, String lastName, String userRole, String hashedPassword, byte[] passwordSalt) {
        super(cprInput, firstName, lastName, userRole, hashedPassword, passwordSalt);
    }

    /**
     * Display the Create New User Command Line prompt.
     */
    void display(Scanner inputScanner) {
        try {

            System.out.println("CREATE NEW USER ACCOUNT");
            System.out.println(" ");
            System.out.print("CPR Number: ");
            String cpr = inputScanner.nextLine();

            System.out.println(" ");
            System.out.print("First Name: ");
            String firstName = inputScanner.nextLine();
            System.out.println(" ");

            System.out.print("Last Name: ");
            String lastName = inputScanner.nextLine();
            System.out.println(" ");

            System.out.print("User Type ([B] banker/ [C] customer): ");
            userRole = inputScanner.nextLine();
            switch (userRole.toUpperCase()) {
                case "B":
                    userRole = "banker";
                    break;
                case "C":
                default:
                    userRole = "customer";
                    break;
            }
            System.out.println(" ");

            System.out.println("Creating new user account...");
            if (save(cpr, firstName, lastName, userRole)) {
                System.out.println("New user account successfully created.");
            } else {
                System.err.println("New user account creation failed. Please try again.");
                display(inputScanner);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println(" ");
            display(inputScanner);
        }
    }

    /**
     * Create a new user account and save it to users.txt data file.
     * @param cpr String user's CPR number. Unique user identifier and login ID.
     * @param firstName String user's first name.
     * @param lastName String user's last name.
     * @param userRole String user's role, 1 of 2 options: [banker, customer].
     * @return boolean
     */
    boolean save(String cpr, String firstName, String lastName, String userRole) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] passwordSalt = IPassword.generateSalt();
        String hashedPassword = IPassword.hashPassword(cpr, passwordSalt);
        User user = new UserCreate(cpr, firstName, lastName, userRole, hashedPassword, passwordSalt);
        String valueBreak = ";";
        String userString = user.cpr + valueBreak + user.firstName + valueBreak + user.lastName + valueBreak + user.userRole + valueBreak + user.hashedPassword + valueBreak + IPassword.base64Salt(user.passwordSalt);

        try {
            int cprNumber = convertCPRInput(cpr);
            if (exists(cprNumber)) {
                throw new IOException("User with CPR " + cpr + " already exists.");
            }
        } catch (NumberFormatException e) {
            throw new IOException("Invalid CPR format: " + cpr);
        }

        try {
            Files.writeString(filePath, userString + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());

            return false;
        }
    }

    /**
     * Command line reset password operation.
     * @param inputScanner Scanner System.in scanner
     * @param userRead Object UserRead with full details.
     */
    void displayResetPassword(Scanner inputScanner, UserRead userRead) {
        try {
            if (String.valueOf(userRead.userRole).equals("banker")) { // Banker resetting customer's pw
                System.out.println("RESET USER ACCOUNT PASSWORD:");
                System.out.print("Customer CPR: ");
                int userAccountId = Integer.parseInt(inputScanner.nextLine().strip());
                System.out.println(" ");

                if (userAccountId != userRead.cpr) { // Banker resetting password for a customer
                    String[] customerData = userRead.read(userAccountId);
                    UserRead customer = new UserRead(customerData[0], customerData[1], customerData[2], customerData[3], customerData[4], IPassword.decodeBase64Salt(customerData[5]));

                    if (!resetPassword(customer.cpr, customer.cprInput)) throw new RuntimeException("Password reset failed. Please try again.");
                    System.out.println("Password successfully reset.");
                    System.out.println(" ");

                    userRead.backToMainMenu(inputScanner, userRead);

                } else { // Banker is resetting their own pw
                    System.out.print("Current Password: ");
                    String currentPassword = inputScanner.nextLine().strip();

                    if (!IPassword.verifyPassword(currentPassword, userRead.hashedPassword, userRead.passwordSalt))
                        throw new IOException("Current password does not match password in database");

                    System.out.println(" ");
                    System.out.print("New Password: ");
                    String newPassword = inputScanner.nextLine().strip();

                    if (newPassword.isEmpty()) throw new IOException("Password cannot be empty");
                    if (newPassword.length() < 8) throw new IOException("Password must be at least 8 characters long.");

                    boolean hasLetter = newPassword.matches(".*[A-Za-z].*");
                    boolean hasDigit = newPassword.matches(".*\\d.*");
                    boolean hasSymbol = newPassword.matches(".*[^A-Za-z0-9].*");

                    if (!hasLetter || !hasDigit || !hasSymbol) throw new IOException("Password must contain a mix of letters, numbers and symbols.");

                    System.out.println(" ");
                    System.out.print("Confirm New Password: ");
                    String confirmNewPassword = inputScanner.nextLine().strip();
                    System.out.println(" ");

                    if (!newPassword.equals(confirmNewPassword))
                        throw new IOException("New password does not match confirm new password. Please try again.");

                    if (!resetPassword(userRead.cpr, newPassword)) throw new RuntimeException("Password reset failed. Please try again.");

                    System.out.println("Password successfully reset. Please login again.");

                    userRead.displayLogin(inputScanner);
                }

            } else if (String.valueOf(userRead.userRole).equals("customer")) { // customer is resetting their own pw
                System.out.println("RESET USER ACCOUNT PASSWORD:");
                System.out.print("Current Password: ");
                String currentPassword = inputScanner.nextLine().strip();

                if (!IPassword.verifyPassword(currentPassword, userRead.hashedPassword, userRead.passwordSalt))
                    throw new IOException("Current password does not match password in database");

                System.out.println(" ");
                System.out.print("New Password: ");
                String newPassword = inputScanner.nextLine().strip();

                if (newPassword.isEmpty()) throw new IOException("Password cannot be empty");
                if (newPassword.length() < 8) throw new IOException("Password must be at least 8 characters long.");

                boolean hasLetter = newPassword.matches(".*[A-Za-z].*");
                boolean hasDigit = newPassword.matches(".*\\d.*");
                boolean hasSymbol = newPassword.matches(".*[^A-Za-z0-9].*");

                if (!hasLetter || !hasDigit || !hasSymbol) throw new IOException("Password must contain a mix of letters, numbers and symbols.");

                System.out.println(" ");
                System.out.print("Confirm New Password: ");
                String confirmNewPassword = inputScanner.nextLine().strip();
                System.out.println(" ");

                if (!newPassword.equals(confirmNewPassword))
                    throw new IOException("New password does not match confirm new password. Please try again.");

                if (!resetPassword(userRead.cpr, newPassword)) throw new RuntimeException("Password reset failed. Please try again.");

                System.out.println("Password successfully reset. Please login again.");

                userRead.displayLogin(inputScanner);

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            displayResetPassword(inputScanner, userRead);
        }
    }

    /**
     * Reset user account's old password to the newly specified password.
     * @param userCPR int User Account ID
     * @param newPassword String The new password
     * @return boolean True if successfully reset, otherwise false.
     * @throws IOException Wrong input handling
     * @throws NoSuchAlgorithmException Wrong algorithm handling
     * @throws InvalidKeySpecException Wrong key spec handling
     */
    boolean resetPassword(int userCPR, String newPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        List<String> userAccounts = Files.readAllLines(filePath);

        for (int _i = 0; _i < userAccounts.size(); _i++) {
            String account = userAccounts.get(_i);
            String[] accountData = account.split(";");
            int accountUserId = Integer.parseInt(accountData[0]);

            if (accountUserId == userCPR) {
                // Generate new password salt
                byte[] salt = IPassword.generateSalt();
                String base64Salt = IPassword.base64Salt(salt);
                // Generate new hashed password
                String hashedPassword = IPassword.hashPassword(newPassword, salt);
                // Update user record with new hashed password and salt
                String valueBreak = ";";
                String updatedUser = accountData[0] + valueBreak
                        + accountData[1] + valueBreak
                        + accountData[2] + valueBreak
                        + accountData[3] + valueBreak
                        + hashedPassword + valueBreak
                        + base64Salt + valueBreak;

                userAccounts.set(_i, updatedUser);
                Files.write(filePath, userAccounts);

                return true;
            }
        }

        return false;
    }
}