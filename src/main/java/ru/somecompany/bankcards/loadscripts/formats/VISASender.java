package ru.somecompany.bankcards.loadscripts.formats;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.VAPChannel;

import ru.somecompany.bankcards.loadscripts.config.ConfigManager;
import ru.somecompany.bankcards.loadscripts.encounter.Encounter;
import ru.somecompany.bankcards.loadscripts.encounter.Environment;
import ru.somecompany.bankcards.loadscripts.loaddata.BankCard;
import ru.somecompany.bankcards.loadscripts.loaddata.BankTerminal;
import ru.somecompany.bankcards.loadscripts.loaddata.DataReader;
import ru.somecompany.bankcards.security.Encripter;

public class VISASender {

	public VISASender(String scenarioName) {
		this.scenarioName = scenarioName;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		// Form Ecounter
		encountPOS = new Encounter("POS", ConfigManager.getPortsMap(), cardmap,
				terminalmap);
		encountATM = new Encounter("ATM", ConfigManager.getPortsMap(), cardmap,
				terminalmap);
	}

	public VISASender() {
		this.scenarioName = null;
		// Form HashMap
		cardmap = DataReader.getBankCardMap(scenarioName);
		terminalmap = DataReader.getTerminalMap(scenarioName);
		// Form Ecounter
		encountPOS = new Encounter("POS", ConfigManager.getPortsMap(), cardmap,
				terminalmap);
		encountATM = new Encounter("ATM", ConfigManager.getPortsMap(), cardmap,
				terminalmap);
	}

	private String scenarioName;

	// Date variables declaration block
	private static DateFormat dateFormatFull = new SimpleDateFormat(
			"MMddHHmmss");
	// private static DateFormat dateFormatDate = new SimpleDateFormat("MMdd");
	private static DateFormat dateFormatTime = new SimpleDateFormat("HHmmss");
	// private static DateFormat dateFormatDDMMYY = new
	// SimpleDateFormat("MMDDYY");

	// HashMap declaration block
	private HashMap<String, BankCard> cardmap;
	private HashMap<String, BankTerminal> terminalmap;

	// Ecounter declaration block
	private Encounter encountPOS;
	private Encounter encountATM;

