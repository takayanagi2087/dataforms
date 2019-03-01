/**
 * @fileOverview {@link EnumGroupQueryForm}クラスを記述したファイルです。
 */

/**
 * @class EnumGroupQueryForm
 *
 * @extends QueryForm
 */
EnumGroupQueryForm = createSubclass("EnumGroupQueryForm", {}, "QueryForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
EnumGroupQueryForm.prototype.attach = function() {
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
EnumGroupQueryForm.prototype.exportInitData = function() {
	var thisForm = this;
	currentPage.confirm(null, MessagesUtil.getMessage("message.dexportAsInitialDataConfirm"), function() {
		thisForm.submit("export", function(data) {
			currentPage.alert(null, data.result);
		});
	});
};
