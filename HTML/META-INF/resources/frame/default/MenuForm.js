/**
 * メニューフォーム.
 */
createSubclass("MenuForm", {}, "Form");


MenuForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
};

/**
 * メニュー項目を更新する.
 */
MenuForm.prototype.update = function() {
	var thisForm = this;
	var method = this.getAsyncServerMethod("getMenu");
	method.execute("", function(ret) {
		if (ret.status == ServerMethod.SUCCESS) {
			thisForm.menu.menuGroupList = ret.result.menuGroupList;
			thisForm.menu.update();
		}
	});
};

