package jp.co.field_works.field_reports;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Field Reportsと連携するためのProxyオブジェクトを生成します。
 * 
 */
public final class Bridge 
{
    /**
     * 引数で与えられるURIに応じたField Reports Proxyオブジェクトを返却します。
     * 
     *      // コマンド連携時:
     *      import jp.co.field_works.field_reports.*;
     *      Proxy reports = Bridge.createProxy("exec:/usr/local/bin/reports?cwd=/usr/share&logleve=3");
     *
     *      // HTTP連携時:
     *      import jp.co.field_works.field_reports.*;
     *      Proxy reports = Bridge.createProxy("http://localhost:50080/");
     * 
     * @param uri Field Reportsとの接続方法を示すURI
     * <p>uriがnullの場合，環境変数'REPORTS_PROXY'からURIを取得します。</br>
     * 環境変数'REPORTS_PROXY'も未設定の場合既定値は"exec:reports"です。</p>
     * 
     *  URI書式（コマンド連携時）:
     * 
     *      exec:{exePath}?cwd={cwd}&amp;loglevel={logLevel}
     *
     *   - cwd, loglevelは省略可能です。
     *   - loglevelが0より大きい場合，STDERRにログを出力します。
     * 
     *  URI書式（HTTP連携時）:
     * 
     *      http://{hostName}:{portNumber}/
     *
     * @return Proxy Field Reports Proxyオブジェクト
     */
    public static Proxy createProxy(String uri)
    {
        if (uri == null)
            uri = System.getenv("REPORTS_PROXY");
        if (uri == null)
            uri = "exec:reports";

        if (uri.startsWith("exec:")) {
            String[] uris = uri.substring(5).split("\\?");
            if (uris.length == 2) {
                Map<String, String> q = splitQuery(uris[1]);
                String cwd = q.containsKey("cwd") ? q.get("cwd") : ".";
                int loglevel = q.containsKey("loglevel") ? Integer.parseInt(q.get("loglevel")) : 0;
                PrintStream logStream = loglevel > 0 ? System.err : null;
                return createExecProxy(uris[0], cwd, loglevel, logStream);
            } else {
                return createExecProxy(uris[0], ".", 0, null);
            }
        } else {
            return createHttpProxy(uri);
        }
    }

    /**
     *  コマンド呼び出しによりField Reportsと連携するProxyオブジェクトを生成します。
     * 
     * @param exePath Field Reportsコマンドのパス
     * @param cwd Field Reportsプロセス実行時のカレントディレクトリ
     * @param logLevel ログ出力レベル（0: ログを出力しない，1: ERRORログ，2: WARNログ，3: INFOログ，4: DEBUGログ）
     * @param logStream ログ出力先Stream
     * @return Field Reports Proxyオブジェクト
     */
    public static Proxy createExecProxy(
        String exePath, String cwd,
        int logLevel, OutputStream logStream)
    {
        return new ExecProxy(exePath, cwd, logLevel, logStream);
    }

    /**
     * HTTP通信によりField Reportsと連携するProxyオブジェクトを生成します。
     * 
     * @param baseUri ベースURI
     * @return Field Reports Proxyオブジェクト
     */
    public static Proxy createHttpProxy(String baseUri)
    {
        return new HttpProxy(baseUri);
    }

    private static Map<String, String> splitQuery(String query) {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                query_pairs.put(
                    URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException ignore) { }
        }
        return query_pairs;
    }
}
