/**
 * @fileOverview {@link Page}クラスを記述したファイルです。
 */

/**
 * @class Page
 *
 * ページクラス。
 * <pre>
 * 1つのHTMLファイル(ページ)に対応するクラスです。
 * ページは複数のFormと複数のDialogを持つことができます。
 * </pre>
 * @extends DataForms
 * @prop {Object} userInfo ログインしているユーザ情報。形式は以下の通り。<br/>
 * <pre>
 * {
 *  "userId":1000, // ユーザID
 *  "loginId":"developer", // ログインID.
 *  "mailAddress":"", // メールアドレス
 *  "userName":"developer", // ユーザ名
 *  "userLevel":"developer", // ユーザレベル
 *   ... 以下ユーザに設定されたユーザ属性
 *  }
 * </pre>
 */
Page = createSubclass("Page", {id: "mainDiv"}, "DataForms");


/**
 * 現在のページインスタンスです。
 */
currentPage = null;

/**
 * consoleのコピーです。
 * <pre>
 * javascriptのログ出力はconsoleでなくloggerを使用してください。
 * loggerを使用すると、web.xmlのclient-log-levelの設定で、ログレベルの変更が可能です。
 * </pre>
 */
logger = null;

/**
 * loggerの設定をおこないます。
 * <pre>
 * consoleをサポートしないブラウザの場合、consoleオブジェクトを作成します。
 * consoleオブジェクトをloggerオブジェクトに代入します。
 * 基本的にjavascriptのログ出力はloggerを使用してください。
 * </pre>
 */
Page.prototype.configureLogger = function () {
	if (typeof window.console === "undefined") {
		window.console = {};
	}
	if (typeof window.console.log !== "function") {
		window.console.log = function() {};
	}
	if (typeof window.console.info !== "function") {
		window.console.info = function() {};
	}
	if (typeof window.console.warn !== "function") {
		window.console.warn = function() {};
	}
	if (typeof window.console.error !== "function") {
		window.console.error = function() {};
	}
	if (typeof window.console.dir !== "function") {
		window.console.dir = function() {};
	}
	window.logger = window.console;
};

/**
 * logレベルを設定します。
 * <pre>
 * クライアントログレベルに応じて、loggerオブジェクトのメソッドを無効化します。
 * </pre>
 */
Page.prototype.configureLogLevel = function () {
	if (this.clientLogLevel == "info" || this.clientLogLevel == "warn" || this.clientLogLevel == "error" || this.clientLogLevel == "none") {
		logger.log = function() {};
	}
	if (this.clientLogLevel == "warn" || this.clientLogLevel == "error" || this.clientLogLevel == "none") {
		logger.info = function() {};
	}
	if (this.clientLogLevel == "error" || this.clientLogLevel == "none") {
		logger.warn = function() {};
	}
	if (this.clientLogLevel == "none") {
		logger.error = function() {};
	}
	logger.info("clientLogLevel=" + this.clientLogLevel);
	logger.log("debug log.");
	logger.info("info log.");
	logger.warn("warn log.");
	logger.error("error log.");
	logger.info("browser type=" + this.getBrowserType());
};


/**
 * 同期サーバメソッドを取得する.
 * <pre>
 * Page中のWebMethodはメソッド名のみ(WebComponentのidは無い形式)のためオーバーライドしています。
 * </pre>
 * @param {String} method メソッド名.
 * @returns {SyncServerMethod} 同期サーバメソッド.
 */
Page.prototype.getSyncServerMethod = function(method) {
	return new SyncServerMethod(method);
};

/**
 * mainDivをFrame.htmlに指定されたフレームで囲む。
 * @param frame {jQuery} フレームのbodyの内容のjQueryオブジェクト。
 * @param frameMainDiv {jQuery}　フレームのmainDivのjQueryオブジェクト。
 * @param mainDiv {jQuery} ページのmainDivのjQueryオブジェクト。
 */
