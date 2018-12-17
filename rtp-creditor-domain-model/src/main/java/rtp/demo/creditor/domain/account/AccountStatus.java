package rtp.demo.creditor.domain.account;

import org.infinispan.protostream.annotations.ProtoEnum;
import org.infinispan.protostream.annotations.ProtoEnumValue;

@ProtoEnum(name = "AccountStatus")
public enum AccountStatus {

	@ProtoEnumValue(number = 1, name = "OPEN")
	OPEN("OPEN"), @ProtoEnumValue(number = 2, name = "CLOSED")
	CLOSED("CLOSED"), @ProtoEnumValue(number = 3, name = "BLOCKED")
	BLOCKED("BLOCKED");

	private final String status;

	private AccountStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public static AccountStatus fromString(String code) {
		for (AccountStatus status : AccountStatus.values()) {
			if (status.status.equalsIgnoreCase(code)) {
				return status;
			}
		}
		return null;
	}

}
