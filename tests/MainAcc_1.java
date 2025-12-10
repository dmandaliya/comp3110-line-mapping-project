package com.ga.cmdbank;

import javax.security.auth.login.FailedLoginException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FailedLoginException {
        Scanner inputScanner = new Scanner(System.in); // Universal app scanner
        UserRead userRead = new UserRead();

        try {
            userRead.display(inputScanner);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            userRead.display(inputScanner);
        }
    }
}
