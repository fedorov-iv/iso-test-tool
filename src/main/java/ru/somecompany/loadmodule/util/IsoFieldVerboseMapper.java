package ru.somecompany.loadmodule.util;

import java.util.HashMap;
import java.util.Map;

public class IsoFieldVerboseMapper {

    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("MTI", "MTI");
        map.put("1", "Additional Bit Map");
        map.put("2", "PAN");
        map.put("3", "Processing code");
        map.put("4", "Amount, transaction");
        map.put("5", "Amount, Settlement");
        map.put("6", "Amount, cardholder billing");
        map.put("7", "Transmission date & time");
        map.put("8", "Amount, Cardholder billing fee");
        map.put("9", "Conversion rate, Settlement");
        map.put("10", "Conversion rate, cardholder billing");
        map.put("11", "Systems trace audit number");
        map.put("12", "Time, Local transaction");
        map.put("13", "Date, Local transaction");
        map.put("14", "Date, Expiration");
        map.put("15", "Date, Settlement");
        map.put("16", "Date, conversion");
        map.put("17", "Date, capture");
        map.put("18", "MCC");
        map.put("19", "Currency");
        map.put("20", "PAN Extended, country code");
        map.put("21", "Forwarding institution, country code");
        map.put("22", "Point of service entry mode");
        map.put("23", "Application PAN number");
        map.put("24", "Function code(ISO 8583:1993)/Network International identifier (NII)");
        map.put("25", "Point of service condition code");
        map.put("26", "Point of service capture code");
        map.put("27", "Authorizing identification response length");
        map.put("28", "Amount, transaction fee");
        map.put("29", "Amount, settlement fee");
        map.put("30", "Amount, transaction processing fee");
        map.put("31", "Amount, settlement processing fee");
        map.put("32", "Acquiring institution identification code");
        map.put("33", "Forwarding institution identification code");
        map.put("34", "Primary account number, extended");
        map.put("35", "Track 2 Data");
        map.put("36", "Track 3 Data");
        map.put("37", "Retrieval reference number");
        map.put("38", "Authorization identification response");
        map.put("39", "Response code");
        map.put("40", "Service restriction code");
        map.put("41", "Terminal ID");
        map.put("42", "Merchant ID");
        map.put("43", "Card acceptor name/location");
        map.put("44", "Additional response data");
        map.put("45", "Track 1 Data");
        map.put("46", "Additional Data - ISO");
        map.put("47", "Additional Data - National");
        map.put("48", "Additional Data - Private");
        map.put("49", "Currency code, transaction");
        map.put("50", "Currency code, settlement");
        map.put("51", "Currency code, cardholder billing");
        map.put("52", "Personal Identification number data");
        map.put("53", "Security related control information");
        map.put("54", "Additional amounts");
        map.put("55", "Reserved ISO");
        map.put("56", "Reserved ISO");
        map.put("57", "Reserved National");
        map.put("58", "Reserved National");
        map.put("59", "Reserved National");
        map.put("60", "Advice/reason code (private reserved)");
        map.put("61", "Reserved Private");
        map.put("62", "Reserved Private");
        map.put("63", "Reserved Private");
        map.put("64", "Message authentication code (MAC)");
        map.put("65", "Bit map, tertiary");
        map.put("66", "Settlement code");
        map.put("67", "Extended payment code");
        map.put("68", "Receiving institution country code");
        map.put("69", "Settlement institution country code");
        map.put("70", "Network management Information code");
        map.put("71", "Message number");
        map.put("72", "Data record (ISO 8583:1993)/n 4 Message number, last(?)");
        map.put("73", "Date, Action");
        map.put("74", "Credits, number");
        map.put("75", "Credits, reversal number");
        map.put("76", "Debits, number");
        map.put("77", "Debits, reversal number");
        map.put("78", "Transfer number");
        map.put("79", "Transfer, reversal number");
        map.put("80", "Inquiries number");
        map.put("81", "Authorizations, number");
        map.put("82", "Credits, processing fee amount");
        map.put("83", "Credits, transaction fee amount");
        map.put("84", "Debits, processing fee amount");
        map.put("85", "Debits, transaction fee amount");
        map.put("86", "Credits, amount");
        map.put("87", "Credits, reversal amount");
        map.put("88", "Debits, amount");
        map.put("89", "Debits, reversal amount");
        map.put("90", "Original data elements");
        map.put("91", "File update code");
        map.put("92", "File security code");
        map.put("93", "Response indicator");
        map.put("94", "Service indicator");
        map.put("95", "Replacement amounts");
        map.put("96", "Message security code");
        map.put("97", "Amount, net settlement");
        map.put("98", "Payee");
        map.put("99", "Settlement institution identification code");
        map.put("100", "Receiving institution identification code");
        map.put("101", "File name");
        map.put("102", "Account identification 1");
        map.put("103", "Account identification 2");
        map.put("104", "Transaction description");

    }
    public static String getVerboseName(String key){

        return map.get(key);

    }
}
