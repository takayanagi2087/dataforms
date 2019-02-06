/**
 * @fileOverview {@link Field}クラスを記述したファイルです。
 */

/**
 * @class Field
 * フィールドクラス。
 * <pre>
 * 各種フィールドの基底クラスです。
 * </pre>
 * @extends WebComponent
 */
Field = createSubclass("Field", {}, "WebComponent");




/**
 * バリデータの初期化を行います。
 * @param {Array} vlist バリデータリスト.
 */
Field.prototype.initValidator = function(vlist) {
	this.validators = [];
	for (var i = 0; i < vlist.length; i++) {
		var v = vlist[i];
		var validator = this.newInstance(v);
		validator.init();
		this.validators[i] = validator;
	}
};

/**
 * Autocomplete等の際にサーバに送信するパラメータを取得します。
 * @returns {String} サーバに送信するQueryString形式のパラメータ。
 */
Field.prototype.getAjaxParameter = function() {
	if (this.ajaxParameter == "FORM") {
		return this.getParentForm().get().serialize() + "&currentFieldId=" + this.id;
	} else {
		return this.id + "=" + this.get().val() + "&currentFieldId=" + this.id;
	}
};

/**
 * フィールドのラベルエレメントを取得します。
 * @returns {jQuery} フィールドに対応したラベルを取得します。
 */
Field.prototype.getLabelElement = function() {
	var label = null;
//	var tag = this.parent.find('#' + this.selectorEscape(this.labelId));
	var tag = this.parent.find("label[for='" + this.id +"']");
	if (tag.length > 0) {
		// ラベルのIDが指定されていた場合の処理.
		label = tag;
	} else {
		// フィールドに対応したラベルを探す処理.
		var l = this.parent.find('#' + this.selectorEscape(this.id)).prev('label:last');
		if (l.length > 0) {
			label = l;
		} else {
			l = this.parent.find('#' + this.selectorEscape(this.id)).parent(':last').prev().find('label:first');
			if (l.length > 0) {
				label = l;
			} else {
				l = this.parent.find('#' + this.selectorEscape(this.id)).parent(':last').prev();
				label = l;
			}
		}
		if (label == null || label.length == 0) {
			var l = this.parent.find('#' + this.selectorEscape(this.id + "[0]")).parent(':last').prev('label:last');
			if (l.length > 0) {
				label = l;
			} else {
				l = this.parent.find('#' + this.selectorEscape(this.id + "[0]")).parent(':last').parent(':last').prev().find('label:first');
				if (l.length > 0) {
					label = l;
				} else {
					l = this.parent.find('#' + this.selectorEscape(this.id + "[0]")).parent(':last').parent(':last').prev();
					label = l;
				}
			}
		}
	}
	return label;
};


/**
 * フィールドのラベル文字列を取得する。
 * @return {String} フィールドに対応したラベル文字列。
 */
Field.prototype.getLabel = function() {
	var labelstr = this.id;
	var label = this.getLabelElement();
	if (label != null) {
		labelstr = label.html();
	}
	return labelstr;
};

/**
 * 初期化処理を行います。
 */
Field.prototype.init = function() {
	WebComponent.prototype.init.call(this);
	this.initValidator(this.validatorList);
};

/**
 * 属性のバックアップをとります。
 */
Field.prototype.backupStyle = function() {
	var comp = this.get();
	comp.prop("readonly-bak", comp.prop("readonly"));
};

/**
 * 関連データの更新後に呼び出されるメソッドです。
 */
Field.prototype.onUpdateRelationField = function() {
	logger.log("onUpdateRelationField");
};

/**
 * 関連データの取得を行います。
 * <pre>
 * 関連データ取得がONの場合フィールド値の更新時にこの処理が呼ばれます。
 * 各フィールドのgetRelationDataメソッドが呼び出され、その結果を親フォームに設定します。
 * </pre>
 */
Field.prototype.getRelationData = function() {
	var thisField = this;
	var m = this.getAsyncServerMethod("getRelationData");
	var form = this.getParentForm();
	var param = this.getAjaxParameter();
	m.execute(param, function(ret) {
		if (ret.status == ServerMethod.SUCCESS) {
			for (var k in ret.result) {
				if (Array.isArray(ret.result[k])) {
					var t = form.getComponent(k);
					if (t != null && typeof t.setTableData == "function") {
						t.setTableData(ret.result[k]);
					}
				} else {
					form.setFieldValue(k, ret.result[k]);
				}
			}
		}
		thisField.onUpdateRelationField();
	});
};


