/**
 * @fileOverview {@link RequiredValidator}クラスを記述したファイルです。
 */

/**
 * @class RequiredValidator
 * 必須バリデータクラス。
 * <pre>
 * </pre>
 * @extends FieldValidator
 */
RequiredValidator = createSubclass("RequiredValidator", {}, "FieldValidator");

/**
 * バリデーションを行ないます。
 * @param {String} v 値。
 * @returns {Boolean} バリデーション結果。
 */
RequiredValidator.prototype.validate = function(v) {
	return (this.isBlank(v) == false);
};


