/**
 * @fileOverview {@link NumericValidator}クラスを記述したファイルです。
 */

/**
 * @class NumericValidator
 * Numeric型数値バリデータクラス。
 * <pre>
 * DBのNumeric型用のチェックで、精度と小数点以下桁数もチェックします。
 * @extends FieldValidator
 */

NumericValidator = createSubclass("NumericValidator", {}, "FieldValidator");

/**
 * バリデーションを行ないます。
 * @param {String} v 値。
 * @returns {Boolean} バリデーション結果。
 */
NumericValidator.prototype.validate = function(v) {
	if (this.isBlank(v)) {
		return true;
	}
    var value = "" + v;
    // カンマを削除
    var sc = 0;
    var abs = value.replace(/[\-\+,]/g, "");
	var sidx = abs.indexOf('.');
	var ln = abs.length;
	if (sidx >= 0) {
		sc = abs.length - sidx - 1;
		ln = sidx;
	}
	if (ln > this.precision - this.scale) {
		return false;
	}
	if (sc > this.scale) {
		return false;
	}
	return true;
};

/**
 * エラーメッセージを取得します。
 * @returns {String} エラーメッセージ。
 */
NumericValidator.prototype.getMessage = function(dspname) {
	return MessagesUtil.getMessage(this.messageKey, dspname, this.precision, this.scale);
};

