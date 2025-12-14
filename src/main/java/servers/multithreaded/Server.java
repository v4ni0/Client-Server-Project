package servers.multithreaded;

import mergesort.MergeSort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int SERVER_PORT = 10_000;
    private static final int NUMBER_OF_ARGS = 3;
    private static final int MAX_THREADS = 5;
    private ExecutorService executor;

    public Server() {
        this.executor = Executors.newFixedThreadPool(MAX_THREADS);
    }

    private static Integer[] parseArray(String str) {
        try {
            if (str.startsWith("[")) {
                str = str.substring(1);
            }
            if (str.endsWith("]")) {
                str = str.substring(0, str.length() - 1);
            }
            if (str.isEmpty()) {
                return new Integer[0];
            }
            String[] nums = str.split(",");
            Integer[] arr = new Integer[nums.length];
            for (int i = 0; i < nums.length; i++) {
                arr[i] = Integer.parseInt(nums[i].strip());
            }
            return arr;
        } catch (Exception e) {
            throw new IllegalArgumentException("error while parsing the array", e);
        }
    }

    private static int parseMinSize(String str) {
        try {
            return Integer.parseInt(str.strip());
        } catch (Exception e) {
            throw new IllegalArgumentException("error while parsing the min size for parallel sort", e);
        }
    }

    private static int parseNumberOfThreads(String str) {
        try {
            return Integer.parseInt(str.strip());
        } catch (Exception e) {
            throw new IllegalArgumentException("error while parsing the number of threads", e);
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] parts = inputLine.split(";", NUMBER_OF_ARGS);
                System.out.println("message received from client: " + inputLine);
                if (parts.length != NUMBER_OF_ARGS) {
                    out.println("expected format: number_of_threads;min size for parallel sort;array");
                    continue;
                }
                int threads = parseNumberOfThreads(parts[0]);
                int minSizeForParallelSort = parseMinSize(parts[1]);
                Integer[] arr = parseArray(parts[2]);
                MergeSort<Integer> mergeSort = new MergeSort<Integer>(arr, threads, minSizeForParallelSort);
                mergeSort.run();
                out.println(Arrays.toString(arr));
            }
        } catch (IOException e) {
            throw new RuntimeException("Communication error with client", e);
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is listening");
            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(() -> handleClient(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException("Connection problem", e);
        } finally {
            executor.shutdown();
        }
    }

    public static void main() {
        Server server = new Server();
        server.start();

    }
}
