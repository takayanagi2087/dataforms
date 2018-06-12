package dataforms.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import dataforms.debug.page.alltype.AllTypeEditForm;
import dataforms.field.base.Field;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.MapUtil;


/**
 * XSL-FOレポート。
 *
 */
public class XslFoReport extends Report {
	
	/**
	 * Logger.
	 */
	private Logger logger = Logger.getLogger(XslFoReport.class);
	
	/**
	 * テンプレートファイルのパス。
	 */
	private String templatePath = null;
	
	/**
	 * コンストラクタ。
	 */
	public XslFoReport() {
		
	}

	/**
	 * コンストラクタ。
	 * @param templatePath テンプレートパス。
	 */
	public XslFoReport(final String templatePath) {
		this.templatePath = templatePath;
	}
	
	/**
	 * テンプレートパスを取得します。
	 * @return テンプレートパス。
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * テンプレートパスを設定します。
	 * @param templatePath テンプレートパス。
	 */
	public void setTemplatePath(final String templatePath) {
		this.templatePath = templatePath;
	}

	/**
	 * ページ編集バッファ。
	 */
	private String pageBuffer = null;
	
	/**
	 * ページリスト。
	 */
	private List<String> pageList = null;
	
	@Override
	protected void printPage(final int page, final Map<String, Object> data) throws Exception {
		this.pageBuffer = (new StringBuilder(this.pageBlockFo)).toString();
		super.printPage(page, data);
		this.pageList.add(this.pageBuffer + "\n");
	}
	
	
	
	@Override
	protected void printField(final int page, final Field<?> field, final Map<String, Object> data) throws Exception {
		Object obj = MapUtil.getValue(field.getId(), data);
		field.setValueObject(obj);
		Object cv = field.getClientValue();
		if (cv == null) {
			cv = "";
		}
//		logger.debug("id=" + field.getId() + ", value=" + cv.toString());
		this.pageBuffer = this.pageBuffer.replace("$" + field.getId(), cv.toString());
	}


	/**
	 * SVGテキストからDocumentに変換。
	 * @param path foファイルのパス。
	 * @return Document。
	 * @throws Exception 例外。
	 */
	private Document getXmlDocument(final String path) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(path));
		return doc;
	}


	
	/**
	 * NodeをXMLテキストに変換します。
	 * @param node ノード。
	 * @return XMLテキスト。
	 * @throws TransformerException 例外。
	 */
	public String convertToString(final Node node) throws TransformerException{
		DOMSource source = new DOMSource(node);
		StringWriter swriter = new StringWriter();
		StreamResult result = new StreamResult(swriter);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(source, result);
		String nodeText = swriter.toString();
		return nodeText.replaceAll("\\<\\?xml.*\\?\\>", "");
	}

	/**
	 * ネームスペースコンテキスト。
	 *
	 */
	public class NamespaceResolver implements NamespaceContext {
		/**
		 * XMLドキュメント。
		 */
		private Document sourceDocument;

		/**
		 * コンストラクタ。
		 * @param document ドキュメント。
		 */
		public NamespaceResolver(final Document document) {
			sourceDocument = document;
		}

		@Override
		public String getNamespaceURI(final String prefix) {
			if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				return sourceDocument.lookupNamespaceURI(null);
			} else {
				return sourceDocument.lookupNamespaceURI(prefix);
			}
		}

		@Override
		public String getPrefix(final String namespaceURI) {
			return sourceDocument.lookupPrefix(namespaceURI);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Iterator getPrefixes(final String namespaceURI) {
			return null;
		}
	}
	
	/**
	 * ドキュメントフレーム。
	 */
	private Document docFo = null;
	/**
	 * PageのFloノード。
	 */
	private Node pageFlow = null;
	/**
	 * 1ページ分のXML。
	 */
	private String pageBlockFo = null;
	
	@Override
	protected void initPage(final int pages) throws Exception {
		logger.debug("pages=" + pages);
		this.docFo = this.getXmlDocument(this.templatePath);
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceResolver(this.docFo));
		Node page = (Node) xpath.evaluate("/fo:root/fo:page-sequence/fo:flow/fo:block", this.docFo, XPathConstants.NODE);
		logger.debug("page nodeName=" + page.getNodeName());
		this.pageBlockFo = this.convertToString(page).replace("xmlns:fo=\"http://www.w3.org/1999/XSL/Format\"", "page-break-before=\"always\"");
		this.pageFlow = (Node) xpath.evaluate("/fo:root/fo:page-sequence/fo:flow", this.docFo, XPathConstants.NODE);
		this.pageFlow.removeChild(page);
		Node textNode = this.docFo.createTextNode("${pageBlock}\n\n");
		this.pageFlow.appendChild(textNode);
		this.pageList = new ArrayList<String>();
	}
	
	/**
	 * FOを指定して、PDFを取得します。
	 * @param foXml Fo形式の文字列。
	 * @return PDFファイル。
	 * @throws Exception 例外。
	 */
	private byte[] createPdf(final String foXml) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(foXml.getBytes(DataFormsServlet.getEncoding()));
			try {
				String conf = AllTypeEditForm.getServlet().getServletContext().getRealPath("/WEB-INF/apachefop/fop.xconf");
				Source fo = new StreamSource(is);
				//FOPをセットアップする
				FopFactory fopFactory = FopFactory.newInstance(new File(conf));
				Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, os);
				//XSL FO⇒PDFに変換する
				Result result = new SAXResult(fop.getDefaultHandler());
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer();
				transformer.transform(fo, result);
			} finally {
				is.close();
			}
		} finally {
			os.close();
		}
		return os.toByteArray();
	}

	
	
	@Override
	public byte[] getReport() throws Exception {
		String xml = this.convertToString(this.docFo);
		StringBuilder sb = new StringBuilder();
		for (String p: this.pageList) {
			sb.append(p);
		}
		xml = xml.replace("${pageBlock}", sb.toString());
		logger.debug("xslFo=" + xml);
		return this.createPdf(xml);
	}

}
