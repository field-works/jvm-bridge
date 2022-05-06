package jp.co.field_works.field_reports;

import java.io.*;

/// @cond
public final class ExecProxy implements Proxy {

    String exePath;
    File cwd;
    int logLevel;
    OutputStream logStream;

    public ExecProxy(
        String exePath, String cwd,
        int logLevel, OutputStream logStream)
    {
        this.exePath = exePath;
        this.cwd = new File(cwd);
        this.logLevel = logLevel;
        this.logStream = logStream;
    }

    private void pipeStream(InputStream instream, OutputStream outStream) throws IOException
    {
        byte[] buf = new byte[16384];
        int len;
        while ((len = instream.read(buf)) >= 0) {
            outStream.write(buf, 0, len);
        }
    }

    @Override
    public String version() throws ReportsException
    {
        ProcessBuilder pb = new ProcessBuilder(this.exePath, "version");
        pb.directory(this.cwd);
        try {
            Process process = pb.start();
            try (InputStream stdout = process.getInputStream();
                 InputStream stderr = process.getErrorStream();
                 ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                this.pipeStream(stdout, result);
                this.pipeStream(stderr, this.logStream);
                int returncode = process.waitFor();
                if (returncode != 0)
                    throw new RuntimeException(String.format("Exit Code = %d", returncode));
                return result.toString().trim();
            }
        } catch (Exception exn) {
            throw new ReportsException(exn_message(exn));
        }
    }
    
    @Override
    public byte[] render(String jparam) throws ReportsException
    {
        ProcessBuilder pb = new ProcessBuilder(this.exePath, "render", "-l", String.valueOf(this.logLevel), "-", "-");
        pb.directory(this.cwd);
        try {
            Process process = pb.start();
            try (OutputStreamWriter stdin = new OutputStreamWriter(process.getOutputStream())) {
                stdin.write(jparam);
            }
            try (InputStream stdout = process.getInputStream();
                 InputStream stderr = process.getErrorStream();
                 ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                this.pipeStream(stdout, result);
                this.pipeStream(stderr, this.logStream);
                int returncode = process.waitFor();
                if (returncode != 0)
                    throw new RuntimeException(String.format("Exit Code = %d", returncode));
                return result.toByteArray();
            }
        } catch (Exception exn) {
            throw new ReportsException(exn_message(exn));
        }
    }

    @Override
    public String parse(byte[] pdf) throws ReportsException
    {
        ProcessBuilder pb = new ProcessBuilder(this.exePath, "parse", "-");
        pb.directory(this.cwd);
        try {
            Process process = pb.start();
            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write(pdf);
            }
            try (
                InputStream stdout = process.getInputStream();
                 InputStream stderr = process.getErrorStream();
                 ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                this.pipeStream(stdout, result);
                this.pipeStream(stderr, this.logStream);
                int returncode = process.waitFor();
                if (returncode != 0)
                    throw new RuntimeException(String.format("Exit Code = %d", returncode));
                return result.toString("UTF-8").trim();
            }
        } catch (Exception exn) {
            throw new ReportsException(exn_message(exn));
        }
    }

    private String exn_message(Exception exn)
    {
        return String.format("Process terminated abnormally: %s.", exn.getMessage());
    }
}
/// @endcond