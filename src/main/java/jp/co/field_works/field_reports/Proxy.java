package jp.co.field_works.field_reports;

/**
 * Field Reportsの機能を呼び出すためのProxyインターフェースです。
 * 
 */
public interface Proxy {
    /**
     * バージョン番号を取得します。
     * @return バージョン番号
     * @throws ReportsException Field Reportsで発生した例外
     */
    String version() throws ReportsException;
    
    /**
     * レンダリング・パラメータparamを元にレンダリングを実行します。
     * @param param レンダリング・パラメータ（JSON文字列）
     * @return PDFデータ
     * @throws ReportsException Field Reportsで発生した例外
     * @see ユーザーズ・マニュアル「第5章 レンダリングパラメータ」
     */
    byte[] render(String param) throws ReportsException;

    /**
     * PDFデータを解析し，フィールドや注釈の情報を取得します。
     * @param pdf PDFデータ
     * @return 解析結果（JSON文字列）
     * @throws ReportsException Field Reportsで発生した例外
     */
    String parse(byte[] pdf) throws ReportsException;
}