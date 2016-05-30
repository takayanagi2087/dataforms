/**
 * ユーザ情報フォーム.
 */

createSubclass("LoginInfoForm", {}, "Form");

/**
 * ログイン状態の更新.
 */
LoginInfoForm.prototype.update = function() {
	var method = this.getSyncServerMethod("getUserInfo");
	var ret = method.execute();
	if (ret.status == ServerMethod.SUCCESS) {
		if (ret.result.loginId != null) {
			this.setFormData(ret.result);
			this.find("#underLoginDiv").show();
			this.find("#dontLoginDiv").hide();
		} else {
			this.find("#underLoginDiv").hide();
			this.find("#dontLoginDiv").show();
		}
	}
}

/**
 * ログアウト処理.
 */
LoginInfoForm.prototype.logout = function() {
//	window.location.href = currentPage.contextPath + "/dataforms/app/page/top/TopPage." + currentPage.pageExt;
	currentPage.toTopPage();
	var method = this.getSyncServerMethod("logout");
	var ret = method.execute();
	if (ret.status == ServerMethod.SUCCESS) {
		this.update();
		currentPage.getComponent("sideMenuForm").update();
	}
}


/**
 * ページの各エレメントとの対応付け.
 */
LoginInfoForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var form = this;
	var thisPage = this.parent;
	form.find('#loginButton').click(function () {
		thisPage.showLoginDialog();
	});
	form.find('#regUserButton').click(function() {
		window.location.href = thisPage.contextPath + "/app/page/user/RegistUserPage." + currentPage.pageExt;
	});
	form.find('#logoutButton').click(function() {
		form.logout();
	});
	this.update();
};
