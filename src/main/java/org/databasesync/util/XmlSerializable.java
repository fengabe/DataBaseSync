package org.databasesync.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.thoughtworks.xstream.XStream;


/*
 * ���ڽ��������־û���XML�ļ���������̵ĳ־û���(dom4j,xStreamʵ��)
 */
public class XmlSerializable<T> {
	
	private final Logger logger = Logger.getLogger(this.getClass());
	// XML�ļ���
	private String xmlFile;
	// XML �ĵ�����
	private Document document;
	// ���ڵ�
	private Element root;
	// ���ڵ�����
	private final String rootText = "root";
	
	/*
	 * ���������캯����ָ���洢���ļ���
	 */
	public XmlSerializable(String xmlFile) throws DocumentException {
		this.xmlFile = xmlFile;
		init();
	}
	
	/*
	 * ��ʼ���ĵ����󼰸��ڵ�
	 */
	private void init() throws DocumentException {
		File file = new File(xmlFile);
		try {
			if (file.exists()) {
				// �ļ�����,ֱ�Ӵ��ļ���ȡ�ĵ�����
				SAXReader reader = new SAXReader();
				
				InputStream in = new FileInputStream(file);
				InputStreamReader strInStream = new InputStreamReader(in, "utf-8");
				document = reader.read(strInStream);
				//document = reader.read(file);
				root = document.getRootElement();
			} else { 
		        //�ȴ����ļ���Ŀ¼   
		        String path = xmlFile.substring(0, xmlFile.lastIndexOf(File.separator));   
		        File pFile = new File(path);   
		        pFile.mkdirs();
		        
				//�����ĵ�����
				document = DocumentHelper.createDocument();
				root = document.addElement(rootText);// �������ڵ�
			}
		} catch (DocumentException e) {
			throw e;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description:TODO
	 *
	 * @param type
	 * @throws DocumentException
	 * @author xiangnan on 2013-3-30
	 */
	public void add(T type) throws DocumentException {
		
		XStream xStream = new XStream();
		String xml = xStream.toXML(type);
		try {
			Document docTmp = DocumentHelper.parseText(xml);
			Element typeElm = docTmp.getRootElement();
			root.add(typeElm);
		} catch (DocumentException e) {
			throw e;
		}
	}

	
	/**
	 * 
	 * @Description:TODO
	 *
	 * @param type
	 * @author xiangnan on 2013-3-30
	 */
	public void del(T type) {
		XStream xStream = new XStream();
		String xml = xStream.toXML(type);
		List nodes = root.elements();
		for (Iterator it = nodes.iterator(); it.hasNext();) {
			Element companyElm = (Element) it.next();
			if (companyElm.asXML().equals(xml)) {
				// ɾ��ԭ�нڵ�
				root.remove(companyElm);
				// �����ļ�
				//saveDocumentToFile();
				//return;
			}
		}
	}

	/*
	 * ��XML��ȡ�����ж���
	 */
	public List<T> loadAll() {
		List<T> retval = new ArrayList<T>();
		List nodes = root.elements();
		for (Iterator it = nodes.iterator(); it.hasNext();) {
			//ȡ��ÿ���ڵ�
			Element companyElm = (Element) it.next();
			//���ڵ�ת��Ϊ����
			XStream xStream = new XStream();
			T t = (T) xStream.fromXML(companyElm.asXML());
			retval.add(t);
		}
		return retval;
	}

	/*
	 * ��Documentд���ļ�
	 */
	public void saveDocumentToFile() throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8"); // ָ��XML����
		format.setTrimText(false);
		XMLWriter writer = null;
		try {
	        OutputStream out = new FileOutputStream(xmlFile);
	        OutputStreamWriter outWriter = new OutputStreamWriter(out, "utf-8");
			writer = new XMLWriter(outWriter, format);
			writer.write(document);
		} catch (IOException e) {
			throw e;
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw e;
				} finally {
					writer = null;
				}
			}
		}
	}
}
