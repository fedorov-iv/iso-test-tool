package ru.somecompany.bankcards.loadscripts.config;

public class Port {
	
	private String portType;
	private String portWeigth;
	private String serverName;
	private String serverPort;
	private String startSymbol;
	private String headerLength;
	
	public Port(String input1, String input2, String input3, String input4, String input5, String input6)
	{
		portType = input1;
		portWeigth = input2;
		serverName = input3;
		serverPort = input4;
		startSymbol = input5;
		headerLength = input6;
	}
	
	public void setServerType(String input)
	{
		portType = input;
	}
	
	public void setPortWeigth(String input)
	{
		portWeigth = input;
	}
	
	public void setServerName(String input)
	{
		serverName = input;
	}
	
	public void setServerPort(String input)
	{
		serverPort = input;
	}
	
	public void setStartSymbol(String input)
	{
		startSymbol = input;
	}
	
	public void setHeaderLength(String input)
	{
		headerLength = input;
	}
	
	public String getPortType()
	{
		return portType;
	}
	
	public String getPortWeigth()
	{
		return portWeigth;
	}
	
	public String getServerName()
	{
		return serverName;
	}
	
	public String getServerPort()
	{
		return serverPort;
	}
	
	public String getStartSymbol()
	{
		return startSymbol;
	}
	
	public String getHeaderLength()
	{
		return headerLength;
	}

}
