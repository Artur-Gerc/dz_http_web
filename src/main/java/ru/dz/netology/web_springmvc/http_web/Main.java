package ru.dz.netology.web_springmvc.http_web;


import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        Server server = new Server(9999);
        server.addHandler("GET", "/messages", (request, stream) -> {
            String responseBody = "Hello from GET /messages";
            stream.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + responseBody.getBytes().length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    responseBody).getBytes());
            stream.flush();
        });

        server.addHandler("POST", "/messages", (request, stream) -> {
            String responseBody = "Hello from POST /messages";
            stream.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + responseBody.getBytes().length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    responseBody).getBytes());
            stream.flush();
        });

        server.start();
    }
}