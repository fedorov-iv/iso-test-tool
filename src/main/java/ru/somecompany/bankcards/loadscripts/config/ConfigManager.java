package ru.somecompany.bankcards.loadscripts.config;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

public class ConfigManager {
	
	private static Logger logger = Logger.getLogger(ConfigManager.class.getName());
	
	private static String portType = null;
	private static String portWeigth = null;
	private static String server = null;
	private static String serverPort = null;
	private static String startSymbol = null;
	private static String headerLength = null;
	
	private static HashMap<String, Port> portsmap = new HashMap<String, Port>();
	
	private static final String PORT_PATH = "/config/port";
	private static final String SERVER_NAME_PATH = "server";
	private static final String SERVER_PORT_PATH = "serverport";
	private static final String SERVER_PORT_START_SYMBOL_PATH = "startsymbol";
	private static final String SERVER_PORT_HEADER_LENGTH_PATH = "headerlength";
	
	static {
		refresh();
	}

	
	public static HashMap<String, Port> getPortsMap(){
		if(portsmap.isEmpty()) refresh();
		return portsmap;
	}
	
	//Refresh values of options from config file 
	private static synchronized void refresh() {
		try 
		{
			//Get java class folder path
			/*CodeSource codeSource = DataReader.class.getProtectionDomain().getCodeSource();
			File jarFile = new File(codeSource.getLocation().toURI().getPath());
			File jarDir = jarFile.getParentFile();
			
			FileInputStream inputStream = new FileInputStream(jarDir.getAbsolutePath()+ File.separatorChar + "classes" + File.separatorChar + "configuration" + File.separatorChar + "config.txt");*/

			InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream("configuration/config.txt");
	
			
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			
			InputSource insource = new InputSource(inputStream);
			NodeList nodelist = (NodeList) xpath.evaluate(PORT_PATH, insource, XPathConstants.NODESET);
			for (int i=0;i < nodelist.getLength();i++)
			{
				portType = nodelist.item(i).getAttributes().getNamedItem("type").getTextContent();
				portWeigth = nodelist.item(i).getAttributes().getNamedItem("weight").getTextContent();
				
				server = xpath.evaluate(SERVER_NAME_PATH,nodelist.item(i));
				serverPort = xpath.evaluate(SERVER_PORT_PATH,nodelist.item(i));
				startSymbol = xpath.evaluate(SERVER_PORT_START_SYMBOL_PATH,nodelist.item(i));
				headerLength = xpath.evaluate(SERVER_PORT_HEADER_LENGTH_PATH,nodelist.item(i));
				
				portsmap.put("" + (i+1), new Port(portType,portWeigth,server,serverPort,startSymbol,headerLength));
			}
		

		} catch (Exception e) {
			logger.warning("Exception: " + e.getMessage());
		}
	}

}
