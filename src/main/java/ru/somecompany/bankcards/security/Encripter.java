package ru.somecompany.bankcards.security;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.jpos.iso.ISOUtil;

import ru.somecompany.bankcards.loadscripts.loaddata.BankCard;

public class Encripter {
	
	private static byte[] cipherbyte;
	
	public static final byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}
	
	public static byte[] formTDESKey (String input) {
		byte[] clearKeyBytes = hexToBytes(input);
    	byte[] key = clearKeyBytes;
        switch (clearKeyBytes.length) {
            case 16:
                {
                    // make it 3 components to work with JCE
                	key = ISOUtil.concat(clearKeyBytes, 0, 16, clearKeyBytes, 0, 8);
                }
                break;
            case 24:
                {
                    key = clearKeyBytes;
                }
        }
        return  key;
    }

	public static byte[] encriptTDES(byte[] tpk, byte[] modifiedPINBlock) {
		try {

			byte[] key = tpk;
			SecretKeySpec keySpec = new SecretKeySpec(key, "TripleDES");
			Cipher nCipher = Cipher.getInstance("TripleDES");

			// Encrypt
			nCipher.init(Cipher.ENCRYPT_MODE, keySpec);
			cipherbyte = nCipher.doFinal(modifiedPINBlock);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cipherbyte;
	}
	
	public static byte[] formPINBlock(BankCard bc)
	{
		String accountNumberPart = bc.getPAN().substring(bc.getPAN().length() - 13,bc.getPAN().length()-1);
		String firstComponent = "0000" + accountNumberPart;

		String secondComponent = "04" + bc.getPIN() + "FFFFFFFFFF";

		
		return xor(hexToBytes(firstComponent),hexToBytes(secondComponent));
	}

	public static byte[] formPINBlock(String pan, String pin)
	{
		String accountNumberPart = pan.substring(pan.length() - 13,pan.length()-1);
		String firstComponent = "0000" + accountNumberPart;

		String secondComponent = "04" + pin + "FFFFFFFFFF";


		return xor(hexToBytes(firstComponent),hexToBytes(secondComponent));
	}
	
	private static byte[] xor(final byte[] input1, final byte[] input2) {
	    final byte[] output = new byte[input1.length];
	    if (input2.length == 0) {
	        throw new IllegalArgumentException("empty security key");
	    }
	    int spos = 0;
	    for (int pos = 0; pos < input1.length; ++pos) {
	        output[pos] = (byte) (input1[pos] ^ input2[spos]);
	        ++spos;
	        if (spos >= input2.length) {
	            spos = 0;
	        }
	    }
	    return output;
	}
	
	public static byte[] hexToBytes(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static String bytesToHex(byte[] bytes) {
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static String convertHexToString(String hex){
		  
		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
	 
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=0; i<hex.length()-1; i+=2 ){
	 
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
	 
		      temp.append(decimal);
		  }
	 
		  return sb.toString();
	}

}
