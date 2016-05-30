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
	var method = this.getSyncServerMethod("getMenu");
	var ret = method.execute();
	if (ret.status == ServerMethod.SUCCESS) {
		this.menu.menuGroupList = ret.result.menuGroupList;
		this.menu.update();
	}
};

