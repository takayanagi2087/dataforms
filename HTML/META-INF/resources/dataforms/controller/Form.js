/**
 * @fileOverview  * {@link Form}クラスを記述したファイルです。
 */

/**
 * @class Form
 *
 * フォームクラス。
 * <pre>
 * HTMLのformタグに対応するクラスです。
 * </pre>
 * @extends WebComponent
 * @prop {Object} formData setFormDataメソッドで設定されたフォームデータ。
 * @prop {Boolian} clientValidation クライアントでバリデーションを行うかどうかを示すフラグ。
 * @prop {Array} fields サーバから送信された情報を元に作成されたフィールドjsクラスのインスタンスを記録した配列。
 * @prop {Array} htmlTables サーバから送信された情報を元に作成されたHTMLテーブルjsクラスのインスタンスを記録した配列。
 */
Form = createSubclass("Form", {clientValidation : true, fields: [], htmlTables : []}, "WebComponent");

/**
 * 各フィールドの初期化を行います。
 * @param {Array} fieldList フィールドリスト。
 */
Form.prototype.initField = function(fieldList) {
	for (var i = 0; i < fieldList.length; i++) {
		var f = fieldList[i];
		var field = this.newInstance(f);
		field.init();
		this.fields[i] = field;
	}
};


/**
 * HTMLテーブルの初期化を行います。
 * @param {Array} htmlTableList HTMLテーブルリスト.
 */
Form.prototype.initHtmlTable = function(htmlTableList) {
	for (var i = 0; i < htmlTableList.length; i++) {
		var t = htmlTableList[i];
		var tbl = this.newInstance(t);
		tbl.init(this.formData);
		this.htmlTables[i] = tbl;
	}
};

/**
 * 必須マークを設定します。
 * <pre>
 * 入力フィールドにRequiredValidatorが設定されていた場合、対応するラベルに対して、必須マークを付加します。
 * </pre>
 */
Form.prototype.setRequiredMark = function() {
	for (var i = 0; i <this.fields.length; i++) {
		var f = this.fields[i];
//		var o = this.find('#' + this.selectorEscape(f.id));
		var o = f.get();
		if (o.length > 0) {
			if (f.isRequired()) {
				var e = f.getLabelElement();
				e.addClass("requiredFieldLabel");
			}
		}
	}
	for (var i = 0; i < this.htmlTables.length; i++) {
		this.htmlTables[i].setRequiredMark();
	}
};

/**
 * フォームデータを設定します。
 * @param {Object} formData フォームデータ。
 */
Form.prototype.setFormData = function(formData) {
	this.formData = formData;
	for (var i = 0; i < this.htmlTables.length; i++) {
		var tbl = this.htmlTables[i];
		tbl.clear();
		tbl.setFormData(formData);
	}
	for (var i = 0; i <this.fields.length; i++) {
		var field = this.fields[i];
		field.setValue(formData[field.id]);
	}
	this.onCalc(null);
};

/**
 * フォームのフィールドに対して、値を設定します。
 * @param {String} fid フィールドID。
 * @param {String} value 値。
 */
Form.prototype.setFieldValue = function(fid, value) {
	if (this.isHtmlTableElementId(fid)) {
		var tblid = this.getHtmlTableId(fid);
		var colid = this.getHtmlTableColumnId(fid);
		var table = this.getComponent(tblid);
		var field = table.getComponent(colid);
		var f = $.extend(true, {}, field);
		f.id = fid;
		f.setValue(value);
	} else {
		var field = this.getComponent(fid);
		if (field != null) {
			field.setValue(value);
		}
	}
};

/**
 * フォームを初期化します。
 */
Form.prototype.init = function() {
	WebComponent.prototype.init.call(this);
	this.initField(this.fieldList);
	this.initHtmlTable(this.htmlTableList);
};

/**
 * HTMLエレメントへの対応付けを行います。
 * <pre>
 * 以下のボタンが存在した場合、イベント処理を登録します。
 * #newButton ... 「新規登録」ボタンの処理.
 * </pre>
 */
