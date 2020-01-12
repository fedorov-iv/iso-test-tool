package ru.somecompany.bankcards.loadscripts.formats;

import java.io.IOException;
import java.io.InputStream;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.channel.VAPChannel;

import ru.somecompany.bankcards.loadscripts.packanger.CustomGenericPackager;

public class VISAServer implements ISORequestListener {
	
	public VISAServer(String scenarioName, String port) throws IOException, ISOException {
		VS = new VISASender(scenarioName);
		
		InputStream instr = VISAServer.class.getResourceAsStream("/Base1.xml");
		channel = new VAPChannel(new CustomGenericPackager(instr));
/*		
		channel = new VAPChannel(new CustomGenericPackager(
				"Base1.xml"));
*/		
		channel.setSrcId("808801");
		channel.setDstId("332812");
		ISOServer server = new ISOServer(Integer.parseInt(port), channel, null);
		server.addISORequestListener(this);
		new Thread(server).start();
	}

	VAPChannel channel;
	VISASender VS;
	boolean activeFlag = false;

	public boolean process(final ISOSource source, ISOMsg in) {

		try {
			if (in.getMTI().equals("0800") && in.getString(70).equals("071")) {
				activeFlag = true;
				
				in.setMTI("0810");
				
				in.set(39, "00");
				source.send(in);
				
			} else if (in.getMTI().equals("0800")
					&& in.getString(70).equals("072")) {
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
	
	public String sendCashAdvance(String currencyTrxn, String amountTrxn) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = VS.sendCashAdvance(channel, currencyTrxn, amountTrxn);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendCashAdvanceReversal(String currencyTrxn, String amountTrxn) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = VS.sendCashAdvanceReversal(channel, currencyTrxn, amountTrxn);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendBalanceInquiry(String currencyTrxn) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = VS.sendBalanceInquiry(channel, currencyTrxn);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendPurchase(String currencyTrxn, String amountTrxn) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = VS.sendPurchase(channel, currencyTrxn, amountTrxn);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendPurchaseReversal(String currencyTrxn, String amountTrxn) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = VS.sendPurchaseReversal(channel, currencyTrxn, amountTrxn);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendAdvice(String currencyTrxn, String amountTrxn) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = VS.sendAdvice(channel, currencyTrxn, amountTrxn);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}
	
	public String sendAdviceReversal(String currencyTrxn, String amountTrxn) throws IOException, ISOException {
		String responceCode = "";
		
		if (activeFlag == true) {
			responceCode = VS.sendAdviceReversal(channel, currencyTrxn, amountTrxn);
		} else {
			throw new RuntimeException("Some error happened: activeFlag=false.");
		}
		
		return responceCode;
	}

}
