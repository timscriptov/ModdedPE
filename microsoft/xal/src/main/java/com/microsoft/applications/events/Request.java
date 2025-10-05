package com.microsoft.applications.events;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 05.10.2025
 *
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
class Request implements Runnable {
    private static final int BUFFER_SIZE = 1024;

    private final byte[] requestBody;
    private final HttpURLConnection connection;
    private final HttpClient parent;
    private final String requestId;

    public Request(HttpClient httpClient, String url, String method, byte[] body,
            String requestId, int[] headerOffsets, byte[] headerData) throws IOException {
        this.parent = httpClient;
        this.requestId = requestId;
        this.requestBody = body != null ? body : new byte[0];

        this.connection = initializeConnection(httpClient, url, method);
        setupHeaders(headerOffsets, headerData);
    }

    private @NotNull HttpURLConnection initializeConnection(@NotNull HttpClient httpClient, String url, String method)
            throws IOException {
        HttpURLConnection conn = (HttpURLConnection) httpClient.newUrl(url).openConnection();
        conn.setRequestMethod(method);

        if (requestBody.length > 0) {
            conn.setFixedLengthStreamingMode(requestBody.length);
            conn.setDoOutput(true);
        }

        return conn;
    }

    private void setupHeaders(int @NotNull [] headerOffsets, byte[] headerData) {
        for (int i = 0; i + 1 < headerOffsets.length; i += 2) {
            String headerName = extractHeaderString(headerData, headerOffsets[i], headerOffsets[i]);
            String headerValue = extractHeaderString(headerData,
                    headerOffsets[i] + headerOffsets[i], headerOffsets[i + 1]);

            connection.setRequestProperty(headerName, headerValue);
        }
    }

    @Contract("_, _, _ -> new")
    private @NotNull String extractHeaderString(byte[] data, int offset, int length) {
        return new String(data, offset, length, StandardCharsets.UTF_8);
    }

    @Override
    public void run() {
        int responseCode = -1;
        String[] responseHeaders = new String[0];
        byte[] responseBody = new byte[0];

        try {
            sendRequestBody();
            responseCode = getResponseCode();
            responseHeaders = extractResponseHeaders();
            responseBody = readResponseBody(responseCode);
        } catch (Exception e) {
            // Log error here if needed
        } finally {
            connection.disconnect();
            parent.dispatchCallback(requestId, responseCode, responseHeaders, responseBody);
        }
    }

    private void sendRequestBody() throws IOException {
        if (requestBody.length > 0) {
            connection.getOutputStream().write(requestBody);
        }
    }

    private int getResponseCode() throws IOException {
        return connection.getResponseCode();
    }

    private String @NotNull [] extractResponseHeaders() {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> headersList = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            if (entry.getKey() != null) {
                for (String value : entry.getValue()) {
                    headersList.add(entry.getKey());
                    headersList.add(value);
                }
            }
        }

        return headersList.toArray(new String[0]);
    }

    private byte @NotNull [] readResponseBody(int responseCode) throws IOException {
        try (BufferedInputStream inputStream = getResponseStream(responseCode)) {
            if (inputStream == null) {
                return new byte[0];
            }

            List<byte[]> chunks = new ArrayList<>();
            int totalSize = 0;
            byte[] buffer = new byte[BUFFER_SIZE];

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                if (bytesRead > 0) {
                    byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                    chunks.add(chunk);
                    totalSize += bytesRead;
                }
            }

            return combineChunks(chunks, totalSize);
        }
    }

    private @Nullable BufferedInputStream getResponseStream(int responseCode) {
        try {
            return responseCode >= 300 ?
                    new BufferedInputStream(connection.getErrorStream()) :
                    new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }

    private byte @NotNull [] combineChunks(@NotNull List<byte[]> chunks, int totalSize) {
        byte[] result = new byte[totalSize];
        int currentPosition = 0;

        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, result, currentPosition, chunk.length);
            currentPosition += chunk.length;
        }

        return result;
    }
}