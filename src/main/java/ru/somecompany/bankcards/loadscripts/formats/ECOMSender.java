package ru.somecompany.bankcards.loadscripts.formats;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.NACChannel;
import org.jpos.iso.packager.GenericPackager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.somecompany.bankcards.loadscripts.config.ConfigManager;
import ru.somecompany.bankcards.loadscripts.config.Port;
import ru.somecompany.bankcards.loadscripts.encounter.Encounter;
import ru.somecompany.bankcards.loadscripts.encounter.Environment;
import ru.somecompany.bankcards.loadscripts.loaddata.BankCard;
import ru.somecompany.bankcards.loadscripts.loaddata.BankTerminal;
import ru.somecompany.bankcards.loadscripts.loaddata.DataReader;
import ru.somecompany.bankcards.security.Encripter;

public class ECOMSender {

	private static final Logger log = LoggerFactory.getLogger(ECOMSender.class);

	public ECOMSender(String scenarioName) {
		this.scenarioName = scenarioName;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		// Form Ecounter
		encountECOM = new Encounter("ECOM", ConfigManager.getPortsMap(),
				cardmap, terminalmap);

		packager =  getClass().getClassLoader().getResourceAsStream("DHI.xml");

	}

	public ECOMSender() {
		this.scenarioName = null;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		// Form Ecounter
		encountECOM = new Encounter("ECOM", ConfigManager.getPortsMap(),
				cardmap, terminalmap);

		packager =  getClass().getClassLoader().getResourceAsStream("DHI.xml");
	}

	private InputStream packager;

	private String scenarioName;

	// Date variables declaration block
	private static DateFormat dateFormatFull = new SimpleDateFormat(
			"MMddHHmmss");
	private static DateFormat dateFormatTime = new SimpleDateFormat("HHmmss");

	// HashMap declaration block
	private HashMap<String, BankCard> cardmap;
	private HashMap<String, BankTerminal> terminalmap;

	// Ecounter declaration block
	private Encounter encountECOM;

