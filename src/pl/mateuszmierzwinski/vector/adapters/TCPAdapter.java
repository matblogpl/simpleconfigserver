package pl.mateuszmierzwinski.vector.adapters;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.mateuszmierzwinski.vector.scheme.PayloadModel;

/**
 * @author Mateusz Mierzwiński
 *
 */
public class TCPAdapter {

	private static final Logger LOG = LogManager.getLogger(TCPAdapter.class);
	
	// maximum thread count
	private static final byte MAX_THREADS = 20;
	
	private ServerSocket socket;
	private byte serverConnectorUsed = 0;
	private int[][] protocol;
	
	
	/**
	 * Constructor with port specified for communication
	 * 
	 * @param port
	 * 		Port number
	 * 
	 * @throws IOException
	 */
	public TCPAdapter(int port) throws IOException {
		LOG.info("Binding port: "+Integer.toString(port));
		this.socket = new ServerSocket(port);
	}
	
	
	/**
	 * Default constructor for port 9999
	 * @throws IOException
	 */
	public TCPAdapter() throws IOException {
		this(9999);
	}
	
	
	public void initCommunicationThread() throws Exception {
		LOG.info("Running thread");
		
		// Sanity check
		if (socket == null || socket.isClosed()) {
			throw new Exception("Socket closed! Cannot init communication. Internal failure.");
		}
		
		
		while(true) {
			if (newThreadPoolAvailable()) {
				TCPAdapterConnectionRunnable runnable = new TCPAdapterConnectionRunnable();
				Socket cs = socket.accept();
				runnable.setClientSocket(cs);
				runnable.setProtocol(protocol);
				runnable.setTCPAdapter(this);
				new Thread(runnable).start();
			} else {
				try {
					Thread.sleep(250);
				} catch (Exception e) {
					throw new Exception("Main thread cannot be stopped! Cannot init communication. Internal failure.");
				}
			}
		}
		
	}
	
	
	/**
	 * Just a sanity check
	 */
	public void finalize() {
		try {
			if (!socket.isClosed()) { 
				socket.close();
			}
		} catch(Exception ex) {
			LOG.info("finalizedSanityCheckFailed", ex);
		}
	}

	/**
	 * Method for freeing pool of threads
	 */
	public void freeThread() {
		if (this.serverConnectorUsed > 0) {
			this.serverConnectorUsed--;
			LOG.info("Freeing thread in pool. Now pool is "+Integer.toString(MAX_THREADS-this.serverConnectorUsed)+" / "+Integer.toString(MAX_THREADS));
		}
	}
	
	/**
	 * Starting new thread should increase pool of threads count
	 */
	public void startThread() {
		if (this.serverConnectorUsed <= Byte.MAX_VALUE) {
			this.serverConnectorUsed++;
			LOG.info("Running new thread. Now pool is "+Integer.toString(MAX_THREADS-this.serverConnectorUsed)+" / "+Integer.toString(MAX_THREADS));
		}
	}
	
	
	/**
	 * Do we have free threads?
	 * 
	 * @return
	 */
	public boolean newThreadPoolAvailable() {
		if (this.serverConnectorUsed+1 <= MAX_THREADS) {
			return true;
		}
		return false;
	}
		
	public void setProtocol(int[][] protocol) {
		this.protocol = protocol;
	}
	
}
