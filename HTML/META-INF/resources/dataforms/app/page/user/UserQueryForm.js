/**
 * @fileOverview {@link UserQueryForm}クラスを記述したファイルです。
 */

/**
 * @class UserQueryForm
 *
 * @extends QueryForm
 */
UserQueryForm = createSubclass("UserQueryForm", {}, "QueryForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
UserQueryForm.prototype.attach = function() {
	QueryForm.prototype.attach.call(this);
	logger.log("userInfo=" + JSON.stringify(currentPage.userInfo));
	if (currentPage.userInfo.userLevel == "developer") {
		var thisForm = this;
		this.find("#exportInitDataButton").click(function() {
			thisForm.exportInitData()
		});
	} else {
		this.find("#exportInitDataButton").remove();
	}
};

/**
 * データのエクスポートを行います。
 */
UserQueryForm.prototype.exportInitData = function() {
	var thisForm = this;
	currentPage.confirm(null, MessagesUtil.getMessage("message.dexportAsInitialDataConfirm"), function() {
		thisForm.submit("export", function(data) {
			currentPage.alert(null, data.result);
		});
	});
};
