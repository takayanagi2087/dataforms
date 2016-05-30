/**
 * @fileOverview {@link MultiSelectField}クラスを記述したファイルです。
 */

/**
 * @class MultiSelectField
 * 複数選択リストフィールドクラス。
 * <pre>
 * </pre>
 * @extends SelectField
 */
MultiSelectField = createSubclass("MultiSelectField", {}, "SelectField");

/**
 * 対応するコンポーネントに値を設定します。
 * <pre>
 * 対応するのは、マルチ選択可能なselectまたは同一nameを持つチェックボックスです。
 * </pre>
 * @param {Array} value 値。
 *
 */
MultiSelectField.prototype.setValue = function(value) {
	var comp = this.get();
	if (comp.size() > 0) {
		var tag = comp.prop("tagName");
		var type = comp.prop("type");
		if ("INPUT" == tag && type == "checkbox") {
			// checkboxの対応.
			comp.each(function() {
				var v = $(this).val();
				$(this).prop("checked", false);
				if (value != null) {
					for (var i = 0; i < value.length; i++) {
						if (v == value[i]) {
							$(this).prop("checked", true);
						}
					}
				}
			});
		} else if ("SELECT" == tag) {
			// マルチ選択リストボックスの設定.
			var opt = comp.find("option");
			opt.each(function() {
				var v = $(this).val();
				$(this).prop("selected", false);
				if (value != null) {
					for (var i = 0; i < value.length; i++) {
						if (v == value[i]) {
							$(this).prop("selected", true);
						}
					}
				}
			});
		} else {
			this.setTextValue(comp, value);
		}
	}
};

/**
 * SPAN等の表示用タグへ値を設定します。
 * @param {jQuery} comp コンポーネント。
 * @param {Array} value 値。
 */
MultiSelectField.prototype.setTextValue = function(comp, value) {
	var v = "";
	if (value != null) {
		for (var i = 0; i < value.length; i++) {
			if (v.length > 0) {
				v += ",";
			}
			var iv = value[i];
			for (var j = 0; j < this.optionList.length; j++) {
				if (value[i] == this.optionList[j].value) {
					iv = this.optionList[j].name;
					break;
				}
			}
			v += iv;
		}
	}
	comp.text(v);
};


/**
 * 値を取得します。
 * @return {String} 値。
 */
MultiSelectField.prototype.getValue = function() {
	var comp = this.get();
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	var ret = [];
	if ("INPUT" == tag && "checkbox" == type) {
		comp.each(function() {
			if ($(this).prop("checked")) {
				ret.push($(this).val());
			}
		});
	} else {
		ret = comp.val();
	}
	return ret;
}
