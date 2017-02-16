/**
 * @fileOverview  {@link EditForm}クラスを記述したファイルです。
 */


/**
 * @class EditForm
 *
 * データ編集フォーム。
 * <pre>
 * データ編集を行うフォームです。
 * </pre>
 * @extends Form
 *
 * @prop {string} mode "edit"(フォームが編集可能な状態)または"confirm"(フォーム全体が編集不可の状態)の値を取ります。
 * @prop {string} saveMode "new"(新規データの入力中)または"update"(既存データの編集中)の値を取ります。
 *
 */
EditForm = createSubclass("EditForm", {mode:"edit", saveMode: "new"}, "Form");

/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 以下のボタンが存在した場合、イベント処理を登録します。
 * #confirmButton ... 「確認」ボタンの処理.
 * #saveButton ... 「保存」ボタンの処理.
 * #resetButton ... 「リセット」ボタンの処理.
 * #deleteButton ... 「削除」ボタンの処理.
 * #backButton ... 「戻る」ボタンの処理.
 * </pre>
 */
EditForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var form = this;
	form.find('#confirmButton').click(function () {
		form.confirm();
		return false;
	});
	form.find('#saveButton').click(function () {
		form.save();
		return false;
	});
	form.find('#resetButton').click(function() {
		form.reset();
		return false;
	});
	form.find('#deleteButton').click(function() {
		form.del();
		return false;
	});
	form.find('#backButton').click(function() {
		if (form.parent.isBrowserBackEnabled()) {
			history.back();
		} else {
			form.back();
		}
		return false;
	});
	form.toEditMode();
};


/**
 * 更新モードの時にPKをロックします。
 * 
 */
EditForm.prototype.lockPkFields = function() {
	var lk = false;
	if (this.saveMode == "new") {
		lk = false;
	} else {
		lk = true;
	}
	if (this.pkFieldIdList != null) {
		for (var i = 0; i < this.pkFieldIdList.length; i++) {
			var f = this.getComponent(this.pkFieldIdList[i]);
			if (f != null) {
				f.lock(lk);
			}
		}
	}
};

/**
 * 編集モードにします。
 * <pre>
 * 各フィールドを編集可能状態にします。
 * </pre>
 */
EditForm.prototype.toEditMode = function() {
	this.mode = "edit";
	this.lockFields(false);
	var cb = this.find("#confirmButton");
	if (cb.size() > 0) {
		// 確認画面があるパターン.
		cb.show();
		this.find("#resetButton").show();
		this.find("#saveButton").hide();
	} else {
		// いきなり保存するパターン.
		this.find("#saveButton").show();
	}
	this.lockPkFields();
};

/**
 * 確認モードにします。
 * <pre>
 * 各フィールドを編集不可状態にします。
 * </pre>
 */
EditForm.prototype.toConfirmMode = function() {
	this.mode = "confirm";
	this.lockFields(true);
	var cb = this.find('#confirmButton');
	if (cb.size() > 0) {
		// 確認画面があるパターン.
		cb.hide();
		this.find('#resetButton').hide();
		this.find('#saveButton').show();
	} else {
		// いきなり保存するパターン.
		this.find('#saveButton').show();
	}
};

/**
 * 新規登録モードにします。
 * <pre>
 * 対応するEditFormのgetNewDataを呼び出し、初期データを取得します。
 * 各フィールドに取得データを設定し、編集モードにします。
 * </pre>
 */
EditForm.prototype.newData = function() {
	var title = MessagesUtil.getMessage("message.editformtitle.new");
	this.find("#editFormTitle").text(title);
	var form = this;
	form.submitWithoutFile("getNewData", function(result) {
		form.parent.resetErrorStatus();
		if (result.status == ServerMethod.SUCCESS) {
			form.saveMode = "new";
			form.setFormData(result.result);
			form.toEditMode();
			form.parent.pushEditModeStatus();
		} else {
			form.parent.setErrorInfo(form.getValidationResult(result), form);
		}
	});
};

/**
 * 更新登録モードにします。
 * <pre>
 * 対応するEditFormのgetDataを呼び出し、編集対象データを取得します。
 * 各フィールドに取得データを設定し、編集モードにします。
 * </pre>
 */
EditForm.prototype.updateData = function(qs) {
	var title = MessagesUtil.getMessage("message.editformtitle.update");
	this.find("#editFormTitle").text(title);
	var form = this;
	if (qs == null) {
		form.submitWithoutFile("getData", function(result) {
			form.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				form.parent.toEditMode();
				form.saveMode = "update";
				form.setFormData(result.result);
				form.toEditMode();
				form.parent.pushEditModeStatus();
			} else {
				form.parent.setErrorInfo(form.getValidationResult(result), form);
			}
		});
	} else {
		form.find("#dfMethod").remove();
		var data = qs;
		logger.log("qs=" + data);
		var method = new AsyncServerMethod("editForm.getDataByQueryFormCondition");
		method.execute(data, function(result) {
			form.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				form.parent.toEditMode();
				form.saveMode = "update";
				form.setFormData(result.result);
				form.toEditMode();
				form.parent.pushEditModeStatus();
			} else {
				form.parent.setErrorInfo(form.getValidationResult(result), form);
			}
		});
	}
};


