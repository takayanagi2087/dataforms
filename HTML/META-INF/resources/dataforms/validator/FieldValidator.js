/**
 * @fileOverview {@link FieldValidator}クラスを記述したファイルです。
 */

/**
 * @class FieldValidator
 * フィールドバリデータ基本クラス。
 * <pre>
 * </pre>
 * @extends WebComponent
 */
FieldValidator = createSubclass("FieldValidator", {}, "WebComponent");


/**
 * 初期化を行います。
 */
FieldValidator.prototype.init = function() {
	WebComponent.prototype.init.call(this);
};

/**
 * バリデーションを行ないます。
 * @param {String} v 値。
 * @returns {Boolean} バリデーション結果。
 */
FieldValidator.prototype.validate = function(v) {
	return true;
};


/**
 * エラーメッセージを取得します。
 * @returns {String} エラーメッセージ。
 */
FieldValidator.prototype.getMessage = function(dspname) {
	return MessagesUtil.getMessage(this.messageKey, dspname);
};


/**
 * 未入力かどうかをチェックします。
 * @param {String} v 値。
 * @returns {Boolean} 未入力の場合true。
 */
FieldValidator.prototype.isBlank = function(v) {
	if (v != null) {
		if (v.length != 0) {
			return false;
		}
	}
	return true;
};
