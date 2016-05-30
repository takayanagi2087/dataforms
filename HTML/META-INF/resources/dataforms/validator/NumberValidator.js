
/**
 * @fileOverview {@link NumberValidator}クラスを記述したファイルです。
 */

/**
 * @class NumbereValidator
 * 数値バリデータクラス。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */
NumberValidator = createSubclass("NumberValidator", {}, "FieldValidator");

/**
 * バリデーションを行ないます。
 * @param {String} v 値。
 * @returns {Boolean} バリデーション結果。
 */
NumberValidator.prototype.validate = function(v) {
	if (this.isBlank(v)) {
		return true;
	}
    var value = "" + v;
    // カンマを削除
    var ntext = value.replace(/,/g, "");
	var n = Number(ntext);
	if (isNaN(n)) {
		return false;
	} else {
		return true;
	}
};

