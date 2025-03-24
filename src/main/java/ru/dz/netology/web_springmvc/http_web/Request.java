package ru.dz.netology.web_springmvc.http_web;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private HttpMethod method;
    private Path path;
    private String httpType;
    private Map<String, String> headers;
    private String body;

    public Request(HttpMethod method, Path path, String httpType, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.httpType = httpType;
        this.headers = headers;
        this.body = body;
    }

    public HttpMethod getMethod() {
        return method;
    }


    public Path getPath() {
        return path;
    }


    public String getHttpType() {
        return httpType;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }


    public String getBody() {
        return body;
    }

    public static Request parseRequest(BufferedReader in) {
        try {
            String requestLine = in.readLine();
            String[] dataInRequestLine = requestLine.split(" ");

            if (dataInRequestLine.length < 3) {
                throw new IOException("Неверный запрос: " + requestLine);
            }

            String methodName = dataInRequestLine[0];
            HttpMethod method = HttpMethod.valueOf(methodName);

            Path path = Path.of(dataInRequestLine[1]);
            String httpType = dataInRequestLine[2];

            Map<String, String> headers = new HashMap<>();

            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                int indexOfSplitElement = headerLine.indexOf(':');
                String header = headerLine.substring(0, indexOfSplitElement).trim();
                String value = headerLine.substring(indexOfSplitElement + 1).trim();
                headers.put(header, value);
            }

            StringBuilder body = new StringBuilder();

            String bodyLine;
            if (headers.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                if (contentLength > 0) {
                    while ((bodyLine = in.readLine()) != null) {
                        body.append(bodyLine);
                    }
                }
            }

            return new Request(method, path, httpType, headers, body.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
