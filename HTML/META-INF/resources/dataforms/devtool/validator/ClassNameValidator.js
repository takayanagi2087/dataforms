/**
 * @fileOverview {@link ClassNameValidator}クラスを記述したファイルです。
 */

/**
 * @class ClassNameValidator
 *
 * @extends RegexpValidator
 */
ClassNameValidator = createSubclass("ClassNameValidator", {}, "RegexpValidator");


/**
 * HTMLエレメントとの対応付けを行います。
 */
ClassNameValidator.prototype.attach = function() {
	RegexpValidator.prototype.attach.call(this);
};


/**
 * エラーメッセージを取得します。
 * @returns {String} エラーメッセージ。
 */
ClassNameValidator.prototype.getMessage = function(dspname) {
	return MessagesUtil.getMessage(this.messageKey, dspname, this.classType);
};