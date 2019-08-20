/**
 * @fileOverview {@link ErrorPage}クラスを記述したファイルです。
 */

/**
 * @class ErrorPage
 *
 * エラーメッセージページクラスです。
 * <pre>
 * エラーメッセージ表示ページです。
 * </pre>
 * @extends BasePage
 */
ErrorPage = createSubclass("ErrorPage", {}, "BasePage");


/**
 * HTMLエレメントとの対応付けを行います。
 */
ErrorPage.prototype.attach = function() {
	Page.prototype.attach.call(this);
	var title = MessagesUtil.getMessage("errorpage.title");
	var backButton = MessagesUtil.getMessage("errorpage.backbutton");
	var message = this.errorMessage;
	this.find('title').html(title);
	this.find('h1').html(title);
	this.find('#errorMessages').html(message);
	this.find('#backButton').val(backButton);
};
