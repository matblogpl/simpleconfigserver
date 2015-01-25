package pl.mateuszmierzwinski.vector.scheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * PayloadResponse - class for managing payloads response
 * 
 * @author Mateusz Mierzwinski
 */
public class PayloadResponse {
	
	private static final Logger LOG = LogManager.getLogger(PayloadResponse.class);
	
	public static final int PAYLOAD_OK = 0x01;
	public static final int PAYLOAD_ERROR = 0x02;
	
	
	/**
	 * Serialization of data - converts payload status into string to be pushed trought socket
	 */
	public static String serializer(int payloadStatus) {
		return Integer.toString(payloadStatus);
	}
	
	
	/**
	 * Status of payload converter - converts int to boolean
	 * @param payloadStatus
	 * @return
	 */
	public static boolean status(int payloadStatus) {
		
		switch (payloadStatus) {
			case PAYLOAD_OK:
				return true;
			default:
				return false;
		}
		
	}
	
	
	/**
	 * Deserialization of data - returns int from string.
	 */
	public static int deserializer(String serializedPayload) throws Exception {
		try {
			return Integer.valueOf(serializedPayload);
		} catch (Exception ex) {
			throw new Exception("Wrong payload frame. Data format mismatch!");
		}
	}
	
}
