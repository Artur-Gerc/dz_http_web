package ru.dz.netology.web_springmvc.http_web;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Request {
    private HttpMethod method;
    private URI path;
    private String httpType;
    private Map<String, String> headers;
    private String body;

    private List<NameValuePair> params;

    public Request(HttpMethod method, URI path, String httpType, List<NameValuePair> params, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.httpType = httpType;
        this.headers = headers;
        this.body = body;
        this.params = params;
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

            List<NameValuePair> params = getQueryParams(path);

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

            return new Request(method, path, httpType, params, headers, body.toString());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<NameValuePair> getQueryParams(URI path){
        List<NameValuePair> params = URLEncodedUtils.parse(path, StandardCharsets.UTF_8);
        return params;
    }

    public Optional<NameValuePair> getQueryParam(String name) {
        for (NameValuePair param : params){
            if(param.getName().equals(name)){
                return Optional.of(param);
            }
        }
        return Optional.empty();
    }
}
