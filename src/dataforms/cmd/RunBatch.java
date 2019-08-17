package dataforms.cmd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * バッチ実行コマンド。
 */
public class RunBatch {
	/**
	 * バッチ処理の呼び出し。
	 * @param url バッチURL。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	private int run(final String url) throws Exception {
		URL u = new URL(url);
		int ret = 0;
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		try  {
			conn.setRequestMethod("GET");
			conn.connect();
			int status = conn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				try (InputStream in = conn.getInputStream()) {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
						String line = reader.readLine();
						ret = Integer.parseInt(line);
					}
				}
			}
		} finally {
			conn.disconnect();
		}
		return ret;
	}


	/**
	 * メイン処理。
	 * @param args コマンドライン引数。
	 */
	public static void main(final String[] args) {
		if (args.length != 1) {
			System.out.println("Usage:");
			System.out.println("java -cp dataforms.jar dataforms.cmd.RunBatch <batch url>");
		} else {
			try {
				RunBatch app = new RunBatch();
				int ret = app.run(args[0]);
				System.exit(ret);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.exit(9999);
	}
}
