/**
 * @fileOverview {@link ExportWebResourceQueryResultForm}クラスを記述したファイルです。
 */

/**
 * @class ExportWebResourceQueryResultForm
 *
 * @extends QueryResultForm
 */
ExportWebResourceQueryResultForm = createSubclass("ExportWebResourceQueryResultForm", {}, "QueryResultForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
ExportWebResourceQueryResultForm.prototype.attach = function() {
	var thisForm = this;
	QueryResultForm.prototype.attach.call(this);
	this.find("#selAll").click(function() {
		logger.log("selAll=" + $(this).prop("checked"));
		thisForm.selAll($(this));
	});
	this.find("#exportButton").click(function() {
		thisForm.exportWebRes();
	});
	this.find("#selectNotExportedButton").click(function() {
		thisForm.selectedNotExportedFile();
	});
};

/**
 * 各フィールドのバリデーションを行います。
 * @returns バリデーション結果。
 */
ExportWebResourceQueryResultForm.prototype.validateFields = function() {
	var ret = QueryResultForm.prototype.validateFields.call(this);
	if (ret.length == 0) {
		if (this.find("#forceOverwrite").prop("checked") == false) {
			var result = this.formData.queryResult;
			for (var i = 0; i < result.length; i++) {
				var selid = "queryResult[" + i + "].sel";
				if (this.find("#" + this.selectorEscape(selid)).prop("checked")) {
					var efid = "queryResult[" + i + "].existFlag";
					if (this.find("#" + this.selectorEscape(efid)).val() == "1") {
						ret.push(new ValidationError("queryResult[" + i + "].fileName",
							MessagesUtil.getMessage("error.alreadyexported", result[i].fileName)));
					}
				}
			}
		}
	}
	return ret;
};


/**
 * エクスポートされていないファイルを選択します。
 */
ExportWebResourceQueryResultForm.prototype.selectedNotExportedFile = function () {
	var result = this.formData.queryResult;
	for (var i = 0; i < result.length; i++) {
		var selid = "queryResult[" + i + "].sel";
		var efid = "queryResult[" + i + "].existFlag";
		if (this.find("#" + this.selectorEscape(efid)).val() != "1") {
			this.find("#" + this.selectorEscape(selid)).prop("checked", true);
		}
	}
};

/**
 * Webリソースをエクスポートします。
 */
ExportWebResourceQueryResultForm.prototype.exportWebRes = function() {
	this.parent.resetErrorStatus();
	if (this.validate()) {
		var p = this.get().serialize();
		logger.log("p=" + p);
		this.submit("exportWebResource", function(r) {
			if (r.status == ServerMethod.SUCCESS) {
				alert(r.result);
			}
		});
	}
};

/**
 * 全選択チェックボックスのイベント処理を行います。
 * @param ck {jQuery} チェックボックスのjQueryオブジェクト。
 *
 */
ExportWebResourceQueryResultForm.prototype.selAll = function(ck) {
	if (ck.prop("checked")) {
		this.find("[id$='.sel']").prop("checked", true);
	} else {
		this.find("[id$='.sel']").prop("checked", false);
	}
};