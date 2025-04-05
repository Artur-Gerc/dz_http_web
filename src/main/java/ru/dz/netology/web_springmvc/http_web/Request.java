package ru.dz.netology.web_springmvc.http_web;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {
    private HttpMethod method;
    private URI path;
    private String httpType;
    private Map<String, String> headers;
    private String body;

    private List<NameValuePair> queryParams;
    private List<NameValuePair> postParam;


    public Request(HttpMethod method, URI path, String httpType, List<NameValuePair> queryParams,
                   List<NameValuePair> postParam, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.httpType = httpType;
        this.headers = headers;
        this.body = body;
        this.queryParams = queryParams;
        this.postParam = postParam;

    }

    public HttpMethod getMethod() {
        return method;
    }


    public URI getPath() {
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

            String requestUri = dataInRequestLine[1];
            URI uri = new URI(requestUri);
            URI path = new URI(uri.getPath());

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

            final int MAX_BODY_LENGTH = 10_000_000;
            if (headers.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));

                if (contentLength > 0) {
                    if (contentLength > MAX_BODY_LENGTH) {
                        throw new IOException("Тело запроса слишком большое: " + contentLength + " байт");
                    }
                    final int BUFFER_SIZE = 8192;
                    int totalRead = 0;

                    while (totalRead < contentLength) {
                        char[] buffer = new char[BUFFER_SIZE];
                        int bytesToRead = Math.min(BUFFER_SIZE, contentLength - totalRead);
                        int bytesRead = in.read(buffer, 0, bytesToRead);

                        if (bytesRead == -1) {
                            throw new IOException("Непредвиденный конец потока при чтении тела запроса");
                        }

                        body.append(buffer, 0, bytesRead);

                        totalRead += bytesRead;
                    }
                }
            }

            List<NameValuePair> queryParams = getQueryParams(uri);
            List<NameValuePair> postParams = getPostParams(body.toString());

            return new Request(method, path, httpType, queryParams, postParams, headers, body.toString());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<NameValuePair> getQueryParams(URI path) {
        return URLEncodedUtils.parse(path, StandardCharsets.UTF_8);
    }

    public String getQueryParam(String name) {
        return queryParams.stream().filter(param -> param.getName().equals(name))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElse(null);
    }


    public static List<NameValuePair> getPostParams(String body) {
        return URLEncodedUtils.parse(body, StandardCharsets.UTF_8);
    }

    public List<String> getPostParam(String paramName) {
        return postParam.stream()
                .filter(param -> param.getName().equals(paramName))
                .map(NameValuePair::getValue)
                .toList();
    }
}