/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 関連データ取得がtrueの場合、blurイベントに関連データ取得処理を登録します。
 * Autocompleteが有効な場合、Autocompleteの設定を行います。
 * 計算イベント発生フィールドの場合、changeイベントで計算処理を呼び出すように設定します。
 * </pre>
 */
Field.prototype.attach = function() {
	var thisField = this;
	WebComponent.prototype.attach.call(this);
	if (this.label == null) {
		this.label = this.getLabel();
	}
	// nameが指定されていない場合nameにidを設定.
	var comp = this.parent.find('#' + this.selectorEscape(this.id));
	if (comp.attr("name") == null) {
		comp.attr("name", comp.attr("id"));
	}
	this.backupStyle();
	if (this.relationDataAcquisition) {
		if (this.relationDataEvent == "BLUR") {
			comp.blur(function() {
				thisField.id = $(this).attr("id")
				thisField.getRelationData();
			});
		} else {
			comp.change(function() {
				thisField.id = $(this).attr("id")
				thisField.getRelationData();
			});
		}
	}
	thisField.get().prop("fieldObject", this);
	if (this.autocomplete) {
		this.setAutocomplete();
	}
	if (this.calcEventField) {
		comp.change(function() {
			var form = thisField.getParentForm();
			form.onCalc($(this));
		});
	}
	if (this.readonly) {
		this.lock(true);
	}
};

/**
 * INPUTまたはSELECTタグへ値を設定します。
 * @param {jQuery} comp 値を設定するコンポーネント。
 * @param {String} value 値.
 */
Field.prototype.setInputValue = function(comp, value) {
	comp.val(value);
};


/**
 * SPAN等の表示用タグへ値を設定します。
 * @param {jQuery} comp 値を設定するコンポーネント。
 * @param {String} value 値。
 */
Field.prototype.setTextValue = function(comp, value) {
	if (value == null) {
		comp.text("");
	} else {
		comp.text(value);
	}
};

/**
 * 値を設定します。
 * @param {String} value 設定値。
 */
Field.prototype.setValue = function(value) {
	var comp = this.get();
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	if ("INPUT" == tag || "TEXTAREA" == tag || "SELECT" == tag) {
		this.setInputValue(comp, value);
		if ("SELECT" == tag) {
			if (this.readonly) {
				var v = this.find("option:selected").text()
				var span = this.addSpan(comp);
				span.text(v);
			}
		}
	} else {
		this.setTextValue(comp, value);
	}
};

/**
 * 値を取得します。
 * @return {String} 値。
 */
Field.prototype.getValue = function() {
	var comp = this.get();
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	if ("INPUT" == tag || "TEXTAREA" == tag || "SELECT" == tag) {
		return comp.val();
	} else {
		return comp.text();
	}
};



/**
 * フィールドの検証を行ないます。
 * <pre>
 * 各フィールドのバリデータを呼び出します。
 * 追加のチェックが必要な場合、このメソッドをオーバーライドします。
 * </pre>
 * @returns {ValidationError} 検証結果。問題が発生しなければnullを返します。
 */
Field.prototype.validate = function() {
	var val = this.getValue();
	this.value = val;
	if (this.validators != null) {
		for (var i = 0; i < this.validators.length; i++) {
			var v = this.validators[i];
			if (v.validate(val) == false) {
				var msg = v.getMessage(this.label);
				return new ValidationError(this.id, msg);
			}
		}
	}
	return null;
};

/**
 * 指定されたバリデータを呼び出します。
 * <pre>
 * Fieldに登録されていないバリデータを呼び出すときに使用します。
 * </pre>
 * @param {FieldValidator} v 呼び出すバリデータ。
 * @returns {ValidationError} 検証結果。正常な場合null。
 */
Field.prototype.performValidator = function(v) {
	var val = this.parent.find('#' + this.selectorEscape(this.id)).val();
	this.value = val;
	if (v.validate(val) == false) {
		var msg = v.getMessage(this.label);
		return new ValidationError(this.id, msg);
	}
	return null;
};

