#dataforms.jar

## Description
Java Webアプリケーションフレームワークと、その開発ツールです。  
特徴を以下にまとめます。  

*依存ライブラリは少なく(jQuery,jQuery-ui,jsonic,apache-commonsのいくつかとpoi)、シンプルな構造です。
*Viewは、Javascriptやonxxx等のイベントアトリビュートを一切記述していない単純なHTMLで定義します。jsp,jsfは使用しません。
*Page,Form,Field等のJavaクラスと、それに対応したJavascriptのクラスが、HTMLを自動的に制御します。
*開発ツールを装備し、とりあえず動作するJava,Javascript,HTMLを自動生成することができます。
*データベースのテーブルや問い合わせは、JavaのTable,Queryクラスで定義するため、ほとんどSQLの記述は不要です。
*データベースのテーブル作成やテーブル構造の変更は、開発ツールで簡単に行うことができます。
*現在のところApache Derby,PostgreSQL,MySQL,Oralceに対応しています。
  
  
## Demo
ドキュメントは以下のリンクを参照してください。  
[ドキュメント](http://woontai.dip.jp/dfsample/dataforms/devtool/page/doc/DocFramePage.df)  

ドキュメントに記述されているサンプルは、以下のデモサイトで動作を確認できます。  
[サンプルページ](http://woontai.dip.jp/dfsample/sample/page/SamplePage.df)  

## Requirement
主に、Eclipse4.5 + Java8 + Tomcat8でテストしています。  
基本的に、Servlet 3.0に対応したアプリケーションサーバで動作するはずです。  
最近評価していませんが、以下のアプリケーションサーバで一度動作させています。  
  
glassfish-4.1  
wildfly-8.2.1.Final  
Oracle WebLogic Server 12.1.3.0  
  

## Install
[リリース](https://github.com/takayanagi2087/dataforms/releases)から、dfblank_xxx.warファイルをダウンロードし、Eclipseプロジェクトとしてインポートしてください。

## Licence
[MIT](https://github.com/takayanagi2087/dataforms/blob/master/LICENSE)

## Author
高柳　正彦

## Status
現在開発ツールのBUGをいくつか確認しており、それの修正後Ver1.00を正式リリースする予定です。  


