package jp.co.field_works.field_reports;

import java.net.*;
import java.io.*;

/// @cond
public final class HttpProxy implements Proxy {

    String baseUri;

    public HttpProxy(String baseUri)
    {
        this.baseUri = baseUri.endsWith("/") ? baseUri : baseUri + "/";
    }

    private HttpURLConnection openConnection(String file) throws MalformedURLException, IOException
    {
        URL url = new URL(this.baseUri + file);
        return (HttpURLConnection)url.openConnection();
    }

    private void pipeStream(InputStream instream, OutputStream outStream) throws IOException
    {
        if (instream == null) {
            outStream.write("(empty)".getBytes());
            return;
        }

        byte[] buf = new byte[16384];
        int len;
        while ((len = instream.read(buf)) >= 0) {
            outStream.write(buf, 0, len);
        }
    }

    @Override
    public String version() throws ReportsException
    {
        HttpURLConnection conn = null;
        try {
            conn = this.openConnection("version");
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            try (InputStream receivedStream = conn.getInputStream();
                 ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                this.pipeStream(receivedStream, result);
                return result.toString().trim();
            }
        } catch (IOException exn) {
            throw new ReportsException(response_message(conn), exn);
        } catch (Exception exn) {
            throw new ReportsException(exn_message(exn));
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
    
    @Override
    public byte[] render(String jparam) throws ReportsException
    {
        HttpURLConnection conn = null;
        try {
            conn = this.openConnection("render");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            try (OutputStreamWriter sendStream = new OutputStreamWriter(conn.getOutputStream())) {
                sendStream.write(jparam);
            }
            try (InputStream receiveStream = conn.getInputStream();
                 ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                this.pipeStream(receiveStream, result);
                return result.toByteArray();
            }
        } catch (IOException exn) {
            throw new ReportsException(response_message(conn), exn);
        } catch (Exception exn) {
            throw new ReportsException(exn_message(exn));
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    @Override
    public String parse(byte[] pdf) throws ReportsException
    {
        HttpURLConnection conn = null;
        try {
            conn = this.openConnection("parse");
            conn.addRequestProperty("Content-Type", "application/pdf");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            try (OutputStream sendStream = conn.getOutputStream()) {
                sendStream.write(pdf);
            }
            try (InputStream stdout = conn.getInputStream();
                 ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                this.pipeStream(stdout, result);
                return result.toString("UTF-8");
            }
        } catch (IOException exn) {
            throw new ReportsException(response_message(conn), exn);
        } catch (Exception exn) {
            throw new ReportsException(exn_message(exn));
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private String exn_message(Exception exn)
    {
        return String.format("Fail to HTTP comunication: %s.", exn.getMessage());
    }

    private String response_message(HttpURLConnection conn)
    {
        try {
            try (InputStream receivedStream = conn.getErrorStream();
                    ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                this.pipeStream(receivedStream, result);
                return String.format("Fail to HTTP comunication: Status Code = %d, Reason = %s, Response = %s.",
                    conn.getResponseCode(), conn.getResponseMessage(), result.toString());
            }
        } catch (IOException exn) {
            return exn_message(exn);
        }
    }
}
/// @endcond