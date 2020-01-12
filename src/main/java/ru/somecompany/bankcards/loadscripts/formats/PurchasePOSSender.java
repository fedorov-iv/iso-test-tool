package ru.somecompany.bankcards.loadscripts.formats;

import java.io.IOException;
import java.io.InputStream;
////import java.text.DateFormat;
////import java.text.SimpleDateFormat;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;

import org.jpos.iso.*;
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
import ru.somecompany.bankcards.loadscripts.packanger.CustomGenericPackager;
import ru.somecompany.bankcards.security.Encripter;

public class PurchasePOSSender {

    private static final Logger log = LoggerFactory.getLogger(PurchasePOSSender.class);

	public PurchasePOSSender(String scenarioName) {
		this.scenarioName = scenarioName;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		// Form Ecounter
		encountPOS = new Encounter("POS", ConfigManager.getPortsMap(), cardmap,
				terminalmap);

		packager = PurchasePOSSender.class.getClassLoader().getResourceAsStream("PosLine.xml");
	}

	public PurchasePOSSender() {

		this.scenarioName = null;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		// Form Ecounter
		encountPOS = new Encounter("POS", ConfigManager.getPortsMap(), cardmap,
				terminalmap);
		packager = PurchasePOSSender.class.getClassLoader().getResourceAsStream("PosLine.xml");
	}

	private InputStream packager;

	private String scenarioName;

	// Date variables declaration block
	private static DateFormat dateFormatDate = new SimpleDateFormat("MMdd");
	private static DateFormat dateFormatTime = new SimpleDateFormat("HHmmss");

	// HashMap declaration block
	private HashMap<String, BankCard> cardmap;
	private HashMap<String, BankTerminal> terminalmap;

	// Ecounter declaration block
	private static ISOChannel channel;
	private Encounter encountPOS;
	

