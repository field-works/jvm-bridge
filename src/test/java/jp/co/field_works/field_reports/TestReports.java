package jp.co.field_works.field_reports;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.nio.file.*;

class TestReports {

    private Proxy reports = null;

    @BeforeEach
    public void setUp() {
        this.reports = Bridge.createProxy(null);
    }

    @Test
    void バージョン番号を所得できる() {
        try {
            String version = this.reports.version();
            assertTrue(version.startsWith("2."));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    void JSON文字列を元にPDFを生成できる() {
        String param = "{\n" +
        "   \"template\": {\"paper\": \"A4\"},\n" +
        "   \"context\": {\n" +
        "       \"hello\": {\n" +
        "           \"new\": \"Tx\",\n" +
        "           \"value\": \"Hello, World!\",\n" +
        "           \"rect\": [100, 700, 400, 750]\n" +
        "       }\n" +
        "   }\n" +
        "}\n";
        try {
            byte[] pdf = this.reports.render(param);
            assertEquals("%PDF-1.6", new String(Arrays.copyOfRange(pdf, 0, 8)));
            assertEquals("%%EOF\n", new String(Arrays.copyOfRange(pdf, pdf.length-6, pdf.length)));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    void パースエラーで例外が発生する() {
        assertThrows(ReportsException.class, () -> this.reports.render("{,}"));
    }

    @Test
    void PDFデータを解析できる() {
        try {
            Path file = Paths.get("src/test/files/mitumori.pdf");
            byte[] pdf = Files.readAllBytes(file);
            String json = this.reports.parse(pdf);
            assertTrue(json.startsWith("{"));
            assertTrue(json.endsWith("}"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}