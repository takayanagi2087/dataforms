package dataforms.field.common;

import java.util.Map;

import dataforms.field.sqltype.VarcharField;
import dataforms.validator.ZipCodeValidator;

/**
 * 郵便番号フィールドクラス。
 * <pre>
 * ajaxzip3を使用すると郵便番号入力時に住所を取得します。
 * 郵便番号から住所を取得した居場合、/frame/jslib.htmlに以下の行を追加してください。
 *
 * &lt;script src=&quot;https://ajaxzip3.github.io/ajaxzip3.js&quot; charset=&quot;UTF-8&quot;&gt;&lt;/script&gt;
 * </pre>
 */
public class ZipCodeField extends VarcharField {

	/**
	 * フィールドコメント.
	 */
	private static final String COMMENT = "郵便番号";
	/**
	 * フィールド長.
	 */
	private static final int LENGTH = 8;
	/**
	 * 住所フィールドID.
	 */
	private String addressFieldId = null;


	/**
	 * 住所フィールドID2.
	 */
	private String addressFieldId2 = null;

	/**
	 * 住所フィールドID2.
	 */
	private String addressFieldId3 = null;

	/**
	 * コンストラクタ。
	 */
	public ZipCodeField() {
		super(null, 8);
		this.setComment(COMMENT);
		this.addValidator(new ZipCodeValidator());
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID.
	 */
	public ZipCodeField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new ZipCodeValidator());
	}

	/**
	 * 住所フィールドのIDを取得します。
	 * @return 住所フィールドのID。
	 */
	public String getAddressFieldId() {
		return addressFieldId;
	}

	/**
	 * 住所フィールドのIDを設定します。
	 * @param addressFieldId 住所フィールドID。
	 * @return 設定したフィールド。
	 */
	public ZipCodeField setAddressFieldId(final String addressFieldId) {
		this.addressFieldId = addressFieldId;
		return this;
	}

	/**
	 * 二番目のアドレスフィールドIDを取得します。
	 * @return 二番目のアドレスフィールドID。
	 */
	public String getAddressFieldId2() {
		return addressFieldId2;
	}


	/**
	 * 二番目のアドレスフィールドIDを設定します。
	 * @param addressFieldId2 二番目のアドレスフィールドID。
	 */
	public void setAddressFieldId2(final String addressFieldId2) {
		this.addressFieldId2 = addressFieldId2;
	}

	/**
	 * 三番目のアドレスフィールドIDを取得します。
	 * @return 三番目のアドレスフィールドID。
	 */
	public String getAddressFieldId3() {
		return addressFieldId3;
	}


	/**
	 * 三番目のアドレスフィールドIDを設定します。
	 * @param addressFieldId3 三番目のアドレスフィールドID。
	 */
	public void setAddressFieldId3(final String addressFieldId3) {
		this.addressFieldId3 = addressFieldId3;
	}


	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> m = super.getClassInfo();
		m.put("addressFieldId", this.getAddressFieldId());
		m.put("addressFieldId2", this.getAddressFieldId2());
		m.put("addressFieldId3", this.getAddressFieldId3());
		return m;
	}

}
