package ru.somecompany.bankcards.loadscripts.formats;

import java.io.IOException;
import java.io.InputStream;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;

import org.jpos.iso.*;
import org.jpos.iso.packager.GenericPackager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.somecompany.bankcards.loadscripts.channels.CustomASCIIChannel;
import ru.cinimex.bankcards.loadscripts.config.*;
import ru.cinimex.bankcards.loadscripts.encounter.*;
import ru.cinimex.bankcards.loadscripts.loaddata.*;
import ru.somecompany.bankcards.loadscripts.config.ConfigManager;
import ru.somecompany.bankcards.loadscripts.config.Port;
import ru.somecompany.bankcards.security.Encripter;
import ru.somecompany.bankcards.loadscripts.encounter.Encounter;
import ru.somecompany.bankcards.loadscripts.encounter.Environment;
import ru.somecompany.bankcards.loadscripts.loaddata.BankCard;
import ru.somecompany.bankcards.loadscripts.loaddata.BankTerminal;
import ru.somecompany.bankcards.loadscripts.loaddata.DataReader;

public class DHISender {

	private static final Logger log = LoggerFactory.getLogger(DHISender.class);

	@SuppressWarnings("unchecked")
	public DHISender(String scenarioName) {
		this.scenarioName = scenarioName;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		portmap = (HashMap<String, Port>) ConfigManager.getPortsMap();
		// Form Ecounter
		encountATM = new Encounter("ATM", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());
		encountServicePay = new Encounter("SPAY", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());
		encountMCMS = new Encounter("MCMS", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());
		encountVMT = new Encounter("VMT", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());
		encountP2P = new Encounter("P2P", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());

		packager =  getClass().getClassLoader().getResourceAsStream("DHI.xml");
	}

	@SuppressWarnings("unchecked")
	public DHISender() {
		this.scenarioName = null;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		// Form Ecounter
		encountATM = new Encounter("ATM", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());
		encountServicePay = new Encounter("SPAY", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());
		encountMCMS = new Encounter("MCMS", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());
		encountVMT = new Encounter("VMT", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());	encountP2P = new Encounter("P2P", (HashMap<String, Port>) portmap.clone(), cardmap,
				(HashMap<String, BankTerminal>) terminalmap.clone());

		packager =  getClass().getClassLoader().getResourceAsStream("DHI.xml");
	}

	private InputStream packager;

	private String scenarioName;

	// Date variables declaration block
	private static DateFormat dateFormatFull = new SimpleDateFormat("MMddHHmmss");
	private static DateFormat dateFormatDate = new SimpleDateFormat("MMdd");
	private static DateFormat dateFormatTime = new SimpleDateFormat("HHmmss");

	// HashMap declaration block
	private HashMap<String, BankCard> cardmap;
	private HashMap<String, BankTerminal> terminalmap;
	private HashMap<String, Port> portmap;

	// Ecounter declaration block
	private Encounter encountATM;
	private Encounter encountServicePay;
	private Encounter encountMCMS;
	private Encounter encountVMT;
	private Encounter encountP2P;

	private static ISOChannel channel;

	public String sendNetworkSignOn() throws IOException, ISOException {

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0800");

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);

