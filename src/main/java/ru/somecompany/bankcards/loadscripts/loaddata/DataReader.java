package ru.somecompany.bankcards.loadscripts.loaddata;

import java.io.*;
import java.util.*;

public class DataReader {

	static private String DELIMITER = "|";
	static private HashMap<String, BankCard> cardmap = new HashMap<String, BankCard>();
	static private HashMap<String, BankTerminal> terminalmap = new HashMap<String, BankTerminal>();

	public static HashMap<String, BankCard> getBankCardMap(String scenarioName) {
		if (cardmap.isEmpty())
			readCardsData(scenarioName);
		return cardmap;
	}

	public static void readCardsData(String scenarioName) {

		InputStream fstream;
		Vector<String> data = new Vector<String>();
		int noOfRecords;
		StringTokenizer token;

		try {
			// Read the 'cards_cfg' file

			if (scenarioName == null) {
				fstream = DataReader.class.getClassLoader().getResourceAsStream("rawdata/cards_default_cfg");
			} else {
				fstream = DataReader.class.getClassLoader().getResourceAsStream("rawdata/cards/" + scenarioName);
			}
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Add the read line into the Vector
				try {
					data.add(strLine);
				} catch (Exception e1) {
					System.err
							.println("Error while adding record into Vector: "
									+ e1.getMessage());
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {
			System.err.println("Error in reading file: " + e.getMessage());
		}

		// Retrive the total number of records in the Vector
		noOfRecords = data.size();

		// Default values for pan and pin
		String pin = "0000";
		String cvv2 = "000";
		String track2 = "0000000000000000";
		for (int i = 0; i < noOfRecords; i++) {
			try {
				token = new StringTokenizer(data.get(i).toString(), DELIMITER);
				// Fill the map appropriately using the tokenized values

				pin = token.nextToken();
				cvv2 = token.nextToken();
				track2 = token.nextToken();

				cardmap.put("" + i, new BankCard(pin, cvv2, track2));
			} catch (Exception e3) {
				System.err.println("Error occured while tokenizing: "
						+ e3.getMessage());
			}
		}

	}

	public static HashMap<String, BankTerminal> getTerminalMap(String scenarioName) {
		if (terminalmap.isEmpty())
			readTerminalData(scenarioName);
		return terminalmap;
	}

	public static void readTerminalData(String scenarioName) {

		InputStream fstream;
		Vector<String> data = new Vector<String>();
		int noOfRecords;
		StringTokenizer token;

		try {
			// Read the 'terminal_cfg' file

			if (scenarioName == null) {
				fstream = DataReader.class.getClassLoader().getResourceAsStream("rawdata/terminal_default_cfg");
			} else {
				fstream = DataReader.class.getClassLoader().getResourceAsStream("rawdata/terminals/" + scenarioName);
			}
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Add the read line into the Vector
				try {
					data.add(strLine);
				} catch (Exception e1) {
					System.err
							.println("Error while adding record into Vector: "
									+ e1.getMessage());
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {
			System.err.println("Error in reading file: " + e.getMessage());
		}

		// Retrive the total number of records in the Vector
		noOfRecords = data.size();

		// Default values for pan and pin
		String terminalType = "ATM";
		String merchantId = "00000000";
		String terminalId = "00000000";
		String currency = "RUR";
		String acqInsId = "000000,000000";
		String mcc = "0000";
		String tpk = "00000000";
		for (int i = 0; i < noOfRecords; i++) {
			try {
				token = new StringTokenizer(data.get(i).toString(), DELIMITER);
				// Fill the map appropriately using the tokenized values

				terminalType = token.nextToken();
				merchantId = token.nextToken();
				terminalId = token.nextToken();
				currency = token.nextToken();
				acqInsId = token.nextToken();
				
				String[] acqInsIdPair = acqInsId.split(",");
				
				mcc = token.nextToken();
				tpk = token.nextToken();

				terminalmap.put("" + i, new BankTerminal(terminalType,
						merchantId, terminalId, currency, acqInsIdPair[0], acqInsIdPair[1], mcc, tpk));
			} catch (Exception e3) {
				System.err.println("Error occured while tokenizing: "
						+ e3.getMessage());
			}
		}

	}

}