Form.prototype.attach = function() {
	if (this.htmlPath != null) {
		var fhtml = $("<div>" + this.additionalHtmlText + "</div>").find("form").html();
		var obj = this.get();
		if (obj.length !=0) {
			this.get().html(fhtml);
		} else {
			obj = $("#" + this.selectorEscape(this.id));
			obj.html(fhtml);
			this.parentDivId = obj.parents("div[id]:first").attr("id");
		}
	} else {
		var obj = $("#" + this.selectorEscape(this.id));
		this.parentDivId = obj.parents("div[id]:first").attr("id");
	}
	WebComponent.prototype.attach.call(this);
	var thisForm = this;
	this.find("#newButton").prop("disabled" , false);
	this.find("#newButton").click(function() {
		thisForm.newData();
		return false;
	});
	this.setRequiredMark();
	this.setFormData(this.formData);
	this.onCalc(null);
};

/**
 * フォームのリセットを行います。
 */
Form.prototype.reset = function() {
	this.setFormData(this.formData);
	this.parent.resetErrorStatus();
};


/**
 * フォームのサブミットを行います。
 * <pre>
 * 通常、フォームのserialize()で得られたパラメータを$.ajax()に渡して、
 * 指定されたサーバメソッドを呼び出し、その結果を取得します。
 * フォーム中に&lt;input type=&quot;file&quot; ...&gt;が存在する場合、
 * 自動的にenctypeをmultipart/form-dataに設定してPOSTします。結果は非表示の
 * &lt;iframe&gt;に受け取り、jsonを解析後、funcに渡します。
 * </pre>
 *
 * @param {String} method 送信先のメソッド.
 * @param {Function} func 応答処理 function(data)。
 */
Form.prototype.submit = function(method, func) {
	var f = this.get();
	var m = new AsyncServerMethod(this.getUniqId() + "." + method);
	m.submit(f, function(data) {
		func(data);
	});
};

/**
 * ファイルフィールド以外をsubmitします。
 * <pre>
 * 時間のかかるファイルPOSTを回避する目的で使用します。
 * サーバメソッドではファイルの処理はできません。
 * フォームのserialize()で得られたパラメータを$.ajax()に渡して、
 * 指定されたサーバメソッドを呼び出し、その結果を取得します。
 * </pre>
 * @param {String} method メソッド名。
 * @param {Function} func 応答処理 function(data)。
 */
Form.prototype.submitWithoutFile = function(method, func) {
	var form = this;
	var m = new AsyncServerMethod(this.getUniqId() + "." + method);
	m.submitWithoutFile(form.get(), func);
};

/**
 * ファイルフィールドも含めてsubmitします。
 * <pre>
 * 自動的にenctypeをmultipart/form-dataに設定してPOSTします。結果は非表示の
 * &lt;iframe&gt;に受け取り、jsonを解析後、funcに渡します。
 * </pre>
 * @param {String} method メソッド。
 * @param {Function} func 応答処理 function(data)。
 */
Form.prototype.submitWithFile = function(method, func) {
	var form = this;
	var m = new AsyncServerMethod(this.getUniqId() + "." + method);
	m.submitWithFile(form.get(), func);
};

/**
 * ダウンロード用のsubmitを行います。
 * <pre>
 * BinaryResponseを返すサーバメソッドに対して、submitを行い、結果をダウンロードします。
 * サーバメソッドがエラーの発生などの要因でJSONを返した場合、funcにその内容を渡します。
 * funcを省略した場合、エラーメッセージが返されたという前提でalertを表示する応答処理を実行します。
 * </pre>
 * @param {String} method メソッド名。
 * @param {Function} func 応答処理 function(data)。
 */
Form.prototype.submitForDownload = function(method, func) {
	var form = this;
	document.cookie = "downloaded=0";
	var m = new AsyncServerMethod(this.getUniqId() + "." + method);
	if (func == null) {
		m.submitWithFile(form.get(), function(ret) {
			var systemName = MessagesUtil.getMessage("message.systemname");
			currentPage.alert(systemName, ret.result);
		});
	} else {
		m.submitWithFile(form.get(), func);
	}
//	logger.log("isLocked=" + window.currentPage.isLocked());
	var tm = setInterval(function() {
		logger.log("isLocked=" + window.currentPage.isLocked());
		if (!window.currentPage.isLocked()) {
			logger.log("stop timer.");
			clearInterval(tm);
		} else {
			var dl = form.getCookie("downloaded");
			logger.log("dl=" + dl);
			if ("1" == dl) {
				window.currentPage.unlock();
				logger.log("stop timer by downloaded cookie");
				clearInterval(tm);
				document.cookie = "downloaded=0";
			}
		}
	}, 500);
};

