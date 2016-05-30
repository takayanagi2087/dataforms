package dataforms.app.dao.user;

import dataforms.app.field.user.LoginIdField;
import dataforms.app.field.user.MailAddressField;
import dataforms.app.field.user.PasswordField;
import dataforms.app.field.user.UserIdField;
import dataforms.app.field.user.UserNameField;
import dataforms.dao.Table;
import dataforms.field.common.DeleteFlagField;

/**
 * ユーザテーブルクラス。
 * <pre>
 * 必要最小限のユーザ情報を記録します。
 * 追加項目が必要な場合、別テーブルを作成してください。
 * </pre>
 */
public class UserInfoTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public UserInfoTable() {
		this.addPkField(new UserIdField());
		this.addField(new LoginIdField());
		this.addField(new PasswordField());
		this.addField(new UserNameField());
		this.addField(new MailAddressField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
		this.setAutoIncrementId(true);
		this.setSequenceStartValue(Long.valueOf(1000));
	}



	/**
	 * {@inheritDoc}
	 * <pre>
	 * 結合対象テーブルは以下の通りです。
	 * 	UserAttributeTable
	 *  UserAttributeQueryのサブクエリ　alias="ua"のサブクエリ
	 *  alias="ul"のサブクエリ
	 * </pre>
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof UserAttributeTable
			|| "ua".equals(alias) || "ul".equals(alias)) {
			return (
					this.getLinkFieldCondition("userId", joinTable, alias, "userId")
				);
		}
		return null;
	}
}
