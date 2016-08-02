/**
 * @fileOverview {@link RestoreForm}クラスを記述したファイルです。
 */

/**
 * @class RestoreForm
 *
 * @extends Form
 */
RestoreForm = createSubclass("RestoreForm", {}, "Form");


/**
 * HTMLエレメントとの対応付けを行います。
 */
RestoreForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var thisForm = this;
	this.find("#restoreButton").click(function() {
		thisForm.restore();
	});
};

/**
 * リストア処理を行います。
 */
RestoreForm.prototype.restore = function() {
	var thisForm = this;
	if (this.validate()) {
		var systemname = MessagesUtil.getMessage("message.systemname");
		var msg = MessagesUtil.getMessage("message.restoreconfirm");
		currentPage.confirm(systemname, msg, function() {
			thisForm.submit("restore", function(r) {
				thisForm.parent.resetErrorStatus();
				if (r.status == ServerMethod.INVALID) {
					currentPage.setErrorInfo(thisForm.getValidationResult(r), thisForm);
				} else {
					currentPage.alert(systemname, r.result);
				}
			});
		});
	}
};

