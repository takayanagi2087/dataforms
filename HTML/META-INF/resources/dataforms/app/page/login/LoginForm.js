/**
 * @fileOverview {@link LoginForm}クラスを記述したファイルです。
 */

/**
 * @class LoginForm
 * ログインフォームクラス。
 * <pre>
 * ユーザIDとパスワードを入力しログイン処理を行います。
 * </pre>
 * @extends Form
 */
LoginForm = createSubclass("LoginForm", {}, "Form");


/**
 * ログイン処理を行います。
 *
 */
LoginForm.prototype.login = function() {
	var form = this;
	if (form.validate()) {
		form.submit("login", function(result) {
			form.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				if (form.parent instanceof Dialog) {
					form.parent.close();
				}
				currentPage.toTopPage();
			} else {
				form.parent.setErrorInfo(form.getValidationResult(result), form);
			}
		});
	}
};

/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 以下のイベント処理を登録します。
 * #loginButton ... ログイン処理。
 * #resetButton ... リセット処理。
 * #closeButton ... 閉じる処理。
 * </pre>
 */
LoginForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var form = this;
	this.find('#loginButton').click(function() {
		form.login();
		return false;
	});
	this.find('#resetButton').click(function() {
		form.reset();
		return false;
	});
};