		m.set(37, STAN + dateFormatTime.format(date));
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

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);

		m.set(37, STAN + dateFormatTime.format(date));
		m.set(70, "002");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		
		return resp.getString(39);
	}

	public void connect() throws IOException, ISOException {

		Port port = encountATM.getRandomPort();
		log.info("Trying to connect to server: " + port.getServerName() + " port: " + port.getServerPort());

		//InputStream instr = DHISender.class.getResourceAsStream("/DHI.xml");
		channel = new CustomASCIIChannel(port.getServerName(),
			Integer.parseInt(port.getServerPort()), new GenericPackager(
					packager), Integer.parseInt(port.getHeaderLength()),
			port.getStartSymbol());
		/*channel = new NACChannel(
				port.getServerName(), Integer.parseInt(port.getServerPort()),
				new GenericPackager(packager), Encripter.hexToBytes("")
		);*/

		channel.connect();
	}

	public void connect_local() throws IOException, ISOException {

		Port port = encountATM.getRandomPort();
		channel = new CustomASCIIChannel(port.getServerName(),
				Integer.parseInt(port.getServerPort()), new GenericPackager(packager), Integer.parseInt(port.getHeaderLength()),
				port.getStartSymbol());
		
		channel.connect();
		
	}
	
	public void disconnect() throws IOException, ISOException {

		channel.disconnect();

	}

	public String sendBalanceInquiry(String currency) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "300000");
		m.set(4, "000000000000");

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "9010");
		m.set(25, "00");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "RAIF                     MOSCOW       RU");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "0001010000000000");
		m.set(60, "2200001000");

		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		
		return resp.getString(39);
	}

	public String sendBalanceInquiry1(String currency) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "300000");
		m.set(4, "000000000000");

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "9010");
		//m.set(22, "9110");
		//m.set(22, "0510");
		m.set(25, "00");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "RAIF                     MOSCOW       RU");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		//byte[] encpin = {1, 5, 9, 33, 22, 7 ,8, 99};
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "0001010000000000");
		m.set(60, "2800001000");

		// Send message
		channel.send(m);
		System.out.println(m.getString(35));
		System.out.println(m.getString(41));
		System.out.println(m.getString(52));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22), m.getString(41), m.getString(42),  m.getString(32));
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39),resp.getString(54));
				
		return resp.getString(39);
	}

	public String sendBalanceInquiry_nopin(String currency) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		// Fill message fields
		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "300000");
		m.set(4, "000000000000");

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "9010");
		//m.set(22, "0510");
		m.set(25, "00");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "RAIF                     MOSCOW       RU");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		//m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "0001010000000000");
		m.set(60, "2200001000");

		// Send message
		channel.send(m);
		System.out.printf("### sendBalanceInquiry_nopin:\nTRACK2: %s\nMerchant ID: %s\nTerminal ID: %s\nPIN: %s\n",
				m.getString(35), m.getString(42), m.getString(41), m.getString(52) );
		//System.out.printf("REQ: %s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
		//		m.getString(22),m.getString(42),m.getString(41) );
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		//System.out.printf("RSP: %s|%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
		//		resp.getString(38),resp.getString(39),resp.getString(54));
				
		return resp.getString(39);
	}

	public String sendServicePayment(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountServicePay.getRandomCard();
		BankTerminal bt = encountServicePay.getRandomTerminal(currency);

		// Define JPOS Channel

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
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		//m.set(18, "5999");
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "9010");
		m.set(25, "00");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "RAIF                     MOSCOW       RU");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "0001010000000000");
		m.set(60, "2500001000");
		m.set(120, "USRDTMTS9166817227");
		// Send message
		System.out.println(m.getString(35));
		System.out.println(m.getString(41));
		System.out.println(m.getString(52));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41) );
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}

	public String sendServicePaymentReversal(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountServicePay.getRandomCard();
		BankTerminal bt = encountServicePay.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "9010");
		m.set(25, "00");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "2001010100000000");
		m.set(60, "2500001000");
		m.set(120, "MTS9166817227");		
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

			cal = Calendar.getInstance();
			date = cal.getTime();
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, bc.getExpDate());
			m.set(18, bt.getMCC());
			m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(22, "9010");
			m.set(25, "00");
			m.set(32, bt.getAcqInsId(bc.getCardType()));
			m.set(35, bc.getTrack2());
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(39, "00");
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());
			m.set(43, "                         ATM1 Transfer  ");
			m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			encpin = Encripter.encriptTDES(Encripter.formTDESKey(bt.getTPK()),
					Encripter.formPINBlock(bc));
			m.set(52, Arrays.copyOfRange(encpin, 0, 8));
			m.set(53, "2001010100000000");
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

	public String sendOnLineCredit(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");	// ONLINE4
//		m.setMTI("0220");	// ONLINE3
		m.set(2, bc.getPAN());
		m.set(3, "500000");	// ONLINE4
		//m.set(3, "200000");	// ONLINE3
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(22, "9010");

		m.set(25, "00");

		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		m.set(48, "USRDT|ONLINECREDIT");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		
		m.set(53, "2001010100000000");
		// Send message
		channel.send(m);
		System.out.println(m.getString(35));
		System.out.println(m.getString(41));
		System.out.println(m.getString(52));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41) );
		
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}

	public String sendOnLineCredit_nopin(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");	// ONLINE4
//		m.setMTI("0220");	// ONLINE3
		m.set(2, bc.getPAN());
		m.set(3, "500000");	// ONLINE4
		//m.set(3, "200000");	// ONLINE3
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(22, "9010");

		m.set(25, "00");

		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		m.set(48, "USRDT|ONLINECREDIT");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		//m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		
		m.set(53, "2001010100000000");
		// Send message
		channel.send(m);
		System.out.printf("### sendOnLineCredit_nopin:\nTRACK2: %s\nMerchant ID: %s\nTerminal ID: %s\nPIN: %s\n",
				m.getString(35), m.getString(42), m.getString(41), m.getString(52) );
		//System.out.printf("REQ: %s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
		//		m.getString(22),m.getString(42),m.getString(41) );
		
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		//System.out.printf("RSP: %s|%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
		//		resp.getString(38),resp.getString(39),resp.getString(54));

		return resp.getString(39);
	}

	public String sendOnLineCreditReversal(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "500000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(22, "9010");

		m.set(25, "00");

		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		m.set(48, "USRDT|ONLINECREDIT");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "2001010100000000");
		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();


		if (resp.getString(39).equals("00")) {
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			
			// Create JPOS message
			m = new ISOMsg();

			// Fill message fields
			m.setMTI("0400");
			m.set(2, bc.getPAN());
			m.set(3, "500000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));

			cal = Calendar.getInstance();
			date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, bc.getExpDate());
			m.set(18, bt.getMCC());
			m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			m.set(22, "9010");

			m.set(25, "00");
			m.set(32, bt.getAcqInsId(bc.getCardType()));
			m.set(35, bc.getTrack2());
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(39, "00");
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());
			m.set(43, "                         ATM1 Transfer  ");
			m.set(48, "USRDT|ONLINECREDIT");
			m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			encpin = Encripter.encriptTDES(Encripter.formTDESKey(bt.getTPK()),
					Encripter.formPINBlock(bc));
			m.set(52, Arrays.copyOfRange(encpin, 0, 8));
			m.set(53, "2001010100000000");

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

	public int sendOnLineCreditLateReversal(int iterationCount, String currency, String amount)
			throws IOException, ISOException {

		HashMap<String, BankCard> historyBC = new HashMap<String, BankCard>();
		HashMap<String, BankTerminal> historyT = new HashMap<String, BankTerminal>();
		HashMap<String, String> historyAC = new HashMap<String, String>();

		for (int i = 1; i <= iterationCount; i++) {

			// Get random card
			BankCard bc = encountATM.getRandomCard();
			BankTerminal bt = encountATM.getRandomTerminal(currency);
			Port port = encountATM.getRandomPort();

			// Define JPOS Channel
			
			// Create JPOS message
			ISOMsg m = new ISOMsg();

			m.setMTI("0100");
			m.set(2, bc.getPAN());
			m.set(3, "500000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));

			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			String STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, bc.getExpDate());
			m.set(18, bt.getMCC());
			m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			m.set(22, "9010");

			m.set(25, "00");

			m.set(32, bt.getAcqInsId(bc.getCardType()));
			m.set(35, bc.getTrack2());
			m.set(37, STAN + dateFormatTime.format(date));

			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());
			m.set(43, "                         ATM1 Transfer  ");
			m.set(48, "USRDT|ONLINECREDIT");
			m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			byte[] encpin = Encripter.encriptTDES(
					Encripter.formTDESKey(bt.getTPK()),
					Encripter.formPINBlock(bc));
			m.set(52, Arrays.copyOfRange(encpin, 0, 8));
			m.set(53, "2001010100000000");
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
			Port port = encountATM.getRandomPort();

			// Define JPOS Channel
			//InputStream instr = DHISender.class.getResourceAsStream("/DHI.xml");
			ISOChannel channel = new CustomASCIIChannel(port.getServerName(),
					Integer.parseInt(port.getServerPort()),
					new GenericPackager(packager), Integer.parseInt(port
							.getHeaderLength()), port.getStartSymbol());

			channel.connect();
			// Create JPOS message
			ISOMsg m = new ISOMsg();

			// Fill message fields
			m.setMTI("0410");
			m.set(2, historyBC.get(pairs.getKey()).getPAN());
			m.set(3, "500000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));

			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			String STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, historyBC.get(pairs.getKey()).getExpDate());
			m.set(18, historyT.get(pairs.getKey()).getMCC());
			m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			m.set(22, "9010");

			m.set(25, "00");
			m.set(32, historyT.get(pairs.getKey()).getAcqInsId(historyBC.get(pairs.getKey()).getCardType()));
			m.set(35, historyBC.get(pairs.getKey()).getTrack2());
			m.set(37, pairs.getKey().toString());
			m.set(38, historyAC.get(pairs.getKey()));
			m.set(39, "00");
			m.set(41, historyT.get(pairs.getKey()).getTerminalId());
			m.set(42, historyT.get(pairs.getKey()).getMerchantId());
			m.set(43, "                         ATM1 Transfer  ");
			m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			byte[] encpin = Encripter.encriptTDES(Encripter
					.formTDESKey(historyT.get(pairs.getKey()).getTPK()),
					Encripter.formPINBlock(historyBC.get(pairs.getKey())));
			m.set(52, Arrays.copyOfRange(encpin, 0, 8));
			m.set(53, "2001010100000000");

			// Send message
			channel.send(m);
			System.out.println(ISOUtil.hexdump(m.pack()));

			// Receive message
			@SuppressWarnings("unused")
			ISOMsg resp = channel.receive();
		}

		return result;
	}

	public String sendCashAdvance(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0200");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(22, "9010");
		//m.set(22, "9210");
		
		m.set(25, "00");
		//m.set(28, "000000000");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "2001010100000000");
		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}

	public String sendCashAdvance_nopin(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0200");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(22, "9010");
		
		m.set(25, "00");
		//m.set(28, "000000000");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		//m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "2001010100000000");
		// Send message
		channel.send(m);
		System.out.printf("### sendCashAdvance_nopin:\nTRACK2: %s\nMerchant ID: %s\nTerminal ID: %s\nPIN: %s\n",
				m.getString(35), m.getString(42), m.getString(41), m.getString(52) );
		//System.out.printf("REQ: %s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
		//		m.getString(22),m.getString(42),m.getString(41) );
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		//System.out.printf("RSP: %s|%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
		//		resp.getString(38),resp.getString(39),resp.getString(54));

		return resp.getString(39);
	}

	
	public String sendCashAdvanceReversal(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0200");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(22, "9010");

		m.set(25, "00");
		//m.set(28, "000000000");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "2001010100000000");
		// Send message
		channel.send(m);
		//System.out.println(m.getString(35));
		//System.out.println(m.getString(41));
		//System.out.println(m.getString(52));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52));
		//System.out.println(ISOUtil.hexdump(m.pack()));
		

		// Receive message
		ISOMsg resp = channel.receive();
		System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39));


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
			m.set(3, "010000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));

			cal = Calendar.getInstance();
			date = cal.getTime();
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, bc.getExpDate());
			m.set(18, bt.getMCC());
			m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			m.set(22, "9010");

			m.set(25, "00");
			m.set(28, "000000000");
			m.set(32, bt.getAcqInsId(bc.getCardType()));
			m.set(35, bc.getTrack2());
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(39, "17");
			m.set(41, bt.getTerminalId());
			m.set(42, bt.getMerchantId());
			m.set(43, "                         ATM1 Transfer  ");
			m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			encpin = Encripter.encriptTDES(Encripter.formTDESKey(bt.getTPK()),
					Encripter.formPINBlock(bc));
			m.set(52, Arrays.copyOfRange(encpin, 0, 8));
			m.set(53, "2001010100000000");

			// Send message
			channel.send(m);
			//System.out.println(ISOUtil.hexdump(m.pack()));
			//System.out.println(m.getString(35));
			//System.out.println(m.getString(41));
			//System.out.println(m.getString(39));
			System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
					m.getString(22),m.getString(42),m.getString(41),m.getString(39));

			// Receive message
			resp = channel.receive();
			System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
					resp.getString(38),resp.getString(39));
			
		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}

	public int sendCashAdvanceLateReversal(int iterationCount, String currency, String amount) throws IOException, ISOException {

		HashMap<String, BankCard> historyBC = new HashMap<String, BankCard>();
		HashMap<String, BankTerminal> historyT = new HashMap<String, BankTerminal>();
		HashMap<String, String> historyAC = new HashMap<String, String>();

		for (int i = 1; i <= iterationCount; i++) {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0200");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(22, "9010");

		m.set(25, "00");
		m.set(28, "000000000");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "2001010100000000");
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

			// Create JPOS message
			ISOMsg m = new ISOMsg();

			// Fill message fields
			m.setMTI("0400");
			m.set(2, historyBC.get(pairs.getKey()).getPAN());
			m.set(3, "010000");
			m.set(4, String.format("%012d", Integer.parseInt(amount)));

			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			String STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, historyBC.get(pairs.getKey()).getExpDate());
			m.set(18, historyT.get(pairs.getKey()).getMCC());
			m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			m.set(22, "9010");

			m.set(25, "00");
			m.set(28, "000000000");
			m.set(32, historyT.get(pairs.getKey()).getAcqInsId(historyBC.get(pairs.getKey()).getCardType()));
			m.set(35, historyBC.get(pairs.getKey()).getTrack2());
			m.set(37, pairs.getKey().toString());
			m.set(38, historyAC.get(pairs.getKey()));
			m.set(39, "00");
			m.set(41, historyT.get(pairs.getKey()).getTerminalId());
			m.set(42, historyT.get(pairs.getKey()).getMerchantId());
			m.set(43, "                         ATM1 Transfer  ");
			m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			byte[] encpin = Encripter.encriptTDES(Encripter.formTDESKey(historyT.get(pairs.getKey()).getTPK()),
					Encripter.formPINBlock(historyBC.get(pairs.getKey())));
			m.set(52, Arrays.copyOfRange(encpin, 0, 8));
			m.set(53, "2001010100000000");

			// Send message
			channel.send(m);
			//System.out.println(ISOUtil.hexdump(m.pack()));

			// Receive message
			@SuppressWarnings("unused")
			ISOMsg resp = channel.receive();

	}

	return result;
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

	private static String sendp2p(BankCard bc1, BankCard bc2, BankTerminal bt,
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
		// m.set(7,dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc1.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "9010");
		m.set(25, "00");
		m.set(28, "000000000");
		m.set(32, bt.getAcqInsId(bc1.getCardType()));
		m.set(35, bc1.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         RBRU P2P Tester");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		
		//byte[] encpin = Encripter.encriptTDES(
		//		Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc1));
		//m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		
		m.set(53, "2001010100000000");
		//m.set(103, bc2.getPAN());
		//m.set(48,"PP0110REFERENCE1111111021CACCOUNT222222222222222222222030ERba Money Send0411Troitskaya ul. 170506MOSCOW07036432402012106129090");
		//m.set(104, "PP00720116REFERENCE11111110228ACCOUNT222222222222222222222F2164469170000001039"); // DEC PP 0072 .....
		m.set(104, "PP00720116REFERENCE11111110228ACCOUNT222222222222222222222F216" + bc2.getPAN()); // DEC PP 0072 .....
		
		//m.set( 104,"PP01700106G21265020727585530314ANTON TUZHILOV 04 47 119620, Moskva g, Aviatorov ul, dom 30, kv. 3900506MOSCOW070364321061196202210929958589623043519F80201F216"  + bc2.getPAN() );
		
		
		
		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		
		System.out.println("");
		System.out.println(m.getString(35));
		//System.out.println(m.getString(41));
		//System.out.println(m.getString(52));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", 
				m.getMTI(), m.getString(3), m.getString(2), m.getString(37),m.getString(22),
				m.getString(18),m.getString(42),m.getString(41),
				m.getString(49),m.getString(4));

		// Receive message
		ISOMsg resp = channel.receive();
		
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", 
				resp.getMTI(), resp.getString(3), m.getString(2),resp.getString(37),resp.getString(22),
				m.getString(18),m.getString(42),m.getString(41),
				m.getString(49),m.getString(4),resp.getString(38),resp.getString(39),resp.getString(54));

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
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));

		m.set(18, bt.getMCC());
		//m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		//m.set(22, "9010");
		m.set(22, "012");
		m.set(25, "00");

		m.set(32, bt.getAcqInsId(bc1.getCardType()));
		m.set(35, bc1.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "MMM                      MCMS TransferRU");
		//m.set(48,"P2PMT0110REFERENCE1111111021CACCOUNT222222222222222222222030ERba Money Send0411Troitskaya ul. 170506MOSCOW07036432402012106129090");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		byte[] encpin = Encripter
				.encriptTDES(Encripter.formTDESKey(bt.getTPK()),
						Encripter.formPINBlock(bc1));
		////m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "9701100001000000");
		//m.set(103, bc2.getPAN());
		
		//m.set(48,"PP0110REFERENCE1111111021CACCOUNT222222222222222222222030ERba Money Send0411Troitskaya ul. 170506MOSCOW07036432402012106129090");
		//m.set(104, "PP00720116REFERENCE11111110228ACCOUNT222222222222222222222F2164469170000001039"); // DEC PP 0072 .....
		m.set(104, "PP01700116REFERENCE11111110225ACCOUNT2222222222222222220314Rba Money Send0417Troitskaya ul. 170506MOSCOW07036432106112909221049522233442311TestTest999F80201F216" + bc2.getPAN() );


		System.out.println("");
		System.out.println(m.getString(35));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", 
				m.getMTI(), m.getString(3), m.getString(2), m.getString(37),m.getString(22),
				m.getString(18),m.getString(42),m.getString(41),
				m.getString(49),m.getString(4));
		
		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));
		
		// Receive message
		ISOMsg resp = channel.receive();

		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", 
				resp.getMTI(), resp.getString(3), m.getString(2),resp.getString(37),resp.getString(22),
				m.getString(18),m.getString(42),m.getString(41),
				m.getString(49),m.getString(4),resp.getString(38),resp.getString(39),resp.getString(54));

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
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));

		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(22, "9010");
		m.set(25, "00");

		m.set(32, bt.getAcqInsId(bc1.getCardType()));
		m.set(35, bc1.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "MMM                      VMT TransferRU");
		//m.set(48,"P2PMT0110REFERENCE1111111021CACCOUNT222222222222222222222030ERba Money Send0411Troitskaya ul. 170506MOSCOW07036432402012106129090");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		/*
		byte[] encpin = Encripter
				.encriptTDES(Encripter.formTDESKey(bt.getTPK()),
						Encripter.formPINBlock(bc1));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		*/
		m.set(53, "9701100001000000");
		//m.set(103, bc2.getPAN());
		
		//m.set(104, "PP01680116REFERENCE11111110225ACCOUNT2222222222222222220312Rba Visa VPP0417Troitskaya ul. 170506MOSCOW07036432106112909221049522233442311TestTest999F80201F216" + bc2.getPAN() );
		m.set(104, "PP01820116REFERENCE11111110225ACCOUNT2222222222222222220312Rba Visa VPP0417Troitskaya ul. 170506MOSCOW07036432106112909221049522233442311TestTest999F802010A10PERPENDROTF216" + bc2.getPAN() );
		
		System.out.println("");
		System.out.println(m.getString(35));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", 
				m.getMTI(), m.getString(3), m.getString(2), m.getString(37),m.getString(22),
				m.getString(18),m.getString(42),m.getString(41),
				m.getString(49),m.getString(4));
		
		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();
		
		
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", 
				resp.getMTI(), resp.getString(3), m.getString(2),resp.getString(37),resp.getString(22),
				m.getString(18),m.getString(42),m.getString(41),
				m.getString(49),m.getString(4),resp.getString(38),resp.getString(39),resp.getString(54));

		return resp.getString(39);
	}

	public String sendPinChange(String currency) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "900000");
		
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(22, "9010");
		

		m.set(25, "00");
		
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		
		// 0BEB2C3005BDB413
		m.set(48, "PINCHNG0BEB2C3005BDB413");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "2001010100000000");
		// Send message
		channel.send(m);
		
		System.out.println(m.getString(35));
		System.out.println(m.getString(41));
		System.out.println(m.getString(52));
		System.out.println(m.getString(48));
		System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", m.getMTI(), m.getString(3), m.getString(2), m.getString(37),
				m.getString(22),m.getString(42),m.getString(41),m.getString(52), m.getString(48));
		
		System.out.println(ISOUtil.hexdump(m.pack()));

		
		// Receive message
		ISOMsg resp = channel.receive();
		System.out.printf("%s|%s|%s|%s|%s|%s|\n", resp.getMTI(), resp.getString(3), m.getString(2), resp.getString(37),
				resp.getString(38),resp.getString(39));

		return resp.getString(39);
	}

	public String sendCashAdvanceContactless(String currency, String amount) throws IOException, ISOException {

		// Get random card
		BankCard bc = encountATM.getRandomCard();
		BankTerminal bt = encountATM.getRandomTerminal(currency);

		// Create JPOS message
		ISOMsg m = new ISOMsg();

		m.setMTI("0200");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amount)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(18, bt.getMCC());
		m.set(19, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		//m.set(22, "9010");
		m.set(22, "9110");

		m.set(25, "00");
		//m.set(28, "000000000");
		m.set(32, bt.getAcqInsId(bc.getCardType()));
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));

		m.set(41, bt.getTerminalId());
		m.set(42, bt.getMerchantId());
		m.set(43, "                         ATM1 Transfer  ");
		m.set(49, currency.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(bt.getTPK()), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));
		m.set(53, "2001010100000000");
		// Send message
		channel.send(m);
		//System.out.println(ISOUtil.hexdump(m.pack()));

		// Receive message
		ISOMsg resp = channel.receive();

		return resp.getString(39);
	}
}
