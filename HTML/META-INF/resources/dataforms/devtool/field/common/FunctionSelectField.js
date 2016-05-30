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
	var thisForm = this.getParentForm();
	this.get().change(function() {
		var funcname = $(this).val();
		if (funcname != null && funcname.length > 0) {
			var packageName = funcname.replace(/\//g, ".").substr(1);
			if (thisField.packageOption.length > 0) {
				packageName +=  "." + thisField.packageOption;
			}
			thisForm.find("#" + thisField.selectorEscape(thisField.packageFieldId)).val(packageName);
		}
	});
}

