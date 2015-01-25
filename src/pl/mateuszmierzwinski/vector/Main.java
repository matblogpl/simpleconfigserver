package pl.mateuszmierzwinski.vector;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.mateuszmierzwinski.vector.adapters.TCPAdapter;
import pl.mateuszmierzwinski.vector.scheme.PayloadModel;


/**
 * Main initailzation class
 * 
 * @author Mateusz Mierzwinski
 *
 */
public class Main {

	private static final Logger LOG = LogManager.getLogger(Main.class);
	
	/**
	 * TCP Connector Protocol initializer
	 * 
	 * @param protocol
	 * 			Array of Paymodel.PAYLOAD_n'th elements as configuration protocol
	 */
	private static void initTCPAdapterSocket(int[][] protocol) {
		LOG.info("Starting Socket");
		try {
			TCPAdapter adapter = new TCPAdapter();
			adapter.setProtocol(protocol);
			adapter.initCommunicationThread();
		} catch (Exception e) {
			LOG.error("TCPAdapterInit", e);
		}
		
	}
	
	
	/**
	 * Main initializer
	 * @param args
	 */
	public static void main(String[] args) {
		
		LOG.info("Initializing application");
		
		
		int[][] protocol = new int[4][];

		protocol[0] = PayloadModel.PAYLOAD_1;
		protocol[1] = PayloadModel.PAYLOAD_2;
		protocol[2] = PayloadModel.PAYLOAD_3;
		protocol[3] = PayloadModel.PAYLOAD_4;
		
		initTCPAdapterSocket(protocol);
	}

}