/**
 * 各フィールドの検証を行います。
 * @returns {Array} 検証結果リスト。
 */
Form.prototype.validateFields = function() {
	var result = [];
	for (var i = 0; i < this.fields.length; i++) {
		var field = this.fields[i];
		var e = field.validate();
		if (e != null) {
			result.push(e);
		}
	}
	for (var i = 0; i < this.htmlTables.length; i++) {
		var r = this.htmlTables[i].validate();
		for (var n = 0; n < r.length; n++) {
			result.push(r[n]);
		}
	}
	return result;
};


/**
 * フォーム単位のバリデーションを行います。
 * <pre>
 * フォーム単位のバリデーションが必要な場合、実装してください。
 * </pre>
 * @returns {Array} バリデーションの結果。
 */
Form.prototype.validateForm = function() {
	var ret = [];
	return ret;
};

/**
 * フォームの検証を行います。
 * <pre>
 * クライアントバリデーションが有効に設定されている場合、このメソッドで各フィールドのバリデーションを行います。
 * 項目関連チェックなどの独自のバリデーションを実装する場合、このメソッドをオーバーライドします。
 * </pre>
 */
Form.prototype.validate = function() {
	if (!this.clientValidation) {
		return true;
	}
	var result = this.validateFields();
	if (result.length == 0) {
		result = this.validateForm();
	}
	if (result.length) {
		this.parent.setErrorInfo(result, this);
		return false;
	} else {
		return true;
	}
};

/**
 * サーバのバリデーション情報を取得します。
 * <pre>
 * サーバから受信したバリデーション結果をメッセージの配列に変換します。
 * サーバから返されるバリデーション情報はフィールドIDとフィールド名が{0}となったメッセージです。
 * フィールドIDから対応するフィールドのラベルを検索し、{0}に置き換えたメッセージを作成します。
 *
 * </pre>
 * @param {Array} result サーバのバリデーション結果。
 * @returns {Array} バリデーション結果。
 */
Form.prototype.getValidationResult = function(result) {
	var errors = [];
	for (var i = 0; i < result.result.length; i++) {
		var comp = this.getComponent(result.result[i].fieldId);
		var msg = result.result[i].message.replace("{0}", comp.label);
		errors.push(new ValidationError(result.result[i].fieldId, msg));
	}
	return errors;
};

/**
 * 各フィールドのロック/ロック解除を行います。
 * @param {Boolean} lk ロックする場合true.
 */
Form.prototype.lockFields = function(lk) {
	var result = [];
	for (var i = 0; i < this.fields.length; i++) {
		var field = this.fields[i];
		field.lock(lk);
	}
	for (var i = 0; i < this.htmlTables.length; i++) {
		this.htmlTables[i].lockFields(lk);
	}
};

/**
 * 「新規登録」ボタンの応答処理です。
 */
Form.prototype.newData = function() {
	this.parent.toEditMode();
	var editForm = this.parent.getComponent("editForm");
	editForm.newData();
};


/**
 * 計算イベント処理を行います。
 * <pre>
 * 計算イベントフィールドが更新された場合、このメソッドが呼び出されます。
 * データ入力時の自動計算が必要な場合このメソッドをオーバーライドしてください。
 * </pre>
 * @param {jQuery} element イベントが発生した要素。初期表示等特定フィールドが要因でない場合はnullが設定されます。
 *
 */
Form.prototype.onCalc = function(element) {
};


/**
 * フォーム中のデータをクリアします。
 */
Form.prototype.clearData = function() {
	var data = {};
	for (var i = 0; i < this.htmlTables.length; i++) {
		var tbl = this.htmlTables[i];
		data[tbl.id] = [];
	}
	this.setFormData(data);
};

