/**
 * @fileOverview  {@link MessagesUtil}クラスを記述したファイルです。
 */


/**
 * @class MessagesUtil
 * メッセージユーティリティ。
 * <pre>
 * 各種メッセージを取得します。
 * ClientMessages.properties, &lt;Page&gt;.properties中のメッセージは初期化時にサーバから取得します。
 * Messages.propertiesのメッセージは、要求された時にサーバから取得します。
 * </pre>
 */
var MessagesUtil = function() {
};

/**
 * キーとメッセージ対応表を記録するマップです。
 */
MessagesUtil.messageMap = null;


/**
 * ClientMessages.properties, &lt;Page&gt;.propertiesに指定されたメッセージを読み込みます。
 * @param {Object} メッセージマップ。
 */
MessagesUtil.init = function(map) {
	MessagesUtil.messageMap = map;
};

/**
 * メッセージを取得します。
 * <pre>
 * initメソッドで読み込んでいないメッセージは、ajaxで取得します。
 * </pre>
 * @param {String} key メッセージキー.
 * @param {String} [arg] メッセージ引数(複数指定可).
 * @returns {String} メッセージ.
 */
MessagesUtil.getMessage = function() {
	var msg = MessagesUtil.messageMap[arguments[0]];
	if (msg == null) {
		// 存在しないメッセージはajaxで取得する.
		var method = new SyncServerMethod("getServerMessage");
		var r = method.execute("key=" + arguments[0]);
		this.messageMap[arguments[0]] = r.result;
		msg = r.result;
	}
	for (var i = 1; i < arguments.length; i++) {
		var rex = RegExp('\\{' + (i - 1) +  '\\}');
		if (msg.match(rex)) {
			var mid = RegExp.lastMatch;
			msg = msg.replace(mid, arguments[i]);
		}
	}
	return msg;
};

