package pl.mateuszmierzwinski.vector.adapters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.mateuszmierzwinski.vector.scheme.PayloadModel;
import pl.mateuszmierzwinski.vector.scheme.PayloadResponse;

/**
 * Client protocol & communication thread 
 * 
 * @author Mateusz Mierzwiński
 *
 */
class TCPAdapterConnectionRunnable implements Runnable {
	
	private static final Logger LOG = LogManager.getLogger(TCPAdapterConnectionRunnable.class);
	
	private Socket clientSocket;
	private int[][] protocol;
	private TCPAdapter adapter;
	
	/**
	 * Setting client socket for receiving of client connection
	 * @param clientSocket
	 */
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
		LOG.info("New connection from: "+clientSocket.getInetAddress().getHostAddress());
	}
	
	
	/**
	 * Setting of dynamic communication protocol
	 * 
	 * @param protocol
	 * 		protocol as array elements of PayloadModel.PAYLOAD_n'th elements
	 */
	public void setProtocol(int[][] protocol) {
		if (null!= protocol) {
			LOG.info("New protocol set with depth of messages: "+Integer.toString(protocol.length));
		}
		this.protocol = protocol;
	}
	
	
	/**
	 * TCPAdapter instance for threads synchronization and pool watchdog similar mechanisms 
	 * 
	 * @param adapterInstance
	 */
	public void setTCPAdapter(TCPAdapter adapterInstance) {
		this.adapter = adapterInstance;
	}
	
	
	/**
	 * Runnable thread as accept
	 */
	@Override
	public void run() {
		// lock thread for starting new ones in pool
		this.adapter.startThread();
		
		if (sanityChecks()) {
			
			// opening buffered reader & writer
			try {
				PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				for (int i=0; i<protocol.length; i++) {
					
					out.println(PayloadModel.serializer(protocol[i]));
					String input = in.readLine().trim();
					if (false == PayloadResponse.status(PayloadResponse.deserializer(input))) {
						LOG.error("Wrong response! Status is BAD/FALSE");
						break;
					} else {
						if (i == protocol.length-1) {
							LOG.info("Communication finished!");
						}
					}
				}

				// communication ends here
				clientSocket.close();
				
			} catch (Exception e) {
				LOG.error("ThreadException", e);
			}
		} 
		
		// allowing new thread to start, because this is ending his job
		this.adapter.freeThread();
	}


	/**
	 * Sanity checks for starting thread
	 * @return
	 */
	private boolean sanityChecks() {
		if (null!=this.protocol && this.protocol.length < 1) {
			LOG.error("There is no protocol for this communication!");
			return false;
		}
		if (this.clientSocket == null || this.clientSocket.isClosed() || !this.clientSocket.isConnected()) {
			LOG.error("There is no opened client socket!");
			return false;
		}

		return true;
	}
	
	
	
}
