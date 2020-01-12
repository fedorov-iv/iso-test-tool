package ru.somecompany.bankcards.loadscripts.loaddata;

public class BankTerminal {

	private String terminalType;
	private String merchantId;
	private String terminalId;
	private String currency;
	private String acqInsIdVisa;
	private String acqInsIdMasterCard;
	private String mcc;
	private String tpk;

	public BankTerminal(String input1, String input2, String input3,
			String input4, String input5, String input6, String input7, String input8) {
		terminalType = input1;
		merchantId = input2;
		terminalId = input3;
		currency = input4;
		acqInsIdVisa = input5;
		acqInsIdMasterCard = input6;
		mcc = input7;
		tpk = input8;
	}

	public void setTerminalType(String input) {
		terminalType = input;
	}

	public void setMerchantId(String input) {
		merchantId = input;
	}

	public void setTerminalId(String input) {
		terminalId = input;
	}

	public String getAcqInsId(char cardType) {
		if (cardType == 'V') {
			return acqInsIdVisa;
		} else if (cardType == 'M') {
			return acqInsIdMasterCard;
		} else {
			throw new RuntimeException("Error in Acquirer Institution.");
		}
	}

	public void setMCC(String input) {
		mcc = input;
	}

	public void setTPK(String input) {
		tpk = input;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public String getMCC() {
		return mcc;
	}

	public String getTPK() {
		return tpk;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
