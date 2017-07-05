/**
 * ユーザ情報フォーム.
 */

createSubclass("LoginInfoForm", {}, "Form");

/**
 * ログイン状態の更新.
 */
LoginInfoForm.prototype.update = function() {
	var thisForm = this;
	var method = this.getAsyncServerMethod("getUserInfo");
	method.execute("", function (ret) {
		if (ret.status == ServerMethod.SUCCESS) {
			if (ret.result.loginId != null) {
				thisForm.setFormData(ret.result);
				thisForm.find("#underLoginDiv").show();
				thisForm.find("#dontLoginDiv").hide();
			} else {
				thisForm.find("#underLoginDiv").hide();
				thisForm.find("#dontLoginDiv").show();
			}
		}
	});
};

/**
 * ログアウト処理.
 */
LoginInfoForm.prototype.logout = function() {
	var thisForm = this;
	var method = this.getAsyncServerMethod("logout");
	method.execute("", function(ret) {
		currentPage.toTopPage();
	});
};


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
