/**
 * @fileOverview {@link TableInfoForm}クラスを記述したファイルです。
 */

/**
 * @class TableInfoForm
 * テーブル情報フォームクラス。
 * <pre>
 * テーブル情報を表示するためのフォームです。
 * </pre>
 * @extends Form
 */
TableInfoForm = createSubclass("TableInfoForm", {}, "Form");

/**
 * HTMLエレメントフォームとの対応付けを行います。
 *
 */
TableInfoForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var thisForm = this;
	this.find("#initTableButton").click(function() {
		thisForm.initTable();
	});
	this.find("#updateTableButton").click(function() {
		thisForm.updateTable();
	});
	this.find("#DataButton").click(function() {
		thisForm.importData();
	});
	this.find("#dropTableButton").click(function() {
		thisForm.dropTable();
	});
	/*
	this.find("#exportDataButton").click(function() {
		thisForm.exportData();
	});*/
	this.find("#closeButton").click(function() {
		thisForm.parent.close();
	});
};

/**
 * フォームデータの設定を行います。
 * @param {Object} formData フォームデータ。
 */
TableInfoForm.prototype.setFormData = function(formData) {
	Form.prototype.setFormData.call(this, formData);
	if (formData.tableExists) {
		this.find("#dropTableButton").prop("disabled", false);
		this.find("#exportDataButton").prop("disabled", false);
	} else {
		this.find("#dropTableButton").prop("disabled", true);
		this.find("#exportDataButton").prop("disabled", true);
	}
};

/**
 * クエリ結果リストのクラス情報を更新します。
 * @param {Object} result クラス情報。
 */
TableInfoForm.prototype.updateTableInfo = function(result) {
	var page = this.parent.parent;
	var resultForm = page.getComponent("queryResultForm");
	resultForm.updateTableInfo(result);
};


/**
 * DBテーブルの初期化を行います。
 */
TableInfoForm.prototype.initTable = function() {
	var thisForm = this;
	var systemName = MessagesUtil.getMessage("message.systemname");
	currentPage.confirm(systemName, MessagesUtil.getMessage("message.initTableConfirm"), function() {
		var clsname = thisForm.find("#className").html();
		var p = "className=" + clsname;
		var method = thisForm.getSyncServerMethod("initTable");
		var result = method.execute(p);
		if (result.status == ServerMethod.SUCCESS) {
			thisForm.setFormData(result.result);
			thisForm.updateTableInfo(result.result);
		}
	});
};

/**
 * DBテーブルの削除を行います。
 */
TableInfoForm.prototype.dropTable = function() {
	var thisForm = this;
	var systemName = MessagesUtil.getMessage("message.systemname");
	currentPage.confirm(systemName, MessagesUtil.getMessage("message.dropTableConfirm"), function() {
		var clsname = thisForm.find("#className").html();
		var p = "className=" + clsname;
		var method = thisForm.getSyncServerMethod("dropTable");
		var result = method.execute(p);
		if (result.status == ServerMethod.SUCCESS) {
			thisForm.setFormData(result.result);
			thisForm.updateTableInfo(result.result);
		}
	});
};

/**
 * DBテーブルの再構築を行います。
 */
TableInfoForm.prototype.updateTable = function() {
	var thisForm = this;
	var systemName = MessagesUtil.getMessage("message.systemname");
	currentPage.confirm(systemName, MessagesUtil.getMessage("message.updateTableConfirm"), function() {
		var clsname = thisForm.find("#className").html();
		var p = "className=" + clsname;
		var method = thisForm.getSyncServerMethod("updateTable");
		var result = method.execute(p);
		if (result.status == ServerMethod.SUCCESS) {
			thisForm.setFormData(result.result);
			thisForm.updateTableInfo(result.result);
		}
	});
};

/**
 * データをエクスポートします。
 */
TableInfoForm.prototype.exportData = function() {
	var clsname = this.find("#className").html();
	var p = "className=" + clsname;
	var method = this.getSyncServerMethod("exportData");
	var result = method.execute(p);
	if (result.status == ServerMethod.SUCCESS) {
		this.setFormData(result.result);
		this.updateTableInfo(result.result);
		var path = result.result.exportDataPath;
		var systemName = MessagesUtil.getMessage("message.systemname");
		currentpage.alert(MessagesUtil.getMessage(systemname, "message.exportInitialDataResult", path));
	}
};

/**
 * データをインポートします。
 */
TableInfoForm.prototype.importData = function() {
	var clsname = this.find("#className").html();
	var p = "className=" + clsname;
	var method = this.getSyncServerMethod("importData");
	var result = method.execute(p);
	if (result.status == ServerMethod.SUCCESS) {
		this.setFormData(result.result);
		this.updateTableInfo(result.result);
	}
};