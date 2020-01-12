package ru.somecompany.bankcards.loadscripts.formats;

import java.io.IOException;
import java.io.InputStream;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;

import org.jpos.iso.*;
import org.jpos.iso.channel.NACChannel;

import ru.cinimex.bankcards.loadscripts.config.*;
import ru.cinimex.bankcards.loadscripts.encounter.*;
import ru.cinimex.bankcards.loadscripts.loaddata.*;
import ru.somecompany.bankcards.loadscripts.config.ConfigManager;
import ru.somecompany.bankcards.loadscripts.config.Port;
import ru.somecompany.bankcards.loadscripts.packanger.CustomGenericPackager;
import ru.somecompany.bankcards.security.Encripter;
import ru.somecompany.bankcards.loadscripts.encounter.Encounter;
import ru.somecompany.bankcards.loadscripts.encounter.Environment;
import ru.somecompany.bankcards.loadscripts.loaddata.BankCard;
import ru.somecompany.bankcards.loadscripts.loaddata.BankTerminal;
import ru.somecompany.bankcards.loadscripts.loaddata.DataReader;

public class VPOSSender {

	public VPOSSender(String scenarioName) {
		this.scenarioName = scenarioName;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		// Form Ecounter
		encountVPOS = new Encounter("VPOS", ConfigManager.getPortsMap(), cardmap, terminalmap);
		encountMCMS = new Encounter("VPOSMCMS", ConfigManager.getPortsMap(), cardmap, terminalmap);
		encountVMT = new Encounter("VPOSVMT", ConfigManager.getPortsMap(), cardmap, terminalmap);
		encountP2P = new Encounter("VPOSP2P", ConfigManager.getPortsMap(), cardmap, terminalmap);
	}

	public VPOSSender() {
		
		this.scenarioName = null;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		// Form Ecounter
		encountVPOS = new Encounter("VPOS",	ConfigManager.getPortsMap(), cardmap, terminalmap);
		encountMCMS = new Encounter("VPOSMCMS", ConfigManager.getPortsMap(), cardmap, terminalmap);
		encountVMT = new Encounter("VPOSVMT", ConfigManager.getPortsMap(), cardmap,	terminalmap);
		encountP2P = new Encounter("VPOSP2P", ConfigManager.getPortsMap(), cardmap,	terminalmap);
	}

	private String scenarioName = null;

	// Date variables declaration block
	private static DateFormat dateFormatFull = new SimpleDateFormat("MMddHHmmss");
	private static DateFormat dateFormatDate = new SimpleDateFormat("MMdd");
	private static DateFormat dateFormatTime = new SimpleDateFormat("HHmmss");

	// HashMap declaration block
	private HashMap<String, BankCard> cardmap;
	private HashMap<String, BankTerminal> terminalmap;

	// Ecounter declaration block
	private Encounter encountVPOS;
	private Encounter encountMCMS;
	private Encounter encountVMT;
	private Encounter encountP2P;

	private static ISOChannel channel;
	
	// Get Date
	private static Calendar cal = Calendar.getInstance();
	private static Date date = cal.getTime();


