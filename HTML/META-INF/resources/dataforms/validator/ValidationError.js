/**
 * @fileOverview {@link ValidationError}クラスを記述したファイルです。
 */

/**
 * @class ValidationError.
 * バリデーションエラー情報クラス.
 * @param {String} fid フィールドID.
 * @param {String} msg メッセージ.
 */
function ValidationError(fid, msg) {
	this.fieldId = fid;
	this.message = msg;
};
