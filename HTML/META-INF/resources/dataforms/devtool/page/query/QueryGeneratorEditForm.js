/**
 * @fileOverview {@link QueryGeneratorEditForm}クラスを記述したファイルです。
 */

/**
 * @class QueryGeneratorEditForm
 *
 * @extends EditForm
 */
QueryGeneratorEditForm = createSubclass("QueryGeneratorEditForm", {}, "EditForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
QueryGeneratorEditForm.prototype.attach = function() {
	EditForm.prototype.attach.call(this);
	var thisForm = this;
	this.find("#selectAll").click(function() {
		if (thisForm.mode == "confirm") {
			return false;
		}
		var ck = $(this).prop("checked");
		thisForm.find("[id$='selectFieldId']").each(function() {
			$(this).prop("checked", ck);
		});
		thisForm.find("[id$='selectTableClass']").each(function() {
			$(this).prop("checked", ck);
		});
		thisForm.getComponent("selectFieldList").disableDuplicate();
	});
};

/**
 * テーブルクラス入力時のフィールドリスト取得。
 * @param {jQuery} f 更新されたフィールド。 
 */
QueryGeneratorEditForm.prototype.onCalc = function(f) {
	logger.log("onCalc");
	if (f == null) {
		return;
	}
	var thisForm = this;
	EditForm.prototype.onCalc.call(this);
	this.submit("getFieldList", function(r) {
		currentPage.resetErrorStatus();
		if (r.status == ServerMethod.SUCCESS) {
			logger.log("field list=" + JSON.stringify(r.result));
			var ftbl = thisForm.getComponent("selectFieldList");
			ftbl.setTableData(r.result);
		} else if (r.status == ServerMethod.INVALID) {
			currentPage.setErrorInfo(thisForm.getValidationResult(r), thisForm);
		}
	});
};