	public void connect() throws IOException, ISOException {

		Port port = encountVPOS.getRandomPort();

		InputStream instr = VPOSSender.class.getResourceAsStream("/PosLine.xml");
		channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), new CustomGenericPackager(
						instr), Encripter.hexToBytes(""));
		

		channel.connect();
	}

	public void connect_local() throws IOException, ISOException {

		Port port = encountVPOS.getRandomPort();
		
		channel = new NACChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), 
				new CustomGenericPackager("PosLine.xml"), Encripter.hexToBytes(""));

		channel.connect();
		
	}
	
	public void disconnect() throws IOException, ISOException {

		channel.disconnect();

	}


	public String sendNetworkSignOn() throws IOException, ISOException {

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0800");

		String STAN = Environment.getSTAN();
		m.set(11, STAN);

		m.set(70, "001");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}

	public String sendNetworkSignOff() throws IOException, ISOException {

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0800");

		//Calendar cal = Calendar.getInstance();
		//Date date = cal.getTime();
		//m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);

		//m.set(37, STAN + dateFormatTime.format(date));
		m.set(70, "002");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		
		return resp.getString(39);
	}

	public String sendNetworkEchoTest () throws IOException, ISOException {

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0800");

		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		//m.set(37, STAN + dateFormatTime.format(date));
		m.set(70, "301");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		
		return resp.getString(39);
	}
	
	
	public String sendBalanceInquiry(String currency) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountVPOS.getRandomCard();
		BankTerminal bt = encountVPOS.getRandomTerminal(currency);
		
		// Define JPOS Channel
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "300000");
		m.set(4, "000000000000");

		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(22, "012");
		m.set(24, "001");
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(62, "135791");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}


	public String sendBalanceInquiry1(String currency) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountVPOS.getRandomCard();
		BankTerminal bt = encountVPOS.getRandomTerminal(currency);
		
		// Define JPOS Channel
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "300000");
		m.set(4, "000000000000");

		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(22, "012");
		m.set(24, "001");
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(62, "135791");

		// Send message
		channel.send(m);
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41) );
		//System.out.println(ISOUtil.hexdump(m.pack()));
		
		// Receive message
		ISOMsg resp = channel.receive();
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39),resp.getString(54));
		//System.out.println(ISOUtil.hexdump(resp.pack()));

		return resp.getString(39);
	}

	
	
	public String sendCashAdvance(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountVPOS.getRandomCard();
		BankTerminal bt = encountVPOS.getRandomTerminal(currency);
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(22, "012");
		m.set(24, "001");

		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());

		m.set(62, "135791");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}

	public String sendCashAdvanceReversal(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountVPOS.getRandomCard();
		BankTerminal bt = encountVPOS.getRandomTerminal(currency);
	
		// Define JPOS Channel
	
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(22, "012");
		m.set(24, "001");

		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());

		m.set(62, "135791");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		if (resp.getString(39).equals("00")) {

			channel.connect();
			// Create JPOS message
			m = new ISOMsg();

			// Fill message fields
			m.setMTI("0400");
			m.set(2, bc.getPAN());
			m.set(3, "000000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(14, bc.getExpDate());
			m.set(22, "012");
			m.set(24, "001");
			m.set(25, "05");
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());

			// Send message
			channel.send(m);
			//System.out.println(ISOUtil.hexdump(m.pack()));

			// Receive message
			resp = channel.receive();
	
		} else {
			return resp.getString(39);
		}

		return resp.getString(39);
	}

	public int sendCashAdvanceLateReversal(int iterationCount, String currency, String amount)
			throws IOException, ISOException {

		HashMap<String, BankCard> historyBC = new HashMap<String, BankCard>();
		HashMap<String, BankTerminal> historyT = new HashMap<String, BankTerminal>();
		HashMap<String, String> historyAC = new HashMap<String, String>();

		for (int i = 1; i <= iterationCount; i++) {
			// Get random card
			BankCard bc = encountVPOS.getRandomCard();
			BankTerminal bt = encountVPOS.getRandomTerminal(currency);

			// Define JPOS Channel

			// Create JPOS message
			ISOMsg m = new ISOMsg();

			// Fill message fields
			m.setMTI("0100");
			m.set(2, bc.getPAN());
			m.set(3, "000000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));
			String STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(14, bc.getExpDate());
			m.set(22, "012");
			m.set(24, "001");

			m.set(37, STAN + dateFormatTime.format(date));
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());

			m.set(62, "135791");

			// Send message
			channel.send(m);
			System.out.println(ISOUtil.hexdump(m.pack()));

			// Receive message
			ISOMsg resp = channel.receive();

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

			// Define JPOS Channel
			
			// Create JPOS message
			ISOMsg m = new ISOMsg();

			// Fill message fields
			m.setMTI("0400");
			m.set(2, historyBC.get(pairs.getKey()).getPAN());
			m.set(3, "000000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));
			String STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(14, historyBC.get(pairs.getKey()).getExpDate());
			m.set(22, "012");
			m.set(24, "001");
			m.set(25, "05");
			m.set(37, pairs.getKey().toString());
			m.set(38, historyAC.get(pairs.getKey()));
			m.set(41, historyT.get(pairs.getKey()).getTerminalId());
			m.set(42, historyT.get(pairs.getKey()).getMerchantId());

			// Send message
			channel.send(m);
			System.out.println(ISOUtil.hexdump(m.pack()));

			// Receive message
			@SuppressWarnings("unused")
			ISOMsg resp = channel.receive();

		}

		return result;
	}

	public String sendServicePayment(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountVPOS.getRandomCard();
		BankTerminal bt = encountVPOS.getRandomTerminal(currency);

		// Define JPOS Channel

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(22, "012");
		m.set(24, "001");
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(62, "135791");
		//m.set(120, "R00099213661775");
		m.set(120, "USRDTMTS9163332211");

		// Send message
		channel.send(m);
		System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}

	public String sendServicePaymentReversal(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountVPOS.getRandomCard();
		BankTerminal bt = encountVPOS.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(22, "012");
		m.set(24, "001");
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(62, "135791");
		m.set(120, "R00099213661775");		

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		if (resp.getString(39).equals("00")) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			
			// Create JPOS message
			m = new ISOMsg();

			// Fill message fields
			m.setMTI("0400");
			m.set(2, bc.getPAN());
			m.set(3, "000000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(14, bc.getExpDate());
			m.set(22, "012");
			m.set(24, "001");
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(39, "00");
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());
			// Send message
			channel.send(m);
			//System.out.println(ISOUtil.hexdump(m.pack()));

			// Receive message
			resp = channel.receive();
			
		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}
	
	public String sendP2P_db_cr(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc1 = encountP2P.getRandomCard();
		BankCard bc2 = encountP2P.getRandomCard();
		BankTerminal bt = encountP2P.getRandomTerminal(currency);

		String result1 = sendp2p(bc1, bc2, bt, currency, amount);
		if (result1.equals("00")) {
			String result2 = sendp2p(bc2, bc1, bt, currency, amount);
			return result2;
		} else {
			return result1;
		}
	}

	public String sendP2P_db_card(String currency, String amount, String pan_to_credit) throws IOException, ISOException {

		// Get random card
		BankCard bc1 = encountP2P.getRandomCard();
		BankCard bc2 = new BankCard("1111","111",pan_to_credit);
		BankTerminal bt = encountP2P.getRandomTerminal(currency);

		String result1 = sendp2p(bc1, bc2, bt, currency, amount);
		return result1;
	}

	public String sendP2P_db_card_old(String currency, String amount, String pan_to_credit) throws IOException, ISOException {

		// Get random card
		BankCard bc1 = encountP2P.getRandomCard();
		BankCard bc2 = new BankCard("1111","111",pan_to_credit);
		BankTerminal bt = encountP2P.getRandomTerminal(currency);

		String result1 = sendp2p_old(bc1, bc2, bt, currency, amount);
		return result1;
	}
	
	private String sendp2p(BankCard bc1, BankCard bc2, BankTerminal bt,
			String currency, String amount) throws IOException, ISOException {

		// Define JPOS Channel
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc1.getPAN());
		// ONLINE
		//m.set(3, "260000");
		// ONLINE4
		m.set(3, "100000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc1.getExpDate());
		m.set(18, bt.getMCC());
		m.set(22, "012");
		m.set(28, "000000000");
		m.set(32, bt.getAcqInsId(bc1.getCardType()));
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(49, "643");
		m.set(62, "135791");
		// ONLINE3
		//m.set(103, bc2.getPAN());
		// ONLINE4
		m.set(104, "PP0020F216" + bc2.getPAN()); // DEC PP 0072 .....

		// Send message
		channel.send(m);
		System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}

	private String sendp2p_old(BankCard bc1, BankCard bc2, BankTerminal bt,
			String currency, String amount) throws IOException, ISOException {

		// Define JPOS Channel
		
		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc1.getPAN());
		// ONLINE
		m.set(3, "280000");
		// ONLINE4
		//m.set(3, "100000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc1.getExpDate());
		m.set(18, bt.getMCC());
		m.set(22, "012");
		m.set(28, "000000000");
		m.set(32, bt.getAcqInsId(bc1.getCardType()));
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(49, "643");
		m.set(62, "135791");
		// ONLINE3
		m.set(103, bc2.getPAN());
		// ONLINE4
		//m.set(104, "PP0020F216" + bc2.getPAN()); // DEC PP 0072 .....

		// Send message
		channel.send(m);
		System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}
	
	public String sendMCMS_db_cr(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc1 = encountMCMS.getRandomCard();
		BankCard bc2 = encountMCMS.getRandomCard();
		BankTerminal bt = encountMCMS.getRandomTerminal(currency);

		String result1 = sendMCMS(bc1, bc2, bt, currency, amount);
		if (result1.equals("00")) {
			String result2 = sendMCMS(bc2, bc1, bt, currency, amount);
			return result2;
		} else {
			return result1;
		}
	}

	public String sendMCMS_db_card(String currency, String amount, String pan_to_credit) throws IOException, ISOException {

		// Get random card
		BankCard bc1 = encountMCMS.getRandomCard();
		BankCard bc2 = new BankCard("1111","111",pan_to_credit);
		BankTerminal bt = encountMCMS.getRandomTerminal(currency);

		String result1 = sendMCMS(bc1, bc2, bt, currency, amount);
		return result1;
	}
	
	private static String sendMCMS(BankCard bc1, BankCard bc2, BankTerminal bt,
			String currency, String amount) throws IOException, ISOException {

		// Define JPOS Channel

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc1.getPAN());
		m.set(3, "100000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		////m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc1.getExpDate());
		m.set(18, bt.getMCC());
		////m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "012");
		////m.set(25, "00");

		m.set(32, bt.getAcqInsId(bc1.getCardType()));
		////m.set(35, bc1.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		////m.set(43, "MMM                      MCMS TransferRU");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		////m.set(53, "9701100001000000");
		m.set(104, "PP01700116REFERENCE11111110225ACCOUNT2222222222222222220314Rba Money Send0417Troitskaya ul. 170506MOSCOW07036432106112909221049522233442311TestTest999F80201F216" + bc2.getPAN() );
		// Send message
		channel.send(m);
		System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendVMT_db_cr(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc1 = encountVMT.getRandomCard();
		BankCard bc2 = encountVMT.getRandomCard();
		BankTerminal bt = encountVMT.getRandomTerminal(currency);

		String result1 = sendVMT(bc1, bc2, bt, currency, amount);
		if (result1.equals("00")) {
			String result2 = sendVMT(bc2, bc1, bt, currency, amount);
			return result2;
		} else {
			return result1;
		}
	}
	
	public String sendVMT_db_card(String currency, String amount, String pan_to_credit) throws IOException, ISOException {

		// Get random card
		BankCard bc1 = encountVMT.getRandomCard();
		BankCard bc2 = new BankCard("1111","111",pan_to_credit);
		BankTerminal bt = encountVMT.getRandomTerminal(currency);
		
		String result1 = sendVMT(bc1, bc2, bt, currency, amount);
		return result1;
	}
	
	private static String sendVMT(BankCard bc1, BankCard bc2, BankTerminal bt,
			String currency, String amount) throws IOException, ISOException {

		// Define JPOS Channel

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc1.getPAN());
		m.set(3, "100000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		////m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));

		m.set(18, bt.getMCC());
		//m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "901");
		m.set(25, "00");

		m.set(32, bt.getAcqInsId(bc1.getCardType()));
		m.set(35, bc1.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		////m.set(43, "MMM                      VMT TransferRU");
		////m.set(48,"P2PMT0110REFERENCE1111111021CACCOUNT222222222222222222222030ERba Money Send0411Troitskaya ul. 170506MOSCOW07036432402012106129090");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		////m.set(53, "9701100001000000");
		////m.set(103, bc2.getPAN());
		m.set(104, "PP01700116REFERENCE11111110225ACCOUNT2222222222222222220314Rba Money Send0417Troitskaya ul. 170506MOSCOW07036432106112909221049522233442311TestTest999F80201F216" + bc2.getPAN() );
		// Send message
		channel.send(m);
		System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		
		return resp.getString(39);
	}

}
