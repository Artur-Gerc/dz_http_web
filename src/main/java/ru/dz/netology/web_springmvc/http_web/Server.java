package ru.dz.netology.web_springmvc.http_web;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Server {
    private final int port;
    private final static int THREAD_POOL_SIZE = 64;
    private final ExecutorService threadPoll;

    private ConcurrentHashMap<HttpMethod, ConcurrentHashMap<URI, Handler>> handlers;

    public Server(int port) {
        this.port = port;
        this.threadPoll = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.handlers = new ConcurrentHashMap<>();
    }

    public void addHandler(String addHttpMethod, String addPath, Handler handler) throws URISyntaxException {
        HttpMethod httpMethod = HttpMethod.valueOf(addHttpMethod.toUpperCase());
        URI path = new URI(addPath);
        if (handlers.containsKey(httpMethod)) {
            handlers.get(httpMethod).put(path, handler);
        } else {
            ConcurrentHashMap<URI, Handler> pathHandler = new ConcurrentHashMap<>();
            pathHandler.put(path, handler);
            handlers.put(httpMethod, pathHandler);
        }
    }

    public void start() {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    final Socket socket = serverSocket.accept();
                    threadPoll.submit(() -> processingClient(socket));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            threadPoll.shutdown();
        }
    }

    private Handler findHandler(HttpMethod httpMethod, URI path) {
        Map<URI, Handler> methodHandler = handlers.get(httpMethod);
        if (methodHandler != null) {
            return methodHandler.get(path);
        }
        return null;
    }

    private void sendNotFind(BufferedOutputStream out) throws IOException {
        out.write(("HTTP/1.1 404 NOT FOUND\r\n" +
                "Content-Length: 0 \r\n" +
                "Connection: close\r\n" +
                "\r\n").getBytes());
        out.flush();
    }

    private void processingClient(Socket socket) {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {

            Request request = Request.parseRequest(in);
            HttpMethod method = request.getMethod();
            URI path = request.getPath();

            Handler handler = findHandler(method, path);

            if (handler == null) {
                sendNotFind(out);
            } else {
                handler.handle(request, out);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