Page.prototype.wrapFrame = function(frame, frameMainDiv, mainDiv) {
	var p = mainDiv.parent();
	var div = mainDiv;
	frameMainDiv.prevAll().each(function() {
		div.before($(this));
		div = $(this);
	});
	div = mainDiv;
	frameMainDiv.nextAll().each(function() {
		div.after($(this));
		div = $(this);
	});
	var fparent = frameMainDiv.parent();
	logger.log("id=" + fparent.attr("id"));
	if (fparent.attr("id") != "rootDiv") {
		logger.log("parent.length=" + fparent.length);
		logger.log("id=" + fparent.attr("id"));
		fparent.empty();
		p.children().wrapAll(fparent);
		this.wrapFrame(frame, fparent, mainDiv.parent());
	}

};

/**
 * 各ブロックのレイアウトを行います。
 * <pre>
 * Frame.htmlを取得し、その内容にしたがってページをレイアウトします。
 * </pre>
 */
Page.prototype.layout = function() {
	var frame = $("<div id=\"rootDiv\">" + this.frameBody + "</div>");
	this.wrapFrame(frame, frame.find("#mainDiv"), $("#mainDiv"));
	var head = $("<div>" + this.frameHead + "</div>");
	// ライブラリと競合するタグを削除
	head.find("meta[charset='UTF-8']").remove();
	head.find("meta[charset='utf-8']").remove();
	head.find("link[rel='stylesheet'][href='Frame.css']").remove();
	head.find("link[rel='stylesheet'][href='FramePC.css']").remove();
	head.find("link[rel='stylesheet'][href='FrameTB.css']").remove();
	head.find("link[rel='stylesheet'][href='FrameSP.css']").remove();
	head.find("title").remove();
	// /frame/のパス調整。
	head.find("link[rel='stylesheet'][href*='/frame/']").each(function() {
		var href = $(this).attr("href");
		$(this).attr("href", href.replace(/^.*\/frame\//, currentPage.contextPath + "/frame/"));
	});
	$("head").append(head.html());
	var systemName = MessagesUtil.getMessage("message.systemname");
	if (systemName != null) {
		$('#systemName').html(systemName);
	}
	if (this.pageTitle == null) {
		$('#pageName').html($('title').html());
	} else {
		$('title').html(this.pageTitle);
		$('#pageName').html(this.pageTitle);
	}
	initFrame();
};

/**
 * ダイアログを初期化します。
 * @param {Array} dialogList ダイアログリスト.
 */
Page.prototype.initDialog = function(dialogList) {
	// ダイアログの初期化.
	for (var key in dialogList) {
		var dlgclass = dialogList[key];
		var dlg = this.newInstance(dlgclass);
		dlg.htmlPath = dlgclass.path + ".html";
		dlg.init();
	}
};

/**
 * トップページに遷移します。
 */
Page.prototype.toTopPage = function() {
	window.location.href = this.contextPath + this.topPage;
};

/**
 * ブラウザの戻るボタンでフォームの制御を行うかどうかを返します。
 * @returns {Boolean} Pageクラスではweb.xmlの設定値を返します。
 */
DataForms.prototype.isBrowserBackEnabled = function() {
	if (this.browserBackButton == "disabled") {
		return false;
	} else {
		return true;
	}
};

/**
 * history.pushStateを呼び出すためのメソッドです。
 * <pre>
 * Pageクラスではweb.xmlのbrowser-back-buttonがenabledの場合、
 * history.pushStateを呼び出します。
 * </pre>
 * @param {Object} state 状態。
 * @param {String} title タイトル。
 * @param {String} url タイトル。
 *
 */
Page.prototype.pushState = function(state, title, url) {
	if (this.browserBackButton == "enabled") {
		logger.log("poshstate=" + state);
		history.pushState(state, title, url);
	}
};

/**
 * history.replaceStateを呼び出すためのメソッドです。
 * <pre>
 * Pageクラスではweb.xmlのbrowser-back-buttonがenabledの場合、
 * history.replaceStateを呼び出します。
 * </pre>
 * @param {Object} state 状態。
 * @param {String} title タイトル。
 * @param {String} url タイトル。
 *
 */
Page.prototype.replaceState = function(state, title, url) {
	if (this.browserBackButton == "enabled") {
		logger.log("replacestate=" + state);
		history.replaceState(state, title, url);
	}
};



/**
 * ブラウザの戻るボタン押下時の処理を行います。
 * <pre>
 * web.xmlのbrowser-back-buttonがenabledの場合は呼ばれません。
 * </pre>
 * @param event イベント情報。
 */
Page.prototype.onDisabledBackButton = function(event) {
	history.pushState("disableBack", "disableBack", location.href);
	return;
};

Page.prototype.onBackButton = function(event) {
	logger.log("popstate=" + event.originalEvent.state);
	if (event.originalEvent.state) {
		var editForm = this.getComponent("editForm");
		if (editForm != null) {
			if (editForm.get().is(":visible")) {
				editForm.back();
			}
		}
	}
	return;
};


/**
 * ブラウザの戻るボタンの設定を行います。
 */
Page.prototype.configureBrowserBackButton = function() {
	var thisPage = this;
	logger.log("browserBackButton=" + this.browserBackButton);
	if (this.browserBackButton == "disabled") {
		history.pushState("disableBack", "disableBack", location.href);
		$(window).on("popstate", function(event){
			if (!event.originalEvent.state){
				thisPage.onDisabledBackButton(event);
			}
		});
	} else {
		$(window).on("popstate", function(event){
			thisPage.onBackButton(event);
		});
	}
};



/**
 * ページの初期化処理を行います。
 */
Page.prototype.init = function() {
	DataForms.prototype.init.call(this);
	this.configureLogger();
	logger.debug("queryString=" + window.location.search);
	if (window.location.search != null && window.location.search.length > 1) {
		$.ajaxSetup({
			headers: {
				queryString: window.location.search.substring(1)
			}
		});
	}
	logger.info("language=" + this.getLanguage());
	$.datepicker.setDefaults($.datepicker.regional[this.getLanguage()]);
	var thisPage = this;
	// ページの初期化.
	var method = new AsyncServerMethod("getPageInfo");
	method.execute("", function(result) {
		for (var key in result.result) {
			thisPage[key] = result.result[key];
		}
		thisPage.configureLogLevel();
		thisPage.configureBrowserBackButton();
		//メッセージユーティリティの初期化.
		MessagesUtil.init(thisPage.messageMap);

		if (!thisPage.noFrame) {
			thisPage.layout();
		}
		// 各フォームの初期化
		thisPage.initForm(thisPage.formMap);
		// ダイアログの初期化
		thisPage.initDialog(thisPage.dialogMap);
		// バージョン情報などを表示。
		$("#dataformsVersion").html(thisPage.dataformsVersion);
//		$("#dataformsVendor").html(this.dataformsVendor);
		// クッキーチェック
		if (thisPage.cookieCheck) {
			thisPage.setCookie("cookiecheck", "true");
			var cookiecheck = thisPage.getCookie("cookiecheck");
			logger.log("cookiecheck=" + cookiecheck);
			if (cookiecheck != "true") {
				alert(MessagesUtil.getMessage("error.cookienotsupport"));
			}
			thisPage.setCookie("cookiecheck", "");
		}
		//
		thisPage.attach();
	});
};

/**
 * エレメントとの対応付けを行います。
 */
Page.prototype.attach = function() {
	var thisPage = this;
	DataForms.prototype.attach.call(thisPage);
	$("#showMenuButton").click(function() {
		var menu = $("#menuDiv");
		if (menu.length == 0) {
			// leftbarDivの対応は互換性維持のため
			$("#leftbarDiv").toggle("blind");
		} else {
			menu.toggle("blind");
		}
		return false;
	});
	$("body").click(function() {
		if ($("#showMenuButton").is(":visible")) {
			var menu = $("#menuDiv");
			if (menu.is(":visible")) {
				menu.toggle("blind");
			}
		}
	});
	$(window).resize(function() {
		thisPage.onResize();
	});
};


/**
 * リサイズ時の処理を行います。
 */
Page.prototype.onResize = function() {
	logger.log("onResize");
	$("#menuDiv").css("display", "");
	// leftbarDivの対応は互換性維持のため
	$("#leftbarDiv").css("display", "");
};


/**
 * ログインダイアログを表示します。
 */
Page.prototype.showLoginDialog = function() {
	var dlg = this.getComponent("loginDialog");
	dlg.showModal();
};

/**
 * ページをロックします。
 * <pre>
 * Ajax呼び出し前に呼び出され、画面をロックします。
 * </pre>
 */
Page.prototype.lock = function() {
	$("#lockLayer").css({
		display: 'block',
		width: $(document).width(),
		height: $(document).height()
	});
	$(window).on("resize.lockLayer", function() {
		$('#lockLayer').css({
			width: $(document).width(),
			height: $(document).height()
		});
	});
};

/**
 * ページをアンロックします。
 * <pre>
 * Ajax呼び出しの応答受信後に呼び出され、画面をアンロックします。
 * </pre>
 */
Page.prototype.unlock = function() {
	 $("#lockLayer").css({display: 'none'});
	  $(window).off("resize.lockLayer");
};

/**
 * ページのロック状態を取得します。
 * @return ロックされている場合true。
 */
Page.prototype.isLocked = function() {
	return $("#lockLayer").is(":visible");
};

/**
 * alertの代替えメソッドです。
 *
 * @param {String} title ダイアログタイトル(nullの場合システム名称)。
 * @param {String} msg ダイアログメッセージ。
 * @param {Functuion} func OKボタンが押された際の処理。
 */
Page.prototype.alert = function(title, msg, func) {
	var dlg = this.getComponent("alertDialog");
	if (title == null) {
		dlg.title = MessagesUtil.getMessage("message.systemname");
	} else {
		dlg.title = title;
	}
	dlg.message = msg;
	dlg.okFunc = func;
	dlg.showModal({
		minHeight: 100
	});
};

/**
 * confirmの代替えメソッドです。
 * @param {String} title ダイアログタイトル(nullの場合システム名称)。
 * @param {String} msg メッセージ。
 * @param {Function} okFunc OKボタンのダイアログ。
 * @param {Function} cancelFunc キャンセルボタンのダイアログ。
 */
Page.prototype.confirm = function(title, msg, okFunc, cancelFunc) {
	var dlg = this.getComponent("confirmDialog");
	if (title == null) {
		dlg.title = MessagesUtil.getMessage("message.systemname");
	} else {
		dlg.title = title;
	}
	dlg.message = msg;
	dlg.okFunc = okFunc;
	dlg.cancelFunc = cancelFunc;
	dlg.showModal({
		minHeight: 100
	});
};


/**
 * Microsoft EDGE。
 */
Page.BROWSER_EDGE = "edge";

/**
 *  Microsoft Internet Explorer。
 */
Page.BROWSER_IE = "ie";

/**
 * Google chrome。
 */
Page.BROWSER_CHOROME = "chrome";

/**
 * Safari。
 */
Page.BROWSER_SAFARI = "safari";

/**
 * Firefox。
 */
Page.BROWSER_FIREFOX = "firefox";


/**
 * Opera。
 */
Page.BROWSER_OPERA = "opera";


/**
 * 其の他ブラウザ。
 */
Page.BROWSER_OTHER = "other";


/**
 * ブラウザタイプを取得します。
 */
Page.prototype.getBrowserType = function() {
	var ua = window.navigator.userAgent.toLowerCase();
	logger.log("ua=" + ua);
	if (ua.indexOf("edge") >= 0) {
		return Page.BROWSER_EDGE;
	} else if (ua.indexOf("msie") >= 0 || ua.indexOf("trident") >= 0) {
		return Page.BROWSER_IE;
	} else if (ua.indexOf(" opr") >= 0) {
		return Page.BROWSER_OPERA;
	} else if (ua.indexOf("chrome") >= 0) {
		return Page.BROWSER_CHOROME;
	} else if (ua.indexOf("firefox") >= 0) {
		return Page.BROWSER_FIREFOX;
	} else if (ua.indexOf("safari") >= 0) {
		return Page.BROWSER_SAFARI;
	} else {
		return Page.BROWSER_OTHER;
	}
};