/**
 * データ確認用のspanタグを作成します。
 * <pre>
 * checkbox,raido,select等のフィールドをロックした場合、元々のコントロールを隠し
 * &lt;span&gt;タグで値を表示します。
 * </pre>
 * @param {jQuery} comp 対応するコンポーネント。
 * @returns {jQuery} 作成したspan要素。
 */
Field.prototype.addSpan = function(comp) {
	var spanid = this.id + "_span";
	var span = this.parent.find("#" + this.selectorEscape(spanid));
	if (span.length == 0) {
		comp.after("<span id='" + spanid + "' class='selectSpan'></span>");
		span = this.parent.find("#" + this.selectorEscape(spanid));
	}
	return span;
};

/**
 * テキストボックスをロックします。
 * @param {Boolean} lk ロックする場合true。
 */
Field.prototype.lockTextbox = function(lk) {
	var comp = this.get();
	if (lk) {
		comp.prop("readonly", true);
		comp.css("border-style", "none");
		comp.css("outline", "none");
		comp.addClass("lockedTextbox");
	} else {
		comp.prop("readonly", comp.prop("readonly-bak"));
		comp.css("border-style", "");
		comp.css("outline", "");
		comp.removeClass("lockedTextbox");
	}
};

/**
 * ラジオボタンをロックします。
 * @param {Boolean} lk ロックする場合true。
 */
Field.prototype.lockRadio = function(lk) {
	var comp = this.get();
	var block = comp.parent("span");
	var v = "";
	comp.each(function() {
		if ($(this).prop("checked")) {
			v = $(this).val();
		}
	});
	var span = this.addSpan(block);
	if (lk) {
		block.hide();
		span.show();
		this.setTextValue(span, v);
	} else {
		block.show();
		span.hide();
	}
};

/**
 * チェックボックスをロックします。
 * @param {Boolean} lk ロックする場合true。
 */
Field.prototype.lockCheckbox = function(lk) {
	var comp = this.get();
	var block = comp.parent('span');
	var v = [];
	comp.each(function() {
		if ($(this).prop("checked")) {
			v.push($(this).val());
		}
	});
	var span = this.addSpan(block);
	if (lk) {
		block.hide();
		span.show();
		this.setTextValue(span, v);
	} else {
		block.show();
		span.hide();
	}
};

/**
 * ファイルフィールドをロックします。
 * @param {Boolean} lk ロックする場合true。
 */
Field.prototype.lockFile = function(lk) {
	var comp = this.get();
	var span = this.addSpan(comp);
	var check = this.parent.find("#" + this.selectorEscape(this.id + "_ck"));
	var fnlink = this.parent.find("#" + this.selectorEscape(this.id + "_link"));
	if (lk) {
		check.hide();
		check.next("label:first").hide();
		comp.hide();
		if (comp.val().length > 0) {
			comp.next("a:first").hide();
		}
		span.show();
		this.setTextValue(span, comp.val());
	} else {
		var v = fnlink.attr("href");
		if (v != null && v.length > 0) {
			check.show();
			check.next("label:first").show();
		} else {
			check.hide();
			check.next("label:first").hide();
		}
		comp.show();
		comp.next("a:first").show();
		span.hide();
	}
};

/**
 * SELECTをロックします。
 * @param {Boolean} lk ロックする場合true。
 */
Field.prototype.lockSelect = function(lk) {
	var comp = this.get();
	var v = this.find("option:selected").text();
	var span = this.addSpan(comp);
	if (lk) {
		comp.hide();
		span.show();
		span.text(v);
	} else {
		comp.show();
		span.hide();
	}
};

/**
 * フィールドのロック/ロック解除を行ないます。
 * @param {Boolean} lk ロックする場合true。
 */
Field.prototype.lock = function(lk) {
	if (lk == false && this.readonly == true) {
		return;
	}
	var comp = this.get();
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	if (("INPUT" == tag && (type.toLowerCase() == "text"
		|| type.toLowerCase() == "password"
		|| type.toLowerCase() == "email"
		|| type.toLowerCase() == "url"
		|| type.toLowerCase() == "tel"
		|| type.toLowerCase() == "number"
		)) || "TEXTAREA" == tag) {
		this.lockTextbox(lk);
	} else if ("INPUT" == tag && type.toLowerCase() == "radio") {
		this.lockRadio(lk);
	} else if ("INPUT" == tag && type.toLowerCase() == "checkbox") {
		this.lockCheckbox(lk);
	} else if ("INPUT" == tag && type.toLowerCase() == "file") {
		this.lockFile(lk);
	} else if ("SELECT" == tag) {
		this.lockSelect(lk);
	}
}

