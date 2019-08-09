/**=======================================================
 *
 *        工程名称:  com.ncs.cms.min
 *        文件名称:  MXmlDocument.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2015年4月28日
 *        修 改 人  : 
 *        修改时间: 
 *
 *=======================================================*/

package tszs.system.xml;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class TXmlDocument extends java.lang.Object
{
	public TXmlDocument() {
	}

	public TXmlDocument(String path) {
		this.Load(path);
	}

	private Document document = null;

	public Boolean Load(String path)
	{
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			InputSource inputSource = new InputSource(path);
			document = documentBuilderFactory.newDocumentBuilder().parse(
					inputSource);
			return new Boolean(true);
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
			document = null;
			return new Boolean(false);
		}
	}

	/**
	 * 获取xml 文件内容
	 * @return
	 */
	public String getXmlString()
	{
		try
		{
			if (document == null)
				return new String("");

			StringWriter sw = new StringWriter();
			Transformer serializer = TransformerFactory.newInstance()
					.newTransformer();
			serializer.transform(new DOMSource(document), new StreamResult(sw));
			return new String(sw.toString());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}