/**
 * @fileOverview  {@link MultiRecordEditForm}クラスを記述したファイルです。
 */


/**
 * @class MultiRecordEditForm
 *
 * データ編集フォーム。
 * <pre>
 * データ編集を行うフォームです。
 * </pre>
 * @extends TableUpdateForm
 *
 * @prop {string} mode "edit"(フォームが編集可能な状態)または"confirm"(フォーム全体が編集不可の状態)の値を取ります。
 *
 */
MultiRecordEditForm = createSubclass("MultiRecordEditForm", {mode:"edit", multiRecord: true}, "TableUpdateForm");

/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 以下のボタンが存在した場合、イベント処理を登録します。
 * #confirmButton ... 「確認」ボタンの処理.
 * #saveButton ... 「保存」ボタンの処理.
 * #resetButton ... 「リセット」ボタンの処理.
 * #backButton ... 「戻る」ボタンの処理.
 * </pre>
 */
MultiRecordEditForm.prototype.attach = function() {
	TableUpdateForm.prototype.attach.call(this);
	this.toEditMode();
};

MultiRecordEditForm.prototype.updateData = function(qs) {
	var title = MessagesUtil.getMessage("message.editformtitle.update");
	this.find("#editFormTitle").text(title);
	var form = this;
	form.find("#dfMethod").remove();
	var data = qs;
	logger.log("qs=" + data);
	var method = new AsyncServerMethod("editForm.getDataByQueryFormCondition");
	method.execute(data, function(result) {
		form.parent.resetErrorStatus();
		if (result.status == ServerMethod.SUCCESS) {
			form.parent.toEditMode();
			form.setFormData(result.result);
			form.toEditMode();
			form.parent.pushEditModeStatus();
		} else {
			form.parent.setErrorInfo(form.getValidationResult(result), form);
		}
	});
};