/**
 * @fileOverview {@link FlagField}クラスを記述したファイルです。
 */
/**
 * @class FlagField
 * フラグフィールドクラス。
 * <pre>
 * 通常checkboxに割り当て、'0' or '1'を指定します。
 * </pre>
 * @extends CharField
 */
FlagField = createSubclass("FlagField", {}, "CharField");


/**
 * HTMLの要素との対応付けを行います。
 */
FlagField.prototype.attach = function() {
	Field.prototype.attach.call(this);
};

/**
 * checkboxへの値設定を行います。
 * @param {jQuery} comp 値を設定するコンポーネント。
 * @param {String} value 値。
 */
FlagField.prototype.setInputValue = function(comp, value) {
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	if (tag == "INPUT" && type.toLowerCase() == "checkbox") {
		if (value == "1") {
			comp.prop("checked", true);
		} else {
			comp.prop("checked", false);
		}
	} else {
		comp.val(value);
	}
};

/**
 * フィールドのロック/ロック解除を行ないます。
 * @param {Boolean} lk ロックする場合true。
 */
FlagField.prototype.lock = function(lk) {
	var comp = this.get();
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	if ("INPUT" == tag && type.toLowerCase() == "checkbox") {
		var span = this.addSpan(comp);
		span.html("<input type='checkbox' id='" + this.id + "_ck' onclick='return false;'>");
		this.parent.find("#" + this.selectorEscape(this.id + "_ck")).prop("checked", comp.prop("checked"));
		if (lk) {
			span.show()
			comp.hide();
		} else {
			span.hide();
			comp.show();
		}

	}
};
