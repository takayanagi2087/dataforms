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
	this.find("#exportDataButton").click(function() {
		thisForm.exportData();
	});
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
	if (confirm(MessagesUtil.getMessage("message.initTableConfirm"))) {
		var clsname = this.find("#className").html();
		var p = "className=" + clsname;
		var method = this.getSyncServerMethod("initTable");
		var result = method.execute(p);
		if (result.status == ServerMethod.SUCCESS) {
			this.setFormData(result.result);
			this.updateTableInfo(result.result);
		}
	}
};

/**
 * DBテーブルの削除を行います。
 */
TableInfoForm.prototype.dropTable = function() {
	if (confirm(MessagesUtil.getMessage("message.dropTableConfirm"))) {
		var clsname = this.find("#className").html();
		var p = "className=" + clsname;
		var method = this.getSyncServerMethod("dropTable");
		var result = method.execute(p);
		if (result.status == ServerMethod.SUCCESS) {
			this.setFormData(result.result);
			this.updateTableInfo(result.result);
		}
	}
};

/**
 * DBテーブルの再構築を行います。
 */
TableInfoForm.prototype.updateTable = function() {
	if (confirm(MessagesUtil.getMessage("message.updateTableConfirm"))) {
		var clsname = this.find("#className").html();
		var p = "className=" + clsname;
		var method = this.getSyncServerMethod("updateTable");
		var result = method.execute(p);
		if (result.status == ServerMethod.SUCCESS) {
			this.setFormData(result.result);
			this.updateTableInfo(result.result);
		}
	}
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
		alert(MessagesUtil.getMessage("message.exportInitialDataResult", path));
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