	public String sendPurchase(VAPChannel channel, String currencyTrxn, String amountTrxn) throws IOException, ISOException {

		BankCard bc = encountPOS.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, "5999");
		m.set(19, "840");
		m.set(22, "9020");
		m.set(25, "00");
		m.set(28, "D00000000");
		m.set(32, "12345678901");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "TERMID01");
		m.set(42, "CARD ACCEPTOR  ");
		m.set(43, "ACQUIRER NAME            CITY NAME    US");
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(59, ISOUtil.hex2byte("0600094044"));
		m.set(60, ISOUtil.hex2byte("42"));
		m.set("62.1", "Y");
		m.set("62.2", ISOUtil.hex2byte("000000000000000"));
		m.set("63.1", ISOUtil.hex2byte("0002"));

		m.set(123, ISOUtil.hex2byte("123456789555 State Street"));
		channel.send(m);
		
		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendPurchaseReversal(VAPChannel channel, String currencyTrxn, String amountTrxn) throws IOException, ISOException {

		BankCard bc = encountPOS.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, "5999");
		m.set(19, "840");
		m.set(22, "9020");
		m.set(25, "00");
		m.set(28, "D00000000");
		m.set(32, "12345678901");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "TERMID01");
		m.set(42, "CARD ACCEPTOR  ");
		m.set(43, "ACQUIRER NAME            CITY NAME    US");
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(59, ISOUtil.hex2byte("0600094044"));
		m.set(60, ISOUtil.hex2byte("42"));
		m.set("62.1", "Y");
		m.set("62.2", ISOUtil.hex2byte("000000000000000"));
		m.set("63.1", ISOUtil.hex2byte("0002"));

		m.set(123, ISOUtil.hex2byte("123456789555 State Street"));

		channel.send(m);
		
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
			m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
			cal = Calendar.getInstance();
			date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(14, bc.getExpDate());
			m.set(18, "5999");
			m.set(19, "840");
			m.set(22, "9020");
			m.set(25, "00");
			m.set(28, "C00000000");
			m.set(32, "12345678901");
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(41, "TERMID01");
			m.set(42, "CARD ACCEPTOR  ");
			m.set(43, "ACQUIRER NAME            CITY NAME    US");
			m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(59, ISOUtil.hex2byte("0600094044"));
			m.set(60, ISOUtil.hex2byte("42"));
			m.set("62.1", "Y");
			m.set("62.2", ISOUtil.hex2byte("000000000000000"));
			m.set("63.1", ISOUtil.hex2byte("0002"));
			m.set("63.3", ISOUtil.hex2byte("2501"));
			//
			m.set(90, "01000000000000000000" + "12345678901" + "00000000000");
			//
			channel.send(m);
			
			// Receive message
			resp = channel.receive();
			channel.disconnect();
		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}

	public String sendCashAdvance(VAPChannel channel, String currencyTrxn, String amountTrxn) throws IOException, ISOException {

		BankCard bc = encountATM.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, "6011");
		m.set(19, "840");
		m.set(22, "9010");
		m.set(25, "00");
		m.set(28, "D00000050");
		m.set(32, "12345678901");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "TERMID01");
		m.set(42, "CARD ACCEPTOR  ");
		m.set(43, "ACQUIRER NAME            CITY NAME    US");
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(Environment.ZMK), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));

		m.set(53, "2001010100000000");
		//
		m.set(60, ISOUtil.hex2byte("22"));
		m.set("63.1", ISOUtil.hex2byte("0002"));
		channel.send(m);
		
		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendCashAdvanceReversal(VAPChannel channel, String currencyTrxn, String amountTrxn) throws IOException, ISOException {

		BankCard bc = encountATM.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, "6011");
		m.set(19, "840");
		m.set(22, "9010");
		m.set(25, "00");
		m.set(28, "D00000050");
		m.set(32, "12345678901");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "TERMID01");
		m.set(42, "CARD ACCEPTOR  ");
		m.set(43, "ACQUIRER NAME            CITY NAME    US");
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(Environment.ZMK), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));

		m.set(53, "2001010100000000");
		//
		m.set(60, ISOUtil.hex2byte("22"));
		m.set("63.1", ISOUtil.hex2byte("0002"));
		channel.send(m);
		
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
			m.set(3, "014000");
			m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
			cal = Calendar.getInstance();
			date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(14, bc.getExpDate());
			m.set(18, "6011");
			m.set(19, "840");
			m.set(22, "9010");
			m.set(25, "00");
			m.set(32, "12345678901");
			m.set(37, resp.getString(37));
			m.set(41, "TERMID01");
			m.set(42, "CARD ACCEPTOR  ");
			m.set(43, "ACQUIRER NAME            CITY NAME    US");
			m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(60, ISOUtil.hex2byte("22"));
			m.set("63.1", ISOUtil.hex2byte("0002"));
			m.set("63.3", ISOUtil.hex2byte("2501"));
			//
			m.set(90, "01000000000000000000" + "12345678901" + "00000000000");
			//
			channel.send(m);
			
			// Receive message
			resp = channel.receive();
			channel.disconnect();
		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}
	
	public String sendBalanceInquiry(VAPChannel channel, String currencyTrxn) throws IOException, ISOException {

		BankCard bc = encountATM.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "302000");

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, "6011");
		m.set(19, "840");
		m.set(22, "9010");
		m.set(25, "00");
		m.set(28, "D00000000");
		m.set(32, "12345678901");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "TERMID01");
		m.set(42, "CARD ACCEPTOR  ");
		m.set(43, "ACQUIRER NAME            CITY NAME    US");
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(Environment.ZMK), Encripter.formPINBlock(bc));
		m.set(52, Arrays.copyOfRange(encpin, 0, 8));

		m.set(53, "2001010100000000");
		//
		m.set(60, ISOUtil.hex2byte("22"));
		m.set("63.1", ISOUtil.hex2byte("0002"));
		channel.send(m);
		
		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendAdvice(VAPChannel channel, String currencyTrxn, String amountTrxn) throws IOException, ISOException {

		BankCard bc = encountPOS.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0120");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, "6011");
		m.set(19, "840");
		m.set(22, "9020");
		m.set(25, "00");
		m.set(28, "D00000000");
		m.set(32, "12345678901");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "TERMID01");
		m.set(42, "CARD ACCEPTOR  ");
		m.set(43, "ACQUIRER NAME            CITY NAME    US");
		m.set(49, "840");
		m.set(59, ISOUtil.hex2byte("0600094044"));
		//
		m.set(60, ISOUtil.hex2byte("42"));
		m.set("62.1", "Y");
		m.set("62.2", ISOUtil.hex2byte("000000000000000"));
		m.set("63.1", ISOUtil.hex2byte("0002"));

		m.set(123, ISOUtil.hex2byte("123456789555 State Street"));
		channel.send(m);
		
		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendAdviceReversal(VAPChannel channel, String currencyTrxn, String amountTrxn) throws IOException, ISOException {

		BankCard bc = encountPOS.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0420");
		m.set(2, bc.getPAN());
		m.set(3, "000000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(14, bc.getExpDate());
		m.set(18, "6011");
		m.set(19, "840");
		m.set(22, "9020");
		m.set(25, "00");
		m.set(28, "D00000000");
		m.set(32, "12345678901");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "TERMID01");
		m.set(42, "CARD ACCEPTOR  ");
		m.set(43, "ACQUIRER NAME            CITY NAME    US");
		m.set(49, "840");
		m.set(59, ISOUtil.hex2byte("0600094044"));
		//
		m.set(60, ISOUtil.hex2byte("42"));
		m.set("62.1", "Y");
		m.set("62.2", ISOUtil.hex2byte("000000000000000"));
		m.set("63.1", ISOUtil.hex2byte("0002"));

		m.set(123, ISOUtil.hex2byte("123456789555 State Street"));
		channel.send(m);
		
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
			m.set(3, "014000");
			m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
			cal = Calendar.getInstance();
			date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(14, bc.getExpDate());
			m.set(18, "6011");
			m.set(19, "840");
			m.set(22, "9010");
			m.set(25, "00");
			m.set(32, "12345678901");
			m.set(37, resp.getString(37));
			m.set(41, "TERMID01");
			m.set(42, "CARD ACCEPTOR  ");
			m.set(43, "ACQUIRER NAME            CITY NAME    US");
			m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(60, ISOUtil.hex2byte("22"));
			m.set("63.1", ISOUtil.hex2byte("0002"));
			m.set("63.3", ISOUtil.hex2byte("2501"));
			//
			m.set(90, "01000000000000000000" + "12345678901" + "00000000000");
			//
			channel.send(m);
			
			// Receive message
			resp = channel.receive();
			channel.disconnect();
		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}

}