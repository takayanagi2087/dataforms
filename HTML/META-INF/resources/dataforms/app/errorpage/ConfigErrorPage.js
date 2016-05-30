/**
 * @fileOverview {@link ConfigErrorPage}クラスを記述したファイルです。
 */

/**
 * @class ConfigErrorPage
 * 設定エラーページクラス。
 * <pre>
 * DB接続関連の設定に問題がある場合に、その原因を表示します。
 * </pre>
 * @extends Page
 */
ConfigErrorPage = createSubclass("ConfigErrorPage", {}, "Page");


/**
 * HTMLエレメントとの対応付けを行います。
 */
ConfigErrorPage.prototype.attach = function() {
	Page.prototype.attach.call(this);
};
