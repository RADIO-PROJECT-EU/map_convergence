package robot.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.validator.routines.InetAddressValidator;

public class NetworkUtils {
	
	private static final int IP_RANGE_START = 2;
	private static final int IP_RANGE_END = 254;
	public static final int GATEWAY_PORT = 1883;
	private static final int DEFAULT_PING_TIMEOUT = 1000;
	private static final String LOCALHOST_IP = "127.0.0.1";
	
	
	/**
	 * Checking connection to host can be established.
	 * @param host - The host to connect
	 * @param port - The port to connection
	 * @param timeout - Timeout in Milliseconds
	 * @return true | false - Response received.
	 */
	public static boolean ping(String host, int port, int timeout) {
	    try (Socket socket = new Socket()) {
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false;
	    }
	}
	
	public static String findGateway(String network, int netmask) throws Exception{
		
		//Firstly check for localhost
		if( ping(LOCALHOST_IP, GATEWAY_PORT, DEFAULT_PING_TIMEOUT) ) return LOCALHOST_IP;
		
		if( netmask < 8 ) throw new Exception("Network mask cannot be less than 8");
		if( netmask > 32 ) throw new Exception("Network mask cannot be more than 32");
		if( netmask != 24 ) throw new Exception("In this version only 24 mask is supported");
		boolean isValid = false;
		isValid = InetAddressValidator.getInstance().isValid(network);
		if( !isValid ) throw new Exception("Invalid network, XXX.XXX.XXX.XXX");
		
		String temp[] = network.split("\\.");
		String tmpNet = temp[0]+"."+temp[1]+"."+temp[2];
		
		String checkIp = "";
		String gatewayIp = null;
		int checkCounter = 10;
		for( int i=IP_RANGE_START; i<IP_RANGE_END; i++ ){
			checkIp = tmpNet + "."+i;
			if( ping(checkIp, GATEWAY_PORT, 500) ){
				gatewayIp = checkIp;
				break;
			}
			checkCounter--;
			if(checkCounter<0){
				System.out.println("[NetoworUtils] - Last IP checked for gateway was: " + checkIp);
				checkCounter = 10;
			}
		}
		
		return gatewayIp;
	}

}
