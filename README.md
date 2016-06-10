#dataforms.jar

## Description
Java Webアプリケーションフレームワークと、その開発ツールです。  
開発環境の指定がないプロジェクトで使用してきたものです。  
ドキュメント、開発ツールが整ってきたので公開します。  

特徴を以下にまとめます。  

* 習得するのに必要な知識は、HTML,Java,Javascript,jQueryくらいです。SQLの基本を押さえておけば、Daoクラス関連の機能もすぐに理解できると思います。
* 依存ライブラリ(jQuery,jQuery-ui,jsonic,apache-commonsのいくつかとpoi)は少なく、シンプルな構造です。
* JSPを使用せず、HTMLをそのまま使用します。
* dataforms.jarが自動生成する処理が、HTML中のイベントハンドラを適切に設定します。そのため、HTMLにはJavascriptやonxxx等のイベントアトリビュートを一切記述しません。
* 開発ツールを装備し、とりあえず動作するJava,Javascript,HTMLを自動生成することができます。
* データベースのテーブルや問い合わせは、JavaのTable,Queryクラスで定義するため、ほとんどSQLの記述は不要です。
* データベースのテーブル作成やテーブル構造の変更は、開発ツールで簡単に行うことができます。
* 複数のベースサーバに対応し、データベースサーバに依存しないアプリケーションの構築が可能です。(開発環境は組み込みApache Derby、運用はPostgreSQLというシステム開発の実績があります。)
* デフォルトのフレームはレスポンシブデザインになっており、1つのHTMLでPC,タブレット,スマートフォンの画面サイズに対応します。
* フレームデザインは単純なHTML,CSSで記述してあるので、簡単にカスタマイズすることができます。

## Install
以下のリンクから、dfblank_xxx.warファイルをダウンロードし、Eclipseでインポートしてください。  
[リリース](https://github.com/takayanagi2087/dataforms/releases)  
インポートの手順は以下のドキュメントを参照してください。  
[ドキュメント](http://woontai.dip.jp/dfsample/dataforms/devtool/page/doc/DocFramePage.df)  

## Demo
ドキュメントに記述されているサンプルは、以下のデモサイトで動作しています。  
[サンプルページ](http://woontai.dip.jp/dfsample/sample/page/SamplePage.df)  
ドキュメントで作成するSamplePageはログインしないと表示されないページですが、デモページではその制限を外しています。  

## Requirement
主に、Eclipse4.5 + Java8 + Tomcat8 + Apache Derby,PostgreSQLでテストしています。  
Servlet 3.0に対応したアプリケーションサーバで動作するはずです。  
最近評価していませんが、以下のアプリケーションサーバで一度動作させています。  

* glassfish-4.1  
* wildfly-8.2.1.Final  
* Oracle WebLogic Server 12.1.3.0  

対応しているデータベースサーバは、以下の通りです。(バージョンは実績のあるバージョンを記載しています。)  

* Apache Derby 10.11.1.1
* PostgreSQL 8.4.20, 9.2.7
* MariaDB(MySQL) 5.5.37
* Oralce11g 11.2.0.1.0

## Licence
[MIT](https://github.com/takayanagi2087/dataforms/blob/master/LICENSE)  

## Status
ライブラリ本体は安定してきており、既にdataforms.jarを応用したシステムをいくつか運用しています。  
現在開発ツールのBUGをいくつか確認しており、それの修正後Ver1.00を正式リリースする予定です。  


