package ru.somecompany.bankcards.loadscripts.formats;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.NACChannel;

import ru.somecompany.bankcards.loadscripts.config.ConfigManager;
import ru.somecompany.bankcards.loadscripts.encounter.Encounter;
import ru.somecompany.bankcards.loadscripts.encounter.Environment;
import ru.somecompany.bankcards.loadscripts.loaddata.BankCard;
import ru.somecompany.bankcards.loadscripts.loaddata.BankTerminal;
import ru.somecompany.bankcards.loadscripts.loaddata.DataReader;
import ru.somecompany.bankcards.security.Encripter;

public class MasterCardSender {

	public MasterCardSender(String scenarioName) {
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

	public MasterCardSender() {
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
	private static DateFormat dateFormatDate = new SimpleDateFormat("MMdd");
	private static DateFormat dateFormatTime = new SimpleDateFormat("HHmmss");

	// HashMap declaration block
	private HashMap<String, BankCard> cardmap;
	private HashMap<String, BankTerminal> terminalmap;

	// Ecounter declaration block
	private Encounter encountPOS;
	private Encounter encountATM;

	public String sendPurchase(NACChannel channel, String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {

		BankCard bc = encountPOS.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "003000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
		m.set(6, String.format("%012d", Integer.parseInt(amountBill)));
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		m.set(10, "70001046");
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(15, dateFormatDate.format(date));
		m.set(16, dateFormatDate.format(date));
		m.set(18, "5999");
		m.set(22, "902");
		m.set(32, "012345");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "12345678");
		m.set(42, "123456789123456");
		m.set(43, "FIRST THIRD BANK       ST. LOUIS     IDN");
		m.set(48, ISOUtil.hex2byte("52"));
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(51, currencyBill.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(61, "0000000000200360902101234");
		m.set(63, "MCC011104");
		channel.send(m);
		
		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendPurchaseReversal(NACChannel channel, String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {

		BankCard bc = encountPOS.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "003000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
		m.set(6, String.format("%012d", Integer.parseInt(amountBill)));
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		m.set(10, "70001046");
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(15, dateFormatDate.format(date));
		m.set(16, dateFormatDate.format(date));
		m.set(18, "5999");
		m.set(22, "902");
		m.set(32, "012345");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "12345678");
		m.set(42, "123456789123456");
		m.set(43, "FIRST THIRD BANK       ST. LOUIS     IDN");
		m.set(48, ISOUtil.hex2byte("52"));
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(51, currencyBill.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

		m.set(61, "0000000000200360902101234");
		m.set(63, "MCC011104");
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
			//
			m.set(90, "0100" + STAN + dateFormatFull.format(date) + "00000012345" + "00000000000");
			//
			m.set(2, bc.getPAN());
			m.set(3, "003000");
			m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
			m.set(6, String.format("%012d", Integer.parseInt(amountBill)));
			cal = Calendar.getInstance();
			date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			m.set(10, "70001046");
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(14, bc.getExpDate());
			m.set(15, dateFormatDate.format(date));
			m.set(16, dateFormatDate.format(date));
			m.set(18, "5999");
			m.set(22, "902");
			m.set(32, "012345");
			m.set(37, resp.getString(37));
			m.set(38, resp.getString(38));
			m.set(39, resp.getString(39));
			m.set(41, "12345678");
			m.set(42, "123456789123456");
			m.set(43, "FIRST THIRD BANK       ST. LOUIS     IDN");
			m.set(48, ISOUtil.hex2byte("523230303153363331354D4343303131313034303732322020"));
			m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(51, currencyBill.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));

			m.set(61, "0000000000200840902101234");
			m.set(63, "MCC011105");
			channel.send(m);
			
			// Receive message
			resp = channel.receive();
			channel.disconnect();
		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}

	public String sendCashAdvance(NACChannel channel, String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {

		BankCard bc = encountATM.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
		m.set(6, String.format("%012d", Integer.parseInt(amountBill)));
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		m.set(10, "78323000");
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(15, dateFormatDate.format(date));
		m.set(16, dateFormatDate.format(date));
		m.set(18, "6011");
		m.set(22, "901");
		m.set(26, "4");
		m.set(32, "012345");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "12345678");
		m.set(42, "123456789123456");
		m.set(43, "Member Financial Insti Boston         MA");
		m.set(48, ISOUtil.hex2byte("5A383030325456"));
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(51, currencyBill.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(Environment.ZMK), Encripter.formPINBlock(bc));
		//m.set(52, Arrays.copyOfRange(encpin, 0, 8));

		m.set(53, "9701010002000000");
		//
		m.set(61, "0010010000200840902101234");
		m.set(63, "MCC011112");
		channel.send(m);
		
		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendCashAdvanceReversal(NACChannel channel, String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {

		BankCard bc = encountATM.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
		m.set(6, String.format("%012d", Integer.parseInt(amountBill)));
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		m.set(10, "78323000");
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(15, dateFormatDate.format(date));
		m.set(16, dateFormatDate.format(date));
		m.set(18, "6011");
		m.set(22, "901");
		m.set(26, "4");
		m.set(32, "012345");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "12345678");
		m.set(42, "123456789123456");
		m.set(43, "Member Financial Insti Boston         MA");
		m.set(48, ISOUtil.hex2byte("5A383030325456"));
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(51, currencyBill.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(Environment.ZMK), Encripter.formPINBlock(bc));
		//m.set(52, Arrays.copyOfRange(encpin, 0, 8));

		m.set(53, "9701010002000000");
		//
		m.set(61, "0010010000200840902101234");
		m.set(63, "MCC011112");
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
			//
			m.set(90, "0100" + STAN + dateFormatFull.format(date) + "00000012345" + "00000000000");
			//
			m.set(2, bc.getPAN());
			m.set(3, "010000");
			m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
			m.set(6, String.format("%012d", Integer.parseInt(amountBill)));
			cal = Calendar.getInstance();
			date = cal.getTime();
			m.set(7, dateFormatFull.format(date));
			m.set(10, "78323000");
			STAN = Environment.getSTAN();
			m.set(11, STAN);
			m.set(12, dateFormatTime.format(date));
			m.set(13, dateFormatDate.format(date));
			m.set(15, dateFormatDate.format(date));
			m.set(16, dateFormatDate.format(date));
			m.set(18, "6011");
			m.set(22, "901");
			m.set(32, "012345");
			m.set(37, STAN + dateFormatTime.format(date));
			m.set(39, "06");
			m.set(41, "12345678");
			m.set(42, "123456789123456");
			m.set(43, "Member Financial Insti Boston         MA");
			m.set(48, ISOUtil.hex2byte("5A3230303150363331354D4343303131313132303732322020"));
			m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(51, currencyBill.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
			m.set(61, "0010010000200840902101234");
			m.set(63, "MCC011113");
			channel.send(m);
			
			// Receive message
			resp = channel.receive();
			channel.disconnect();
		} else {
			return resp.getString(39);
		}
		return resp.getString(39);
	}
	
	public String sendBalanceInquiry(NACChannel channel, String currencyTrxn, String currencyBill) throws IOException, ISOException {

		BankCard bc = encountATM.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0100");
		m.set(2, bc.getPAN());
		m.set(3, "300000");
		m.set(4, "000000000000");
		m.set(6, "000000000000");
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		m.set(10, "78323000");
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(14, bc.getExpDate());
		m.set(15, dateFormatDate.format(date));
		m.set(16, dateFormatDate.format(date));
		m.set(18, "5499");
		m.set(22, "902");
		m.set(32, "012345");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(41, "12345678");
		m.set(42, "123456789123456");
		m.set(43, "FIRST THIRD BANK       ST. LOUIS     MO ");
		m.set(48, ISOUtil.hex2byte("52"));
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(51, currencyBill.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		byte[] encpin = Encripter.encriptTDES(
				Encripter.formTDESKey(Environment.ZMK), Encripter.formPINBlock(bc));
		//m.set(52, Arrays.copyOfRange(encpin, 0, 8));

		m.set(61, "00000000002008401111111111");
		m.set(63, "MCC011114");
		channel.send(m);
		
		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendAdvice(NACChannel channel, String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {

		BankCard bc = encountPOS.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0120");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
		m.set(6, String.format("%012d", Integer.parseInt(amountBill)));
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		m.set(10, "78323000");
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(12, dateFormatTime.format(date));
		m.set(13, dateFormatDate.format(date));
		m.set(15, dateFormatDate.format(date));
		m.set(16, dateFormatDate.format(date));
		m.set(18, "6011");
		m.set(22, "900");
		m.set(23, "000");
		m.set(32, "012345");
		m.set(35, bc.getTrack2());
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(38, "000000");
		m.set(39, "00");
		m.set(41, "12345678");
		m.set(42, "123456789123456");
		m.set(43, "Merchant Name          St Louis      MO");
		m.set(48, ISOUtil.hex2byte("5A"));
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(51, currencyBill.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(60, "1000000");
		m.set(61, "000000000120084090210");
		m.set(63, "MCC011116");
		m.set(121, "000001");
		channel.send(m);
		
		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}
	
	public String sendAdviceReversal(NACChannel channel, String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {

		BankCard bc = encountPOS.getRandomCard();
		//
		channel.connect();
		ISOMsg m = new ISOMsg();

		m.setMTI("0420");
		m.set(2, bc.getPAN());
		m.set(3, "010000");
		m.set(4, String.format("%012d", Integer.parseInt(amountTrxn)));
		m.set(6, String.format("%012d", Integer.parseInt(amountBill)));
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		m.set(7, dateFormatFull.format(date));
		m.set(10, "61000000");
		String STAN = Environment.getSTAN();
		m.set(11, STAN);
		m.set(15, dateFormatDate.format(date));
		m.set(16, dateFormatDate.format(date));
		m.set(22, "900");
		m.set(32, "012345");
		m.set(37, STAN + dateFormatTime.format(date));
		m.set(38, "005345");
		m.set(39, "00");
		m.set(43, "FIRST THIRD BANK       ST. LOUIS     MO");
		m.set(48, ISOUtil.hex2byte("5A3230303150"));
		m.set(49, currencyTrxn.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(51, currencyBill.replace("RUR", "643").replace("EUR", "978").replace("USD", "840"));
		m.set(60, "4000000");
		m.set(61, "0000000000000840");
		m.set(63, "MCC011118");
		m.set(90, "010000000104201623560000000968500000000000");
		channel.send(m);
		
		// Receive message
		ISOMsg resp = channel.receive();
		channel.disconnect();

		return resp.getString(39);
	}

}