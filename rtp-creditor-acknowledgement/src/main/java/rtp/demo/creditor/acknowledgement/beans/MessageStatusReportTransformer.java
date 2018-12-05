package rtp.demo.creditor.acknowledgement.beans;

import iso.std.iso._20022.tech.xsd.pacs_002_001.FIToFIPaymentStatusReportV07;
import rtp.demo.creditor.domain.error.PaymentValidationError;
import rtp.demo.creditor.domain.payments.Payment;

public class MessageStatusReportTransformer {

	public FIToFIPaymentStatusReportV07 toMessageStatusReport(Payment payment) {
		FIToFIPaymentStatusReportV07 messageStatusReport = new FIToFIPaymentStatusReportV07();
		PaymentValidationError validationError;

		if (payment.getErrors().size() > 0) {
			payment.getErrors().get(0);
		}

		return messageStatusReport;
	}

}
