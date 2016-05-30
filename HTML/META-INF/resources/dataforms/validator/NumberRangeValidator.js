/**
 * @fileOverview {@link NumberRangeValidator}クラスを記述したファイルです。
 */

/**
 * @class NumberRangeValidator
 * 数値範囲バリデータクラス。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */
NumberRangeValidator = createSubclass("NumberRangeValidator", {}, "FieldValidator");


/**
 * バリデーションを行ないます。
 * @param {String} v 値。
 * @returns {Boolean} バリデーション結果。
 */
NumberRangeValidator.prototype.validate = function(v) {
	if (this.isBlank(v)) {
		return true;
	}
    var value = "" + v;
    // カンマを削除
    var ntext = value.replace(/,/g, "");
	var num = Number(ntext);
	if (this.min <= num && num <= this.max) {
		return true;
	} else {
		return false;
	}
};

/**
 * エラーメッセージを取得します。
 * @returns {String} エラーメッセージ。
 */
NumberRangeValidator.prototype.getMessage = function(dspname) {
	return MessagesUtil.getMessage(this.messageKey, dspname, this.min, this.max);
};

