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
 * 値の設定を行ないます。
 * <pre>
 * 同時にユーザ属性値の選択肢も更新します。
 * </pre>
 * @param {String} v 値。
 *
 */
UserAttributeValueField.prototype.setValue = function(v) {
	// この設定処理は選択肢がそろっていないので空振りします。
	EnumOptionSingleSelectField.prototype.setValue.call(this, v);
	// 一旦値を保持し、選択肢を取得してから設定します。
	this.value = v;
	var tid = this.id.replace("userAttributeValue", "userAttributeType");
	var type = this.parent.find("#" + this.selectorEscape(tid)).val();
	this.setUserAttributeType(type);
}


/**
 * ユーザ属性を設定します。
 * <pre>
 * 指定されたユーザ属性に対応した選択肢を取得し設定します。
 * </pre>
 * @param {String} type ユーザ属性。
 */
UserAttributeValueField.prototype.setUserAttributeType = function(type) {
	var thisField = this;
	var m = this.getAsyncServerMethod("getTypeOption");
	var opt = m.execute("type=" + type, function(opt) {
		if (opt.status == ServerMethod.SUCCESS) {
			thisField.setOptionList(opt.result);
			// setValueから呼ばれた場合、保持した値を設定します。
			thisField.get().val(thisField.value);
		}
	});
};