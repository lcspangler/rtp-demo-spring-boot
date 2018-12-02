package rtp.demo.creditor.domain.rtp.simplified;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CreditTransferMessageDeserializer implements Deserializer {

	@Override
	public void close() {
	}

	@Override
	public void configure(Map arg0, boolean arg1) {
	}

	@Override
	public Object deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		CreditTransferMessage user = null;
		try {
			user = mapper.readValue(arg1, CreditTransferMessage.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return user;
	}

}
