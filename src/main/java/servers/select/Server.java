package servers.select;

import mergesort.MergeSort;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static final int SERVER_PORT = 10_000;
    private static final int BUFFER_SIZE = 512;
    private static final int NUMBER_OF_ARGS = 3;
    private ByteBuffer buffer;
    private Selector selector;

    public Server() {
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
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

    private void configureServer(ServerSocketChannel serverSocketChannel) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(SERVER_PORT));
        serverSocketChannel.configureBlocking(false);

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server is listening");
    }

    private void registerClient(ServerSocketChannel serverChannel) throws IOException {
        SocketChannel accept = serverChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private String readFromClient(SocketChannel clientChannel) throws IOException {
        buffer.clear();
        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }
        buffer.flip();
        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);
        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void sendToClient(SocketChannel clientChannel, String message) throws IOException {
        String[] parts = message.split(";", NUMBER_OF_ARGS);
        int threads = parseNumberOfThreads(parts[0]);
        int minSizeForParallelSort = parseMinSize(parts[1]);
        Integer[] arr = parseArray(parts[2]);
        MergeSort<Integer> mergeSort = new MergeSort<Integer>(arr, threads, minSizeForParallelSort);
        mergeSort.run();
        String reply = Arrays.toString(arr) + "\n";
        buffer.clear();
        buffer.put(reply.getBytes());
        buffer.flip();
        clientChannel.write(buffer);
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            configureServer(serverSocketChannel);
            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        String message = readFromClient(clientChannel);
                        if (message == null) {
                            continue;
                        }
                        sendToClient(clientChannel, message);

                    } else if (key.isAcceptable()) {
                        registerClient((ServerSocketChannel) key.channel());
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}

