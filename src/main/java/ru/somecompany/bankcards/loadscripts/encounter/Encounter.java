package ru.somecompany.bankcards.loadscripts.encounter;

import java.util.*;

import ru.somecompany.bankcards.loadscripts.config.Port;
import ru.somecompany.bankcards.loadscripts.loaddata.BankCard;
import ru.somecompany.bankcards.loadscripts.loaddata.BankTerminal;

public class Encounter {

	private HashMap<String, BankCard> cardmap;
	private HashMap<String, BankTerminal> terminalmap;
	private HashMap<String, Port> portsmap;
	private HashMap<String, BankCard> tmpcardmap = new HashMap<String, BankCard>();
	private HashMap<String, BankTerminal> tmpterminalmap = new HashMap<String, BankTerminal>();
	//private HashMap<String, Port> tmpportsmap = new HashMap<String, Port>();
	private String portType = "";

	Random rand = new Random();

	public Encounter(String input1, HashMap<String, Port> input2,
			HashMap<String, BankCard> input3,
			HashMap<String, BankTerminal> input4) {
		portType = input1;
		portsmap = input2;
		cardmap = input3;
		terminalmap = input4;
	}

	public BankCard getRandomCard() {
		if (portType.equals("VMT") || portType.equals("VPOSVMT")) {
			Iterator<Map.Entry<String, BankCard>> it = cardmap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, BankCard> pair = (Map.Entry<String, BankCard>) it
						.next();
				if (pair.getValue().getPAN().charAt(0) == '4' || pair.getValue().getPAN().charAt(0) == '5' || pair.getValue().getPAN().charAt(0) == '2')
					tmpcardmap.put(pair.getKey(), pair.getValue());
			}
		} else if (portType.equals("MCMS") || portType.equals("VPOSMCMS")) {
			Iterator<Map.Entry<String, BankCard>> it = cardmap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, BankCard> pair = (Map.Entry<String, BankCard>) it
						.next();
				if (pair.getValue().getPAN().charAt(0) == '5' || pair.getValue().getPAN().charAt(0) == '6' || pair.getValue().getPAN().charAt(0) == '2'
						|| pair.getValue().getPAN().charAt(0) == '4')
					tmpcardmap.put(pair.getKey(), pair.getValue());
			}
		} else {
			Iterator<Map.Entry<String, BankCard>> it = cardmap.entrySet()
					.iterator();
			tmpcardmap.putAll(cardmap);
		}
		String randomKey = null;
		if (!tmpcardmap.isEmpty()) {
			List<String> keysCards = new ArrayList<String>(tmpcardmap.keySet());
			randomKey = keysCards.get(rand.nextInt(keysCards.size()));
		} else {
			throw new RuntimeException("Cardmap runtime exception");
		}
		tmpcardmap.clear();
		return cardmap.get(randomKey);
	}

	public BankTerminal getRandomTerminal(String currency) {
		Iterator<Map.Entry<String, BankTerminal>> it = terminalmap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, BankTerminal> pair = (Map.Entry<String, BankTerminal>) it
					.next();
			if (pair.getValue().getTerminalType().equals(portType)
					&& pair.getValue().getCurrency().equals(currency))
				tmpterminalmap.put(pair.getKey(), pair.getValue());
		}

		String randomKey = null;
		//System.out.println("Terminal map size: " + terminalmap.size());
		if (!terminalmap.isEmpty()) {
			List<String> keysTerminals = new ArrayList<String>(
					terminalmap.keySet());
			randomKey = keysTerminals.get(rand.nextInt(keysTerminals.size()));
		} else {
			throw new RuntimeException("Find terminal runtime exception");
		}
		tmpterminalmap.clear();
		return terminalmap.get(randomKey);
	}
	/*
	public BankTerminal getRandomTerminal() {
		Iterator<Map.Entry<String, BankTerminal>> it = terminalmap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, BankTerminal> pair = (Map.Entry<String, BankTerminal>) it
					.next();
			if (!pair.getValue().getTerminalType().equals(portType))
				it.remove();
		}
		String randomKey = null;
		if (!terminalmap.isEmpty()) {
			List<String> keysTerminals = new ArrayList<String>(
					terminalmap.keySet());
			randomKey = keysTerminals.get(rand.nextInt(keysTerminals.size()));
		} else {
			throw new RuntimeException("�� ������ ���������� ��������.");
		}

		return terminalmap.get(randomKey);
	}
	*/

	public Port getRandomPort() {

		Iterator<Map.Entry<String, Port>> it = portsmap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Port> pair = (Map.Entry<String, Port>) it.next();
			if (!pair.getValue().getPortType().equals(portType))
				it.remove();
		}
		String randomKey = null;
		if (!portsmap.isEmpty()) {
			List<String> keysPorts = new ArrayList<String>(portsmap.keySet());
			randomKey = keysPorts.get(rand.nextInt(keysPorts.size()));
		} else {
			throw new RuntimeException(
					"Find port runtime exception");
		}
		return portsmap.get(randomKey);
	}

}
