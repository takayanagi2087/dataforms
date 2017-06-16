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
		this.find("#exportButton").click(function() {
			thisForm.exportData()
		});
	} else {
		this.find("#exportButton").remove();
	}
};

/**
 * データのエクスポートを行います。
 */
UserQueryForm.prototype.exportData = function() {
	var thisForm = this;
	this.submit("export", function(data) {
		currentPage.alert(null, data.result);
	});
};
