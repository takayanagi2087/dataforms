/**
 * @fileOverview {@link DateTimeValidator}クラスを記述したファイルです。
 */

/**
 * @class DateTimeValidator
 * 日付時刻バリデータの基本クラス。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */

DateTimeValidator = createSubclass("DateTimeValidator", {dateFormatKey: null}, "FieldValidator");

/**
 * バリデーションを行ないます。
 * @param {String} v 値。
 * @returns {Boolean} バリデーション結果。
 */
DateTimeValidator.prototype.validate = function(v) {
	var dateFormat = MessagesUtil.getMessage(this.dateFormatKey);
	if (this.isBlank(v)) {
		return true;
	}
	var fmt = new SimpleDateFormat(dateFormat);
    if (fmt.parse(v) != null) {
    	return true;
    }
    return false;
};


/**
 * エラーメッセージを取得します。
 * @returns {String} エラーメッセージ。
 */
DateTimeValidator.prototype.getMessage = function(dspname) {
	var dateFormat = MessagesUtil.getMessage(this.dateFormatKey);
	return MessagesUtil.getMessage(this.messageKey, dspname, dateFormat);
};
