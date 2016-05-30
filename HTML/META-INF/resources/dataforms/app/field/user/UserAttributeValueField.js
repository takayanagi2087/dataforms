/**
 * @fileOverview {@link UserAttributeValueField}クラスを記述したファイルです。
 */

/**
 * @class UserAttributeValueField
 * ユーザ属性値フィールドクラス。
 * <pre>
 * ユーザ属性を指定すると、その選択肢を取得します。
 * </pre>
 * @extends EnumOptionSingleSelectField
 */
UserAttributeValueField = createSubclass("UserAttributeValueField", {}, "EnumOptionSingleSelectField");

/**
 * ユーザ属性を設定します。
 * <pre>
 * 指定されたユーザ属性に対応した選択肢を取得し設定します。
 * </pre>
 * @param {String} type ユーザ属性。
 */
UserAttributeValueField.prototype.setUserAttributeType = function(type) {
	var m = this.getSyncServerMethod("getTypeOption");
	var opt = m.execute("type=" + type);
	if (opt.status == ServerMethod.SUCCESS) {
		this.optionList = opt.result;
		this.setOptionList();
	}
};