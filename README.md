#dataforms.jar

## Description
Java Webアプリケーションフレームワークと、その開発ツールです。  
特徴を以下にまとめます。  

* 依存ライブラリ(jQuery,jQuery-ui,jsonic,apache-commonsのいくつかとpoi)は少なく、シンプルな構造です。
* dataforms.jar、上記の依存ライブラリ、対応するデータベースサーバがあれば、データベースを使用したWEBアプリケーションの構築が可能です。
* ViewはJavascriptやonxxx等のイベントアトリビュートを一切記述していない単純なHTMLで定義します。jsp,jsfは使用しません。
* Page,Form,Field等のJavaクラスと、それに対応したJavascriptのクラスが、HTMLを自動的に制御します。
* 開発ツールを装備し、とりあえず動作するJava,Javascript,HTMLを自動生成することができます。
* データベースのテーブルや問い合わせは、JavaのTable,Queryクラスで定義するため、ほとんどSQLの記述は不要です。
* データベースのテーブル作成やテーブル構造の変更は、開発ツールで簡単に行うことができます。
* 複数のベースサーバに対応し、データベースサーバに依存しないアプリケーションの構築が可能です。(開発環境は組み込みApache Derby、運用はPostgreSQLというシステムの実績があります。)
* デフォルトのフレームはレスポンシブデザインになっており、PC,タブレット,スマートフォンの画面サイズに対応しています。
* フレームデザインは単純なHTMLCSSで記述してあるので、簡単にカスタマイズすることができます。

## Install
[リリース](https://github.com/takayanagi2087/dataforms/releases)から、dfblank_xxx.warファイルをダウンロードし、Eclipseでインポートしてください。  
インポートの手順は以下のドキュメントを参照してください。  
[ドキュメント](http://woontai.dip.jp/dfsample/dataforms/devtool/page/doc/DocFramePage.df)  


## Demo
ドキュメントに記述されているサンプルは、以下のデモサイトで動作しています。  
[サンプルページ](http://woontai.dip.jp/dfsample/sample/page/SamplePage.df)  
ドキュメントで作成するSamplePageはログインしないと表示されないページですが、デモページではその制限を外しています。  

## Requirement
主に、Eclipse4.5 + Java8 + Tomcat8 + Apache Derby,PostgreSQLでテストしています。  
基本的に、Servlet 3.0に対応したアプリケーションサーバで動作するはずです。  
最近評価していませんが、以下のアプリケーションサーバで一度動作させています。  

* glassfish-4.1  
* wildfly-8.2.1.Final  
* Oracle WebLogic Server 12.1.3.0  

対応しているデータベースサーバは以下の通りです。  

* Apache Derby
* PostgreSQL
* MariaDB(MySQL)
* Oralce

## Licence
[MIT](https://github.com/takayanagi2087/dataforms/blob/master/LICENSE)

## Status
ライブラリ本体は安定してきており、既にdataforms.jarを応用したシステムをいくつか運用しています。  
現在開発ツールのBUGをいくつか確認しており、それの修正後Ver1.00を正式リリースする予定です。  