/**
 * 必須入力フィールドがどうかを判定します。
 * <pre>
 * 指定されたフィールドがテキスト入力フィールとまたはSELECTでかつ
 * 必須バリデータが設定されている場合trueを返します。
 * </pre>
 * @param {jQuery} [el] フィールドに対応するjQueryオブジェクト。HTMLテーブル中のフィールドの場合指定します。
 */
Field.prototype.isRequired = function(el) {
	if (el == null) {
		el = this.get();
	}
	var tag = el.prop("tagName");
	var type = el.prop("type");
	if ("INPUT" == tag || "TEXTAREA" == tag || "SELECT" == tag) {
		for (var i = 0; i < this.validatorList.length; i++) {
			var v = this.newInstance(this.validatorList[i]);
			if (v instanceof RequiredValidator) {
				return true;
			}
		}
	}
	return false;
}

/**
 * 入力補完データを取得します。
 * <pre>
 * 対応するフィールドのgetAutocompleteSourceを呼び出し、入力候補を取得します。
 * </pre>
 * @param {res} リスト設定メソッド.
 */
Field.prototype.getSource = function(res) {
	var method = this.getAsyncServerMethod("getAutocompleteSource");
	var param = this.getAjaxParameter();
	method.execute(param, function(ret) {
		if (ret.status == ServerMethod.SUCCESS) {
			res(ret.result);
		}
	});
};

/**
 * autocompleteの選択時の処理を記述します。
 */
Field.prototype.onAutocompleteSelected = function() {
	if (!this.relationDataAcquisition) {
		this.onUpdateRelationField();
	}
};

/**
 * Autocompleteの設定を行います。
 */
Field.prototype.setAutocomplete = function() {
	var thisField = this;
	this.get().autocomplete({
		search:function(event, ui) {
			thisField.id = event.target.id;
		},
		source:function(req, res) {
			var list = thisField.getSource(res);
		},
		select: function(event, ui) {
			for (var k in ui.item) {
				if (k == "label") {
				} else if (k == "value") {
				} else {
					if (thisField.parent instanceof Form) {
						thisField.parent.setFieldValue(k, ui.item[k]);
					} else if (thisField.parent instanceof HtmlTable) {
						thisField.parent.parent.setFieldValue(k, ui.item[k]);
					}
				}
			}
			if (thisField.calcEventField) {
				var form = thisField.getParentForm();
				form.onCalc($(event.target));
			}
			thisField.onAutocompleteSelected();
		}
	});
};

/**
 * フィールドのIDが変更された場合呼び出されます。
 * <pre>
 * EditableHtmlTableの場合、行追加/削除やソートによって、各フィールドのIDが変化します。
 * DateField等はIDが変わると、datepickerの動作がおかしくなるため、このメソッドで、
 * datepickerを再作成を行います。
 * </pre>
 */
Field.prototype.onIdChange = function() {
};



/**
 * フィールドが削除された場合呼び出されます。
 * <pre>
 * HtmlTableのデータ更新時やEdiatableHtmlTableの行削除時等フィールドが、削除されるタイミングで
 * 呼び出されます。通常イベントハンドラ等の削除を行います。
 * </pre>
 */
Field.prototype.onDestroy = function() {
	this.get().unbind();
};

/**
 * マップ中の対応フィールドを比較します。
 * @param a {Object} 比較対象のマップ1。
 * @param b {Object} 比較対象のマップ2。
 * @returns {Number} 比較結果。
 */
Field.prototype.comp = function(a, b) {
	var ret = 0;
	if (a[this.id].toString() < b[this.id].toString()) {
		ret = -1;
	} else if (a[this.id].toString() > b[this.id].toString()) {
		ret = 1;
	}
	return ret;
};

/**
 * 同一フォーム内の別フィールドまたは同一テーブルの同一行内のフィールドを取得します。
 * @param {String} id 取得するフィールドID。
 * @returns {Field} フィールド。
 */
Field.prototype.getNearField = function(id) {
	var tableId = this.getHtmlTableId(this.id);
	if (tableId != null) {
		return this.parent.getSameRowField(this, id);
	} else {
		return this.parent.getComponent(id);
	}
};