/**
 * データを参照登録します。
 * <pre>
 * 対応するEditFormのgetReferDataを呼び出し、参照対象データを取得します。
 * 各フィールドに取得データを設定し、編集モードにします。
 * </pre>
 *
 */
EditForm.prototype.referData = function() {
	var title = MessagesUtil.getMessage("message.editformtitle.refer");
	this.find("#editFormTitle").text(title);
	var form = this;
	form.submitWithoutFile("getReferData", function(result) {
		form.parent.resetErrorStatus();
		if (result.status == ServerMethod.SUCCESS) {
			form.parent.toEditMode();
			form.saveMode = "new";
			form.setFormData(result.result);
			form.toEditMode();
			form.parent.pushEditModeStatus();
		} else {
			form.parent.setErrorInfo(form.getValidationResult(result), form);
		}
	});
};

/**
 * データを参照します。
 * <pre>
 * 対応するEditFormのgetDataを呼び出し、参照対象データを取得します。
 * 各フィールドに取得データを設定し、参照モードにします。
 * </pre>
 */
EditForm.prototype.viewData = function() {
	var title = MessagesUtil.getMessage("message.editformtitle.view");
	this.find("#editFormTitle").text(title);
	var form = this;
	form.submitWithoutFile("getData", function(result) {
		form.parent.resetErrorStatus();
		if (result.status == ServerMethod.SUCCESS) {
			form.parent.toEditMode();
			form.saveMode = "update";
			form.setFormData(result.result);
			form.lockFields(true);
			form.find("#confirmButton").hide();
			form.find("#saveButton").hide();
			form.find("#resetButton").hide();
			form.parent.pushConfirmModeStatus();
		} else {
			form.parent.setErrorInfo(form.getValidationResult(result), form);
		}
	});
};


/**
 * 各フィールドにデータを設定します。
 * <pre>
 * 新規モードの場合、削除ボタンを隠します。
 * </pre>
 * @param {Object} data フォームデータ.
 *
 */
EditForm.prototype.setFormData = function(data) {
	Form.prototype.setFormData.call(this, data);
	if (this.saveMode == "new") {
		this.find('#deleteButton').hide();
	} else {
		this.find('#deleteButton').show();
	}
};


/**
 * 確認ボタンのイベント処理を行います。
 * <pre>
 * 対応するEditFormのconfirmメソッドを呼び出し、問題なければ確認モードに遷移します。
 * ファイルアップロードフィールドはサーバーに送信されません。
 * </pre>
 */
EditForm.prototype.confirm = function() {
	var form = this;
	if (form.validate()) {
		form.submitWithoutFile("confirm", function(result) {
			form.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				form.toConfirmMode();
				form.parent.pushConfirmModeStatus();
			} else {
				form.parent.setErrorInfo(form.getValidationResult(result), form);
			}
		});
	}
};

/**
 * 保存や削除後の画面状態遷移を行います。
 */
EditForm.prototype.changeStateForAfterUpdate = function() {
	var form = this;
	var queryForm = form.parent.getComponent("queryForm");
	var resultForm = form.parent.getComponent("queryResultForm");
	if (queryForm == null && resultForm == null) {
		form.clearData();
		currentPage.toTopPage();
	} else {
		form.clearData();
		form.toEditMode();
		form.parent.toQueryMode();
		var queryResultForm = form.parent.getComponent("queryResultForm");
		if (queryResultForm != null) {
			queryResultForm.changePage();
		}
	}
};


/**
 * 保存ボタンのイベント処理を行います。
 * <pre>
 * 対応するEditFormのsaveメソッドを呼び出し、保存処理を行います。
 * ファイルアップロードフィールドもサーバーに送信されます。
 * </pre>
 */
EditForm.prototype.save = function() {
	var form = this;
	if (form.validate()) {
		this.find("#saveMode").val(this.saveMode);
		form.submit("save", function(result) {
			form.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				if (result.result != null && result.result.length > 0) {
					currentPage.alert(null, result.result, function() {
						form.changeStateForAfterUpdate();
					});
				} else {
					form.changeStateForAfterUpdate();
				}
			} else {
				form.parent.setErrorInfo(form.getValidationResult(result), form);
			}
		});
	}
};

/**
 * 保存ボタンのイベント処理を行います。
 * <pre>
 * 対応するEditFormのdeleteメソッドを呼び出し、保存処理を行います。
 * </pre>
 */
EditForm.prototype.del = function() {
	var systemName = MessagesUtil.getMessage("message.systemname");
	var msg = MessagesUtil.getMessage("message.deleteconfirm");
	var form = this;
	currentPage.confirm(systemName, msg, function() {
		form.submit("delete", function(result) {
			form.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				if (result.result != null && result.result.length > 0) {
					currentPage.alert(null, result.result, function() {
						form.changeStateForAfterUpdate();
					});
				} else {
					form.changeStateForAfterUpdate();
				}
			}
		});
	});
};

/**
 * 戻るボタンのイベント処理を行います。
 */
EditForm.prototype.back = function() {
	if (this.mode == "edit") {
		this.clearData();
		if (!this.parent.toQueryMode()) {
			currentPage.toTopPage();
		}
	} else if (this.mode == "confirm") {
		this.toEditMode();
	}
};
