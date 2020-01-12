package ru.somecompany.bankcards.loadscripts.formats;

import java.io.IOException;
import java.io.InputStream;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.channel.NACChannel;
import org.jpos.iso.packager.GenericPackager;

import ru.somecompany.bankcards.security.Encripter;

public class MasterCardServer implements ISORequestListener {

	public MasterCardServer(String scenarioName, String port)
			throws IOException, ISOException {
		MCS = new MasterCardSender(scenarioName);
		
		InputStream instr = MasterCardServer.class.getResourceAsStream("/MasterCard.xml");
		channel = new NACChannel(new GenericPackager(instr),
				Encripter.hexToBytes(""));
		
		/*channel = new NACChannel(new GenericPackager("MasterCard.xml"),
				Encripter.hexToBytes(""));*/
		
		
		ISOServer server = new ISOServer(Integer.parseInt(port), channel, null);
		server.addISORequestListener(this);
		new Thread(server).start();

	}

	NACChannel channel;
	MasterCardSender MCS;
	boolean activeFlag = false;
	//ISOSource source;

	public boolean process(final ISOSource source, ISOMsg in) {

		try {
			if (in.getMTI().equals("0800") && in.getString(70).equals("081")) {
				activeFlag = true;
				
				in.setMTI("0810");
				
				in.set(39, "00");
				source.send(in);
				
			} else if (in.getMTI().equals("0800")
					&& in.getString(70).equals("082")) {
				activeFlag = false;
				
				in.setMTI("0810");
				
				in.set(39, "00");
				source.send(in);
			}
		} catch (ISOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public String sendCashAdvance(String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {
		String responceCode;
		
		if (activeFlag == true) {
			responceCode = MCS.sendCashAdvance(channel, currencyTrxn, amountTrxn, currencyBill, amountBill);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendCashAdvanceReversal(String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = MCS.sendCashAdvanceReversal(channel, currencyTrxn, amountTrxn, currencyBill, amountBill);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendBalanceInquiry(String currencyTrxn, String currencyBill) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = MCS.sendBalanceInquiry(channel, currencyTrxn, currencyBill);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendPurchase(String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = MCS.sendPurchase(channel, currencyTrxn, amountTrxn, currencyBill, amountBill);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendPurchaseReversal(String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = MCS.sendPurchaseReversal(channel, currencyTrxn, amountTrxn, currencyBill, amountBill);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendAdvice(String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = MCS.sendAdvice(channel, currencyTrxn, amountTrxn, currencyBill, amountBill);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendAdviceReversal(String currencyTrxn, String amountTrxn, String currencyBill, String amountBill) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = MCS.sendAdviceReversal(channel, currencyTrxn, amountTrxn, currencyBill, amountBill);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}

}
