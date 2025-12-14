package client;

import mergesort.MergeSort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final int SERVER_PORT = 10_000;

    private static Integer[] parseArray(String str) {
        if (str.startsWith("[")) {
            str = str.substring(1);
        }
        if (str.endsWith("]")) {
            str = str.substring(0, str.length() - 1);
        }
        if (str.isEmpty()) {
            return new Integer[0];
        }
        String[] numbers = str.split(",");
        Integer[] arr = new Integer[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            arr[i] = Integer.parseInt(numbers[i].strip());
        }
        return arr;
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            System.out.println("connected to server");
            while (true) {
                System.out.print("Enter threads;minSize;array: ");
                String message = scanner.nextLine();

                if ("exit".equals(message)) {
                    break;
                }
                writer.println(message);
                String reply = reader.readLine();
                Integer[] sortedArray = parseArray(reply);
                System.out.println("The server replied " + reply);
                System.out.println(MergeSort.isSorted(sortedArray));
            }

        } catch (IOException e) {
            throw new RuntimeException("Communication error", e);
        }

    }
}