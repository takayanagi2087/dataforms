/**
 * @fileOverview {@link FuncEditForm}クラスを記述したファイルです。
 */

/**
 * @class FuncEditForm
 *
 * @extends EditForm
 */
FuncEditForm = createSubclass("FuncEditForm", {}, "EditForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
FuncEditForm.prototype.attach = function() {
	EditForm.prototype.attach.call(this);
};


/**
 * フォームに対してデータを設定します。
 * @param data データ。
 */
FuncEditForm.prototype.setFormData = function(data) {
	EditForm.prototype.setFormData.call(this, data);
};

/**
 * 編集モードにします。
 */
FuncEditForm.prototype.toEditMode = function() {
	EditForm.prototype.toEditMode.call(this);
	var thisForm = this;
	var table = this.getComponent("funcTable");
	this.find("[id$='\\.funcPath']").each(function() {
		var f = thisForm.getComponent($(this).attr("id"));
		logger.log("id=" + f.id + ":" + $(this).val());
		if ($(this).val().indexOf("/dataforms") == 0) {
			var field = table.getSameRowField($(this), "funcName");
			var namef = thisForm.getComponent(field.attr("id"));
			f.lock(true);
			namef.lock(true);
		}
	});
};

