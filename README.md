JVM Bridge 2.0.0
================

Field Reports JVM (Java Virtual Machine) Bridge（以降，本モジュールと表記します）は，
PDF帳票ツールField ReportsをJVM言語から利用するためのライブラリです。

JVM Bridge APIを通じて，Field Reportsの各機能を呼び出すことができます。

* Field Reportsのバージョンを取得する。

* レンダリング・パラメータを元にPDFを生成し，結果をバイナリ文字列として受け取る。

* PDFデータを解析し，フィールドや注釈の情報を取得する。

## 必要条件
### Field Reports本体

本モジュールのご利用に際しては，Field Reports本体が導入済みである必要があります。

Field Reportsのご購入もしくは試用版のダウンロードにつきましては，
下記サイトをご参照ください。

https://www.field-works.co.jp/製品情報/

### 連携手段の選択

本モジュールとField Reports本体との連携方法として，以下の２種類があります。
システム構成に応じて，適切な連携方法を選択してください。

* コマンド呼び出しによる連携
    - reports本体と本モジュールを同一マシンに配置する必要があります。
    - パスが通る場所にreportsコマンドを置くか，reportsコマンドのパスをAPIに渡してください。

* HTTP通信による連携
    - Field Reports本体をリモートマシンに配置することができます。
    - Field Reportsは，サーバーモードで常駐起動させてください（`reports server`）。
    - サーバーモードで使用するポート番号（既定値：`50080`）の通信を許可してください。

### Java処理系

* Java SE 8以上

## インストール
### jarファイルによるインストール

インストール媒体より，jarファイルを所定の格納場所にコピーしてください。

    field_reports-x.x.x.jar

jarファイルの格納場所は，環境変数'CLASSPATH'または実行時引数'-classpath'で指定してください。

### Mavenプロジェクトからの利用

Mavenから参照可能なインストールモジュールをGitHubで配布しています。

プロジェクトから利用する際には，`pos.xml`ファイルに下記の記述を追加してください。

```xml:pom.xml
  <dependencies>
    <dependency>
        <groupId>jp.co.field_works</groupId>
        <artifactId>field_reports</artifactId>
        <version>2.0.0</version>
    </dependency>
  </dependencies>

  <repositories>
    <repository>
      <id>github</id>
      <url>https://raw.githubusercontent.com/field-works/jvm-bridge/repo/</url>
    </repository>
  </repositories>

```

### 動作確認
### コマンド連携時

以下のコマンドを実行してください。

```
$ jshell --class-path <jarファイル格納場所>/field_reports-2.0.0.jar
jshell> import jp.co.field_works.field_reports.*
jshell> jp.co.field_works.field_reports.Proxy reports = Bridge.createProxy("exec:/usr/local/bin/reports")
jshell> reports.version()
$3 ==> "2.0.0"
jshell> reports.render("{}")
$4 ==> byte[672] { 37, 80, 68, 70, 45, 49,...
```

* 動作環境に応じて，create_proxy()に与えるパスを適宜変更してください。  
  （Windowsでは，"exec:C:/Program Files/Field Works/Field Reports x.x/bin/reports.exe"など）

### HTTP通携時

Field Reportsをサーバーモードで起動してください。

```
$ reports server -l3
```

次に，以下のコマンドを実行してください

```
$ jshell --class-path <jarファイル格納場所>/field_reports-2.0.0.jar
jshell> import jp.co.field_works.field_reports.*
jshell> jp.co.field_works.field_reports.Proxy reports = Bridge.createProxy("http://localhost:50080/")
jshell> reports.version()
$3 ==> "2.0.0"
jshell> reports.render("{}")
$4 ==> byte[672] { 37, 80, 68, 70, 45, 49,...
```

* 動作環境に応じて，create_proxy()に与えるURLを適宜変更してください。  

## API使用例

```java
import jp.co.field_works.field_reports.*;

Proxy reports = Bridge.createProxy("http://localhost:50080/");
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
    byte[] pdf = reports.render(param);

} catch (Exception e) {
    e.printStackTrace();
}
```

## ライセンス

本モジュールのソースコードは，BSDライセンスのオープンソースとします。

    https://github.com/field-works/jvm-bridge/

以下のような場合，自由に改変／再配布していただいて結構です。

* 独自機能の追加

* ビルド/実行環境等の違いにより，本モジュールが正常に機能しない。

* 未サポートのJavaバージョンへの対応のため改造が必要。

* 他の言語の拡張ライブラリ作成のベースとして利用したい。

ただし，ソースを改変したモジュール自体において問題が発生し場合については，
サポート対応いたしかねますのでご了承ください
（Field Reports本体もしくはオリジナルの本モジュールに起因する問題であれば対応いたします）。

## 著者

* 合同会社フィールドワークス / Field Works, LLC
* https://www.field-works.co.jp/
* support@field-works.co.jp