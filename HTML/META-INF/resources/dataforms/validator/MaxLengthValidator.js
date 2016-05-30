/**
 * @fileOverview {@link MaxLengthValidator}クラスを記述したファイルです。
 */

/**
 * @class MaxLengthValidator
 * 最大長バリデータクラス。
 * @extends FieldValidator
 */
MaxLengthValidator = createSubclass("MaxLengthValidator", {}, "FieldValidator");

/**
 * バリデーションを行ないます。
 * @param {String} v 値。
 * @returns {Boolean} バリデーション結果。
 */
MaxLengthValidator.prototype.validate = function(v) {
	if (this.isBlank(v)) {
		return true;
	} else {
		if (v.length <= this.length) {
			return true;
		} else {
			return false;
		}
	}
};

/**
 * エラーメッセージを取得します。
 * @param dspname 項目名。
 * @returns {String} エラーメッセージ。
 */
MaxLengthValidator.prototype.getMessage = function(dspname) {
	return MessagesUtil.getMessage(this.messageKey, dspname, this.length);
};