	public void connect() throws IOException, ISOException {
		
		Port port = encountPOS.getRandomPort();
        log.info("Trying to connect to server: " + port.getServerName() + " port: " + port.getServerPort());

		channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), new GenericPackager(
						packager), Encripter.hexToBytes("6000010000"));

		channel.connect();
	}

	public void connect_local() throws IOException, ISOException {

		Port port = encountPOS.getRandomPort();
		
		channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), 
				new CustomGenericPackager(packager), Encripter.hexToBytes("6000010000"));

		channel.connect();
		
	}
	
	public void disconnect() throws IOException, ISOException {

		channel.disconnect();

	}

	

	public String sendRefund(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountPOS.getRandomCard();
		BankTerminal bt = encountPOS.getRandomTerminal(currency);


		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0200");

		m.set(3, "200000");
		//m.set(4, String.format("%012d", Integer.parseInt(amount)));
		m.set(4, amount);
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(22, "052");
		m.set(24, "001");
		m.set(25, "00");
		m.set(35, bc.getTrack2());
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());

		m.set(62, "040306");

		// Send message
		channel.send(m);
		System.out.println(ISOUtil.hexdump(m.pack()));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(4),  m.getString(11), m.getString(22), m.getString(24), m.getString(25),m.getString(35),
				m.getString(42),m.getString(41), m.getString(62));

		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}

	public String sendPurchase(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountPOS.getRandomCard();
		BankTerminal bt = encountPOS.getRandomTerminal(currency);
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0200");

		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(22, "901");
		m.set(24, "001");
		m.set(25, "00");
		m.set(35, bc.getTrack2());
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		
		m.set(62, "054640");
		
		//m.set(63, "1862A000000022334455");

		// Send message
		channel.send(m);
		System.out.println(ISOUtil.hexdump(m.pack()));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52), m.getString(48), m.getString(63));

		// Receive message
		ISOMsg resp = channel.receive();
		System.out.println(ISOUtil.hexdump(resp.pack()));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39),resp.getString(63));

		//channel.disconnect();

		return resp.getString(39);
	}

	public String sendPurchaseReversal(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountPOS.getRandomCard();
		BankTerminal bt = encountPOS.getRandomTerminal(currency);
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0200");

		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(22, "901");
		m.set(24, "001");
		m.set(25, "00");
		m.set(35, bc.getTrack2());
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());

		m.set(62, "054640");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52));
		// Receive message
		ISOMsg resp = channel.receive();
		System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39));
		

		if (resp.getString(39).equals("00")) {

			
			// Create JPOS message
			m = new ISOMsg();

			// Fill message fields
			m.setMTI("0200");
			m.set(2, bc.getPAN());
			m.set(3, "020000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));
			STAN = Environment.getSTAN();
			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, bc.getExpDate());
			m.set(22, "901");
			m.set(24, "001");
			m.set(25, "00");
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(39, "17");
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());
			m.set(60, String.format("%012d", Integer.parseInt(amount)));
			m.set(62, "054640");

			// Send message
			channel.send(m);
			//System.out.println(ISOUtil.hexdump(m.pack()));
			System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
					m.getString(22),m.getString(42),m.getString(41),m.getString(52),m.getString(39));

			// Receive message
			resp = channel.receive();
			System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
					resp.getString(38),resp.getString(39));

		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}

	public int sendPurchaseLateReversal(int iterationCount, String currency, String amount) throws IOException,
			ISOException {

		HashMap<String, BankCard> historyBC = new HashMap<String, BankCard>();
		HashMap<String, BankTerminal> historyT = new HashMap<String, BankTerminal>();
		HashMap<String, String> historyAC = new HashMap<String, String>();

		for (int i = 1; i <= iterationCount; i++) {

			// Get random card
			BankCard bc = encountPOS.getRandomCard();
			BankTerminal bt = encountPOS.getRandomTerminal(currency);
			Port port = encountPOS.getRandomPort();

			// Define JPOS Channel

			// Create JPOS message
			ISOMsg m = new ISOMsg();

			// Fill message fields
			m.setMTI("0200");

			m.set(3, "000000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));
			String STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(22, "052");
			m.set(24, "001");
			m.set(25, "00");
			m.set(35, bc.getTrack2());
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());

			m.set(62, "054640");

			// Send message
			channel.send(m);
			System.out.println(ISOUtil.hexdump(m.pack()));

			// Receive message
			ISOMsg resp = channel.receive();
			channel.disconnect();

			if (resp.getString(39).equals("00")) {
				historyBC.put(resp.getString(37), bc);
				historyT.put(resp.getString(37), bt);
				historyAC.put(resp.getString(37), resp.getString(38));
			}

		}

		try {
			Thread.sleep(30000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		Iterator<Entry<String, BankCard>> it = historyBC.entrySet().iterator();
		int result = historyBC.size();

		while (it.hasNext()) {
			Map.Entry<String, BankCard> pairs = (Map.Entry<String, BankCard>) it
					.next();

			// Create JPOS message
			ISOMsg m = new ISOMsg();

			// Fill message fields
			m.setMTI("0200");
			m.set(2, historyBC.get(pairs.getKey()).getPAN());
			m.set(3, "020000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));
			String STAN = Environment.getSTAN();
			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, historyBC.get(pairs.getKey()).getExpDate());
			m.set(22, "052");
			m.set(24, "001");
			m.set(25, "00");
			m.set(37, pairs.getKey().toString());
			m.set(38, historyAC.get(pairs.getKey()));
			m.set(41, historyT.get(pairs.getKey()).getTerminalId());
			m.set(42, historyT.get(pairs.getKey()).getMerchantId());
			m.set(60, String.format("%012d", Integer.parseInt(amount)));
			m.set(62, "054640");

			// Send message
			channel.send(m);
			System.out.println(ISOUtil.hexdump(m.pack()));

			// Receive message
			@SuppressWarnings("unused")
			ISOMsg resp = channel.receive();
			channel.disconnect();

		}

		return result;
	}

	public String sendPurchaseSignature(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountPOS.getRandomCard();
		BankTerminal bt = encountPOS.getRandomTerminal(currency);
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0200");

		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(22, "901");
		m.set(24, "001");
		m.set(25, "00");
		m.set(35, bc.getTrack2());
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());

		m.set(62, "054640");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52), m.getString(48));

		// Receive message
		ISOMsg resp = channel.receive();
		System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39));

		//channel.disconnect();

		return resp.getString(39);
	}

	public String sendPurchaseContactless(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountPOS.getRandomCard();
		BankTerminal bt = encountPOS.getRandomTerminal(currency);
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0200");

		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(22, "911");
		m.set(24, "001");
		m.set(25, "00");
		m.set(35, bc.getTrack2());
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		
		m.set(62, "054640");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52), m.getString(48));

		// Receive message
		ISOMsg resp = channel.receive();
		System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39));

		//channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendPurchaseCompletion(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountPOS.getRandomCard();
		BankTerminal bt = encountPOS.getRandomTerminal(currency);
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");

		m.set(3, "300000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(22, "901");
		m.set(24, "001");
		m.set(25, "00");
		m.set(35, bc.getTrack2());
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());

		m.set(62, "054640");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52));
		// Receive message
		ISOMsg resp = channel.receive();
		System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39));
		

		if (resp.getString(39).equals("00")) {

			
			// Create JPOS message
			m = new ISOMsg();

			// Fill message fields
			m.setMTI("0220");
			m.set(2, bc.getPAN());
			m.set(3, "000000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));
			STAN = Environment.getSTAN();
			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, bc.getExpDate());
			m.set(22, "012");
			m.set(24, "001");
			m.set(25, "06");
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(39, "00");
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());
			//m.set(60, String.format("%012d", Integer.parseInt(amount)));
			m.set(60, String.format("%012d", Integer.parseInt("00095000")));
			
			m.set(62, "054640");
			

			// Send message
			channel.send(m);
			//System.out.println(ISOUtil.hexdump(m.pack()));
			System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
					m.getString(22),m.getString(42),m.getString(41),m.getString(52),m.getString(39));

			// Receive message
			resp = channel.receive();
			System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
					resp.getString(38),resp.getString(39));

		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}
	
	public String sendPurchaseNoCVM(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountPOS.getRandomCard();
		BankTerminal bt = encountPOS.getRandomTerminal(currency);
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0200");

		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(22, "900");
		m.set(24, "001");
		m.set(25, "00");
		m.set(35, bc.getTrack2());
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		
		m.set(62, "054640");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52), m.getString(48));

		// Receive message
		ISOMsg resp = channel.receive();
		System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39));

		//channel.disconnect();

		return resp.getString(39);
	}

}
