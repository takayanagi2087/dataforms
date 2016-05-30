/**
 * @fileOverview {@link SingleSelectField}クラスを記述したファイルです。
 */

/**
 * @class SingleSelectField
 * 単一選択リストクラス。
 * <pre>
 * </pre>
 * @extends SelectField
 */
SingleSelectField = createSubclass("SingleSelectField", {}, "SelectField");

/**
 * INPUTまたはSELECTタグへ値を設定します。
 * @param {jQuery} comp 値を設定するコンポーネント。
 * @param {String} value 値。
 */
SingleSelectField.prototype.setInputValue = function(comp, value) {
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	if ("INPUT" == tag && "radio" == type) {
		comp.each(function() {
			if ($(this).val() == value) {
				$(this).prop("checked", true);
			}
		});
	} else {
		comp.val(value);
	}
};


/**
 * 値を取得します。
 * @return {String} 値。
 */
SingleSelectField.prototype.getValue = function() {
	var comp = this.get();
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	var ret = "";
	if ("INPUT" == tag && "radio" == type) {
		comp.each(function() {
			if ($(this).prop("checked")) {
				ret = $(this).val();
			}
		});
	} else {
		ret = comp.val();
	}
	return ret;
}
