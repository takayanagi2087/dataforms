package dataforms.report;

import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;

/**
 * プリンターの管理クラス。
 *
 */
public final class PrintDevices {
	
	/**
	 * 
	 */
	private static Logger logger = Logger.getLogger(PrintDevices.class);
	
	/**
	 * コンストラクタ。
	 */
	private PrintDevices() {
		
	}
	
	/**
	 * 印刷サービスの名称一覧を取得します。
	 * @return 印刷サービスの名称一覧。
	 */
	public static List<String> getPrintServiceNameList() {
		List<String> ret = new ArrayList<String>();
		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
		for (PrintService svc : services) {
			ret.add(svc.getName());
		}
		return ret;
	}
	
	// "EPSON PX-M5041F"
	
	/**
	 * 印刷サービスを取得します。
	 * @param printerName プリンタ名。 
	 * @return 印刷JOB。
	 */
	public static PrintService getPrintService(final String printerName) {
		PrintService[] services = PrinterJob.lookupPrintServices();
		PrintService ps = null;
		for (PrintService svc : services) {
			logger.debug("printer=" + svc.getName());
			if (printerName.equals(svc.getName())) {
				ps = svc;
				break;
			}
		}
		return ps;
	}

}
