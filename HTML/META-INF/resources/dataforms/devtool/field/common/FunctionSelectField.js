/**
 * @fileOverview {@link FunctionSelectField}クラスを記述したファイルです。
 */

/**
 * @class FunctionSelectField
 * 機能フィールドクラス。
 * <pre>
 * </pre>
 * @extends SingleSelectField
 */
FunctionSelectField = createSubclass("FunctionSelectField", {}, "SingleSelectField");

/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 機能を変更したタイミングで、パッケージ名を設定します。
 * </pre>
 */
FunctionSelectField.prototype.attach = function() {
	SingleSelectField.prototype.attach.call(this);
	var thisField = this;
	this.get().change(function() {
		thisField.setPackageName($(this))
	});
}

FunctionSelectField.prototype.setPackageName = function(jq) {
	var form = this.getParentForm();
	var funcname = jq.val();
	if (funcname != null && funcname.length > 0) {
		var packageName = funcname.replace(/\//g, ".").substr(1);
		if (this.packageOption.length > 0) {
			packageName +=  "." + this.packageOption;
		}
		var id = jq.attr("id");
		logger.log("functionSelectField id=" + id)
		if (this.isHtmlTableElementId(id)) {
			var a = id.split(".");
			form.find("#" + this.selectorEscape(a[0] + "." + this.packageFieldId)).val(packageName);
		} else {
			form.find("#" + this.selectorEscape(this.packageFieldId)).val(packageName);
		}
	}
};
