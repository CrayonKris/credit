package com.bonc.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * xml用dom4j解析工具类
 * @author zhijie.ma
 * @date 2017年5月2日
 *
 */
public final class DocHelper {
	
	/**
	 * 根据xml串，获取document
	 * @param xmlStr xml串
	 * @return
	 */
	public static Document load(String xmlStr) {
		Document document;
		try {
			document = DocumentHelper.parseText(xmlStr);
			
			return document;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据xml串，获取document根节点
	 * @param xmlStr xml串
	 * @return
	 */
	public static Element getRootElement(String xmlStr) {
		Document doc = load(xmlStr);
		return doc.getRootElement();
	}
		
	public static Map<String, Object> toMap(Document doc) {  
		// 采用LinkedHashMap
        Map<String, Object> map = new LinkedHashMap<String, Object>();  
        if(doc == null)  
            return map;  
        Element root = doc.getRootElement();  
        for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {  
            Element e = (Element) iterator.next();  
            //System.out.println(e.getName());  
            List list = e.elements();  
            if(list.size() > 0){  
                map.put(e.getName(), toMap(e));  
            }else  
                map.put(e.getName(), e.getText());  
        }  
        return map;  
    }  
      
  
    public static Map<String, Object> toMap(Element e) {  
    	// 采用LinkedHashMap
        Map<String, Object> map = new LinkedHashMap<String, Object>();  
        List list = e.elements();  
        if(list.size() > 0){  
            for (int i = 0;i < list.size(); i++) {  
                Element iter = (Element) list.get(i);  
                List mapList = new ArrayList();  
                  
                if(iter.elements().size() > 0){  
                    Map m = toMap(iter);  
                    if(map.get(iter.getName()) != null){  
                        Object obj = map.get(iter.getName());  
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = new ArrayList();  
                            mapList.add(obj);  
                            mapList.add(m);  
                        }  
                        if(obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = (List) obj;  
                            mapList.add(m);  
                        }  
                        map.put(iter.getName(), mapList);  
                    }else  
                        map.put(iter.getName(), m);  
                }  
                else{  
                    if(map.get(iter.getName()) != null){  
                        Object obj = map.get(iter.getName());  
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = new ArrayList();  
                            mapList.add(obj);  
                            mapList.add(iter.getText());  
                        }  
                        if(obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = (List) obj;  
                            mapList.add(iter.getText());  
                        }  
                        map.put(iter.getName(), mapList);  
                    }else  
                        map.put(iter.getName(), iter.getText());  
                }  
            }  
        }else  
            map.put(e.getName(), e.getText());  
        return map;  
    }  
}
