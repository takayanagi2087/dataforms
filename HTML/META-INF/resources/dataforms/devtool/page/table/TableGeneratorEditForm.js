/**
 * @fileOverview {@link TableGeneratorEditForm}クラスを記述したファイルです。
 */

/**
 * @class TableGeneratorEditForm
 *
 * @extends EditForm
 */
TableGeneratorEditForm = createSubclass("TableGeneratorEditForm", {}, "EditForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
TableGeneratorEditForm.prototype.attach = function() {
	EditForm.prototype.attach.call(this);
	var thisForm = this;
	var tbl = this.getComponent("fieldList");
	this.find("#allErrorButton").click(function() {
		thisForm.find("[id$='\\.overwriteMode']").each(function() {
			$(this).val("error");
		});
	});
	this.find("#allSkipButton").click(function() {
		thisForm.find("[id$='\\.overwriteMode']").each(function() {
			$(this).val("skip");
		});
	});
	this.find("#allForceOverwriteButton").click(function() {
		thisForm.find("[id$='\\.overwriteMode']").each(function() {
			$(this).val("force");
		});
	});
	this.find("#errorSkipButton").click(function() {
		logger.log("errorSkipButton");
		thisForm.find("[id$='\\.fieldClassName'].errorField").each(function() {
			logger.log("error field=" + $(this).attr("id"));
			tbl.getSameRowField($(this), "overwriteMode").val("skip");
		});
	});
	this.find("#errorForceButton").click(function() {
		logger.log("errorSkipButton");
		thisForm.find("[id$='\\.fieldClassName'].errorField").each(function() {
			logger.log("error field=" + $(this).attr("id"));
			tbl.getSameRowField($(this), "overwriteMode").val("force");
		});
	});
	this.find("#printButton").click(function() {
		thisForm.print();
	});

};


/**
 * 編集モードにします。
 * <pre>
 * 各フィールドを編集可能状態にします。
 * </pre>
 */
TableGeneratorEditForm.prototype.toEditMode = function() {
	EditForm.prototype.toEditMode.call(this);
	// 更新時にはクラス名を編集できなくする。
	var funcsel = this.getComponent("functionSelect");
	var pkgname = this.getComponent("packageName");
	var cnfield = this.getComponent("tableClassName");
	if (this.saveMode == "new") {
		funcsel.lock(false);
		pkgname.lock(false);
		cnfield.lock(false);
	} else {
		funcsel.lock(true);
		pkgname.lock(true);
		cnfield.lock(true);
	}
	var tbl = this.getComponent("fieldList");
	var n = tbl.find("tbody>tr").length;
	for (var i = 0; i < n; i++) {
		var spkg = tbl.getComponent("fieldList[" + i + "].superPackageName");
		var scls = tbl.getComponent("fieldList[" + i + "].superSimpleClassName");
		var ovm = tbl.getComponent("fieldList[" + i + "].overwriteMode");
		logger.log("spkg.id=" + spkg.id);
		logger.log("scls.id=" + scls.id);
		var flg = this.find("#" + this.selectorEscape("fieldList[" + i + "].isDataformsField"));
		logger.log("flg=" + flg.val());
		if (flg.val() == "0") {
			logger.log("unlock");
			spkg.lock(false);
			scls.lock(false);
			ovm.get().show();
		} else {
			logger.log("lock");
			spkg.lock(true);
			scls.lock(true);
			ovm.get().hide();
			var len = tbl.getComponent("fieldList[" + i + "].fieldLength");
			if (len.get().val().length > 0) {
				len.lock(false);
			} else {
				len.lock(true);
			}
		}
	}
	this.find("#allErrorButton").prop("disabled", false);
	this.find("#allSkipButton").prop("disabled", false);
	this.find("#allForceOverwriteButton").prop("disabled", false);
	this.find("#errorSkipButton").prop("disabled", false);
	this.find("#errorForceButton").prop("disabled", false);
};

/**
 * 確認モードにします。
 * <pre>
 * 各フィールドを編集可能状態にします。
 * </pre>
 */
TableGeneratorEditForm.prototype.toConfirmMode = function() {
	EditForm.prototype.toConfirmMode.call(this);
	this.find("#allErrorButton").prop("disabled", true);
	this.find("#allSkipButton").prop("disabled", true);
	this.find("#allForceOverwriteButton").prop("disabled", true);
	this.find("#errorSkipButton").prop("disabled", true);
	this.find("#errorForceButton").prop("disabled", true);
};


/**
 * 計算イベント処理を行います。
 * <pre>
 * 計算イベントフィールドが更新された場合、このメソッドが呼び出されます。
 * データ入力時の自動計算が必要な場合このメソッドをオーバーライドしてください。
 * </pre>
 * @param {jQuery} element イベントが発生した要素。
 */
TableGeneratorEditForm.prototype.onCalc = function(element) {
	if (element != null) {
		logger.log("element.id=" + element.attr("id"));
		var id = element.attr("id");
		if (id.indexOf("packageName") >= 0 || id.indexOf("fieldClassName") >= 0) {
			this.onCalcClass(element);
		} else if (id.indexOf("superPackageName") > 0 || id.indexOf("superSimpleClassName") >= 0) {
			this.onCalcSuperClass(element);
		}
	}
};


/**
 * フィールドクラスの計算イベント処理を行います。
 * @param {jQuery} element イベントが発生した要素。
 */
TableGeneratorEditForm.prototype.onCalcClass = function(element) {
	var tbl = this.getComponent("fieldList");
	var p = tbl.getSameRowField(element, "packageName").val();
	var c = tbl.getSameRowField(element, "fieldClassName").val();
	if (p.length > 0 && c.length > 0) {
		var classname = p + "." + c;
		logger.log("classname=" + classname);
		var method = this.getSyncServerMethod("getFieldClassInfo");
		var ret = method.execute("classname=" + classname);
		if (ret.status == ServerMethod.SUCCESS) {
			var dfflg = tbl.getSameRowField(element, "isDataformsField");
			var len = tbl.getSameRowField(element, "fieldLength");
			var cmnt = tbl.getSameRowField(element, "comment");
			var bpkg = tbl.getSameRowField(element, "superPackageName");
			var bcls = tbl.getSameRowField(element, "superSimpleClassName");
			var owm = tbl.getSameRowField(element, "overwriteMode");
			dfflg.val(ret.result.isDataformsField);
			if (ret.result.isDataformsField == "1") {
				if (ret.result.fieldLength != null && ret.result.fieldLength.length > 0) {
					len.val(ret.result.fieldLength);
					tbl.getComponent(len.attr("id")).lock(false);
				} else {
					len.val("");
					tbl.getComponent(len.attr("id")).lock(true);
				}
				bpkg.val(ret.result.superClassPackage);
				bcls.val(ret.result.superClassSimpleName);
				cmnt.val(ret.result.fieldComment);
				tbl.getComponent(bpkg.attr("id")).lock(true);
				tbl.getComponent(bcls.attr("id")).lock(true);
				owm.hide();
			} else {
				if (ret.result.fieldLength != null && ret.result.fieldLength.length > 0) {
					if (len.val().length == 0) {
						len.val(ret.result.fieldLength);
					}
				}
				if (ret.result.superClassPackage != null) {
					bpkg.val(ret.result.superClassPackage);
				}
				if (ret.result.superClassSimpleName != null) {
					bcls.val(ret.result.superClassSimpleName);
				}
				if (ret.result.fieldComment != null && ret.result.fieldComment.length > 0) {
					if (cmnt.val().length == 0) {
						cmnt.val(ret.result.fieldComment);
					}
				}
				tbl.getComponent(bpkg.attr("id")).lock(false);
				tbl.getComponent(bcls.attr("id")).lock(false);
				owm.show();
			}
		}
	}
};


/**
 * 親クラスの計算イベント処理を行います。
 * @param {jQuery} element イベントが発生した要素。
 */
TableGeneratorEditForm.prototype.onCalcSuperClass = function(element) {
	var tbl = this.getComponent("fieldList");
	var p = tbl.getSameRowField(element, "superPackageName").val();
	var c = tbl.getSameRowField(element, "superSimpleClassName").val();
	if (p.length > 0 && c.length > 0) {
		var classname = p + "." + c;
		logger.log("super classname=" + classname);
		var method = this.getSyncServerMethod("getSuperFieldClassInfo");
		var ret = method.execute("superclassname=" + classname);
		if (ret.status == ServerMethod.SUCCESS) {
			var len = tbl.getSameRowField(element, "fieldLength");
			if (len.val().length == 0) {
				len.val(ret.result.fieldLength);
			}
		}
	}
};

/**
 * テーブル定義書を作成します。
 */
TableGeneratorEditForm.prototype.print = function() {
	var thisForm = this;
	thisForm.parent.resetErrorStatus();
	thisForm.submitForDownload("print");
};