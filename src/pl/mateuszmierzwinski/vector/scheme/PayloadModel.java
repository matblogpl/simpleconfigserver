package pl.mateuszmierzwinski.vector.scheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * PayloadModel - class for managing payloads
 * 
 * @author Mateusz Mierzwinski
 */
public class PayloadModel {
	
	private static final Logger LOG = LogManager.getLogger(PayloadModel.class);
	
	public static final int[] PAYLOAD_1 = new int[]{ 0x01 };
	public static final int[] PAYLOAD_2 = new int[]{ 0x02, 0x02 };
	public static final int[] PAYLOAD_3 = new int[]{ 0x03, 0x03, 0x03 };
	public static final int[] PAYLOAD_4 = new int[]{ 0x04, 0x04, 0x04, 0x04 };
	
	private static final String VALUES_SEPARATOR = ",";
	
	
	
	/**
	 * Serialization of data - converts payload into string to be pushed trought socket
	 * 
	 * @param payload
	 * 			Array from static fields: PayloadModel.PAYLOAD_1 ... PayloadModel.PAYLOAD_4
	 * 
	 * @return
	 * 			Serialized string ready to be pushed trought socket
	 */
	public static String serializer(int[] payload) throws Exception {
		
		if (payload == null) {
			throw new Exception("Wrong serialized data! Payload is null");
		}
		if (payload.length < 1 || payload.length > 4) {
			throw new Exception("Wrong serialized data! Cannot convert payload!");
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i<payload.length; i++) {
			sb.append(Integer.toString(payload[i])+VALUES_SEPARATOR);
		}
		
		return sb.toString();
	}
	
	
	
	/**
	 * Payload comparator, that allows us to detect payloads
	 * 
	 * @param payload
	 * 			Payload.PAYLOAD_n - staticaly defined payload frame to compare serialized string with it.
	 * 
	 * @param serializedPayload
	 * 			Serialized Payload to be compared with payload data
	 * 
	 * @return
	 * 			True if it's compared payload
	 */
	public static boolean payloadCompare(int[] payload, String serializedPayload) {
		
		int[] payloadDeserialized = new int[]{};
		
		try {
			payloadDeserialized = deserializer(serializedPayload);
			
			if (payload.length != payloadDeserialized.length) return false;
			
			for (int i=0; i<payload.length; i++) {
				if (payload[i] != payloadDeserialized[i]) return false;
			}
			
		} catch (Exception ex) {
			LOG.error("payloadCompare", ex);
			return false;
		}
		
		return true;
	}
	
	
	
	/**
	 * Deserialization of data - returns int array from string.
	 * 
	 * @param serializedPayload
	 * 			String of serialized data
	 * 
	 * @return
	 * 			Array of integers from payload
	 * 
	 * @throws Exception
	 * 			Exeption about problem with data deserialization
	 */
	public static int[] deserializer(String serializedPayload) throws Exception {
		
		String[] values = serializedPayload.split(VALUES_SEPARATOR);
		
		if (values == null || values.length < 1 || values.length > 4) {
			throw new Exception("Wrong payload frame. Cannot deserialize data!");
		}
		
		int[] outputValues = new int[values.length];
		
		try {
			for (int i=0; i<values.length; i++) {
				outputValues[i] = Integer.parseInt(values[i]);
			}
		} catch (NumberFormatException ex) {
			throw new Exception("Wrong payload frame. Data format mismatch!");
		}
		
		return outputValues;
	}
	
}
