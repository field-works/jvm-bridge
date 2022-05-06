package jp.co.field_works.field_reports;

/**
 * JVM Bridgeで発生する例外
 * 
 */
public final class ReportsException extends Exception {
    public ReportsException() {
    }

    public ReportsException(String message) {
        super(message);
    }

    public ReportsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportsException(Throwable cause) {
        super(cause);
    }
}
