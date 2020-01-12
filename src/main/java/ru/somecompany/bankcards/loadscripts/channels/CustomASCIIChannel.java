package ru.somecompany.bankcards.loadscripts.channels;

import java.io.IOException;
import java.net.ServerSocket;

import org.jpos.iso.*;

import ru.somecompany.bankcards.security.Encripter;

public class CustomASCIIChannel extends BaseChannel {
	
	int headerLength;
	String startSymbol;
	
	    /**
	     * Public constructor (used by Class.forName("...").newInstance())
	     */
	    public CustomASCIIChannel () {
	        super();
	    }
	    /**
	     * Construct client ISOChannel
	     * @param host  server TCP Address
	     * @param port  server port number
	     * @param p     an ISOPackager
	     * @see ISOPackager
	     */
	    public CustomASCIIChannel (String host, int port, ISOPackager p, int headerLengthIN, String startSymbolIN) {
	        super(host, port, p);
	    	headerLength = headerLengthIN;
	    	startSymbol = startSymbolIN;
	    }
	    /**
	     * Construct server ISOChannel
	     * @param p     an ISOPackager
	     * @exception IOException
	     * @see ISOPackager
	     */
	    public CustomASCIIChannel (ISOPackager p, int headerLengthIN, String startSymbolIN) throws IOException {
	        super(p);
	        headerLength = headerLengthIN;
	    	startSymbol = startSymbolIN;
	    }
	    /**
	     * constructs a server ISOChannel associated with a Server Socket
	     * @param p     an ISOPackager
	     * @param serverSocket where to accept a connection
	     * @exception IOException
	     * @see ISOPackager
	     */
	    public CustomASCIIChannel (ISOPackager p, ServerSocket serverSocket) 
	        throws IOException
	    {
	        super(p, serverSocket);
	    }
	    /**
	     * @param len the packed Message len
	     * @exception IOException
	     */
	    protected void sendMessageLength(int len) throws IOException {
	        if (len > 9999)
	            throw new IOException ("len exceeded");
	        serverOut.write(Encripter.hexToBytes(startSymbol));
	        try {
	            serverOut.write(
	                ISOUtil.zeropad(Integer.toString(len), headerLength).getBytes()
	            );
	        } catch (ISOException e) { }
	    }
	    /**
	     * @return the Message len
	     * @exception IOException, ISOException
	     */
	    protected int getMessageLength() throws IOException, ISOException {
	        int l = 0;
	        byte[] b = new byte[headerLength];
	        while (l == 0) {
	        	serverIn.read();
	        	serverIn.readFully(b,0,headerLength);
	            try {
	                if ((l=Integer.parseInt(new String(b))) == 0) {
	                    serverOut.write(b);
	                    serverOut.flush();
	                }
	            } catch (NumberFormatException e) { 
	                throw new ISOException ("Invalid message length "+new String(b));
	            }
	        }
	        return l;
	    }
	}

