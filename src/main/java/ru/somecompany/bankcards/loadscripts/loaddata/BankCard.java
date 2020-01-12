package ru.somecompany.bankcards.loadscripts.loaddata;

public class BankCard {

	private String pin;
	private String cvv2;
	private String track2;
	private String tr2AcctNum;
	private String tr2ExpDate;
	private char cardType;

	// private String tr2OptData ;

	public BankCard(String input1, String input2, String input3) {
		pin = input1;
		cvv2 = input2;
		track2 = input3;

		// parse track2 data
		if (track2.indexOf("D") > 0) {
			tr2AcctNum = track2.substring(0, track2.indexOf("D"));
			tr2ExpDate = track2.substring((track2.indexOf("D") + 1),
					track2.indexOf("D") + 5);
			// tr2OptData = track2.substring(track2.indexOf("D") + 5,
			// track2.length() - 2);
		} else if (track2.indexOf("=") > 0) {
			tr2AcctNum = track2.substring(0, track2.indexOf("="));
			tr2ExpDate = track2.substring((track2.indexOf("=") + 1),
					track2.indexOf("=") + 5);
			// tr2OptData = track2.substring(track2.indexOf("=") + 5,
			// track2.length() - 2);
		}

		if (track2.charAt(0) == '4') {
			cardType = 'V';
		} else if (track2.charAt(0) == '5' || track2.charAt(0) == '6' || track2.charAt(0) == '2') {
			cardType = 'M';
		} else {
			throw new RuntimeException("Error in Track2");
		}
	}

	public void setPIN(String input) {
		pin = input;
	}

	public void setCVV2(String input) {
		cvv2 = input;
	}

	public void setTrack2(String input) {
		track2 = input;

		// parse track2 data
		if (track2.indexOf("D") > 0) {
			tr2AcctNum = track2.substring(0, track2.indexOf("D"));
			tr2ExpDate = track2.substring((track2.indexOf("D") + 1),
					track2.indexOf("D") + 5);
			// tr2OptData = track2.substring(track2.indexOf("D") + 5,
			// track2.length() - 2);
		} else if (track2.indexOf("=") > 0) {
			tr2AcctNum = track2.substring(0, track2.indexOf("="));
			tr2ExpDate = track2.substring((track2.indexOf("=") + 1),
					track2.indexOf("=") + 5);
			// tr2OptData = track2.substring(track2.indexOf("=") + 5,
			// track2.length() - 2);
		}
	}

	public String getPIN() {
		return pin;
	}

	public String getCVV2() {
		return cvv2;
	}

	public String getTrack2() {
		return track2;
	}

	public String getPAN() {
		return tr2AcctNum;
	}

	public String getExpDate() {
		return tr2ExpDate;
	}
	
	public char getCardType() {
		return cardType;
	}

}
