package com.ga.cmdbank;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner inputScanner = new Scanner(System.in); // Universal input scanner
        UserRead userRead = new UserRead();

        userRead.displayLogin(inputScanner);
    }
}
