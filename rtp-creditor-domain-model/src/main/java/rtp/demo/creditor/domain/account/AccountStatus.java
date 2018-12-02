package rtp.demo.creditor.domain.account;

public enum AccountStatus {

	OPEN("OPEN"), CLOSED("CLOSED"), BLOCKED("BLOCKED");

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
