/**
 * @fileOverview {@link FileSizeValidator}クラスを記述したファイルです。
 */

/**
 * @class FileSizeValidator
 * 最大長バリデータクラス。
 * @extends FieldValidator
 */
FileSizeValidator = createSubclass("FileSizeValidator", {}, "FieldValidator");

/**
 * バリデーションを行ないます。
 * @param {String} v 値。
 * @returns {Boolean} バリデーション結果。
 */
FileSizeValidator.prototype.validate = function(v) {
	if (this.isBlank(v)) {
		return true;
	} else {
		if (this.parent.get()[0].files.length > 0) {
			var size = this.parent.get()[0].files[0].size;
			if (size > this.maxFileSize) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
};

/**
 * エラーメッセージを取得します。
 * @param dspname 項目名。
 * @returns {String} エラーメッセージ。
 */
FileSizeValidator.prototype.getMessage = function(dspname) {
	return MessagesUtil.getMessage(this.messageKey, dspname, (this.maxFileSize / 1024 / 1024) + "MB");
};