	public String sendEcomAuth(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountECOM.getRandomCard();
		BankTerminal bt = encountECOM.getRandomTerminal(currency);
		Port port = encountECOM.getRandomPort();

		log.info("Trying to connect to server: " + port.getServerName() + " port: " + port.getServerPort());

		// Define JPOS Channel
		
		//InputStream instr = VPOSSender.class.getResourceAsStream("/DHI.xml");
/*
		ISOChannel channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), new GenericPackager(
						instr), Encripter.hexToBytes(""));
*/		
		ISOChannel channel = new NACChannel(
				port.getServerName(), Integer.parseInt(port.getServerPort()), 
				new GenericPackager(packager), Encripter.hexToBytes("")
		);
		

		channel.connect();
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		//m.set(22, "0120");
		m.set(22, "0100");
		m.set(25, "59");
		//m.set(25, "05");
		m.set(32, bt.getAcqInsId(bc.getCardType()));

		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(48, "211");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(60, "5001060005");

		// m.set("126.10", "10 " + bc.getCVV2());

		// Send message
		channel.send(m);
		log.info(ISOUtil.hexdump(m.pack()));
		log.info(String.format("%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52)));

		// Receive message
		ISOMsg resp = channel.receive();
		log.info(String.format("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39)));
		
		channel.disconnect();

		return resp.getString(39);
	}

	public String sendComplition(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountECOM.getRandomCard();
		BankTerminal bt = encountECOM.getRandomTerminal(currency);
		Port port = encountECOM.getRandomPort();

		log.info("Trying to connect to server: " + port.getServerName() + " port: " + port.getServerPort());

		// Define JPOS Channel
		
		//InputStream instr = VPOSSender.class.getResourceAsStream("/DHI.xml");
		ISOChannel channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), new GenericPackager(
						packager), Encripter.hexToBytes(""));
/*		
		ISOChannel channel = new NACChannel(
				port.getServerName(), Integer.parseInt(port.getServerPort()), 
				new GenericPackager("DHI.xml"), Encripter.hexToBytes("")
		);*/
		
		
		channel.connect();
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "0120");
		m.set(25, "59");
		m.set(32, bt.getAcqInsId(bc.getCardType()));

		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(48, "211");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(60, "5001080007");

		// m.set("126.10", "10 " + bc.getCVV2());

		// Send message
		channel.send(m);
		log.info(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		if (resp.getString(39).equals("00")) {

			channel.connect();
			// Create JPOS message
			m = new ISOMsg();

			// Fill message fields
			m.setMTI("0220");
			m.set(2, bc.getPAN());
			m.set(3, "000000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));

			cal = Calendar.getInstance();
			date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(14, bc.getExpDate());
			m.set(18, bt.getMCC());
			m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(22, "0120");
			m.set(25, "59");
			m.set(32, bt.getAcqInsId(bc.getCardType()));
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());
			m.set(48, "211");
			m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(60, "5001080007");

			m.set(120, "Test_payture_new_12313643728");
			// m.set("126.10", "10 " + bc.getCVV2());

			// Send message
			channel.send(m);
			log.info(ISOUtil.hexdump(m.pack()));

			// Receive message
			resp = channel.receive();
			channel.disconnect();
		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}

	public String sendEcomAuthMOTO(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountECOM.getRandomCard();
		BankTerminal bt = encountECOM.getRandomTerminal(currency);
		Port port = encountECOM.getRandomPort();

		log.info("Trying to connect to server: " + port.getServerName() + " port: " + port.getServerPort());

		// Define JPOS Channel
		
		//InputStream instr = VPOSSender.class.getResourceAsStream("/DHI.xml");
/*
		ISOChannel channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), new GenericPackager(
						instr), Encripter.hexToBytes(""));
*/		
		ISOChannel channel = new NACChannel(
				port.getServerName(), Integer.parseInt(port.getServerPort()), 
				new GenericPackager(packager), Encripter.hexToBytes("")
		);
		

		channel.connect();
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "0000");
		//m.set(25, "01");
		m.set(25, "08");
		//m.set(25, "05");
		m.set(32, bt.getAcqInsId(bc.getCardType()));

		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(48, "211");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(60, "5001080007");

		// m.set("126.10", "10 " + bc.getCVV2());

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		log.info(String.format("%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52)));

		// Receive message
		ISOMsg resp = channel.receive();
		log.info(String.format("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39)));
		
		channel.disconnect();

		return resp.getString(39);
	}
	public String sendEcomPayment(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountECOM.getRandomCard();
		BankTerminal bt = encountECOM.getRandomTerminal(currency);
		Port port = encountECOM.getRandomPort();

		log.info("Trying to connect to server: " + port.getServerName() + " port: " + port.getServerPort());

		// Define JPOS Channel
		
//		InputStream instr = VPOSSender.class.getResourceAsStream("/DHI.xml");
/*
		ISOChannel channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), new GenericPackager(
						instr), Encripter.hexToBytes(""));
*/		
		ISOChannel channel = new NACChannel(
				port.getServerName(), Integer.parseInt(port.getServerPort()), 
				new GenericPackager(packager), Encripter.hexToBytes("")
		);
		

		channel.connect();
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "500000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "0120");
		m.set(25, "59");
		m.set(32, bt.getAcqInsId(bc.getCardType()));

		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(48, "211");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(60, "5001080007");
		m.set(104, "PP01700116REFERENCE11111110225ACCOUNT2222222222222222220314Rba Money Send0417Troitskaya ul. 170506MOSCOW07036432106112909221049522233442311TestTest999F80205F216" + bc.getPAN() );
		// m.set("126.10", "10 " + bc.getCVV2());

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		log.info(String.format("%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52)));

		// Receive message
		ISOMsg resp = channel.receive();
		log.info(String.format("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39)));
		
		channel.disconnect();

		return resp.getString(39);
	}

	public String sendEcomAuthReversal(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountECOM.getRandomCard();
		BankTerminal bt = encountECOM.getRandomTerminal(currency);
		Port port = encountECOM.getRandomPort();

		log.info("Trying to connect to server: " + port.getServerName() + " port: " + port.getServerPort());

		// Define JPOS Channel
		
		//InputStream instr = VPOSSender.class.getResourceAsStream("/DHI.xml");
/*
		ISOChannel channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), new GenericPackager(
						instr), Encripter.hexToBytes(""));
*/		
		ISOChannel channel = new NACChannel(
				port.getServerName(), Integer.parseInt(port.getServerPort()), 
				new GenericPackager(packager), Encripter.hexToBytes("")
		);
		

		channel.connect();
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "0120");
		m.set(25, "59");
		//m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(32, "999999");
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(48, "211");
		//m.set(48,"212jJazd/wR3HbOCBEBb42aACgAAAA=");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(60, "5001080007");
		//m.set("126.10", "10 " + bc.getCVV2());

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		log.info(String.format("%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52)));

		// Receive message
		ISOMsg resp = channel.receive();
		log.info(String.format("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39)));
		

		if (resp.getString(39).equals("00")) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			
			// Fill message fields
			m.setMTI("0400");
			m.set(2, bc.getPAN());
			m.set(3, "000000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));
			
			cal = Calendar.getInstance();
			date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			
			m.set(14, bc.getExpDate());
			m.set(18, bt.getMCC());
			m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(22, "0120");
			m.set(25, "59");
			//m.set(32, bt.getAcqInsId(bc.getCardType()));
			m.set(32,"999999");
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(39, "17");
			
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());
			//m.set(48, "211");
			//m.set(48,"212jJazd/wR3HbOCBEBb42aACgAAAA=");
			m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			m.set(60, "5001080007");

			// m.set("126.10", "10 " + bc.getCVV2());

			// Send message
			channel.send(m);
			//System.out.println(ISOUtil.hexdump(m.pack()));
			log.info(String.format("%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
					m.getString(22),m.getString(42),m.getString(41)));

			// Receive message
			resp = channel.receive();
			log.info(String.format("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
					resp.getString(38),resp.getString(39)));
			
		} else {
			return resp.getString(39);
		}
		
		channel.disconnect();
		return resp.getString(39);
		
	}
	
	public String sendComplitionPartial(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountECOM.getRandomCard();
		BankTerminal bt = encountECOM.getRandomTerminal(currency);
		Port port = encountECOM.getRandomPort();

		log.info("Trying to connect to server: " + port.getServerName() + " port: " + port.getServerPort());

		// Define JPOS Channel
		

		ISOChannel channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), new GenericPackager(
						packager), Encripter.hexToBytes(""));

		channel.connect();

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0220");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "0120");
		m.set(25, "59");
		m.set(32, bt.getAcqInsId(bc.getCardType()));

		m.set(37, STAN + dateFormatTime.format(date));

		m.set(38, "888999");
		
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(48, "212jMnqvjCwtKm4CBAC49YWBzgAAAA=");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(60, "5001080007");

		//m.set(120, "UD135AD1300125GOLUBEVA/EKATERINA MS    0308201608230408924952000525SWISS INTERNATIONAL AIR L06157244561040801  0704LX  08120000010800001201Y");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		log.info(String.format("%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41)));

		// Receive message
		ISOMsg resp = channel.receive();
		log.info(String.format("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39)));
		
		channel.disconnect();

		return resp.getString(39);
	}

}
