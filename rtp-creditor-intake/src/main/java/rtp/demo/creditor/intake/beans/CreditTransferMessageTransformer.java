package rtp.demo.creditor.intake.beans;

import iso.std.iso._20022.tech.xsd.pacs_008_001.FIToFICustomerCreditTransferV06;
import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;

public class CreditTransferMessageTransformer {

	public CreditTransferMessage toCreditTransferMessage(FIToFICustomerCreditTransferV06 rtpCreditTransferMessage) {
		CreditTransferMessage creditTransferMessage = new CreditTransferMessage();

		return creditTransferMessage;
	}
}
