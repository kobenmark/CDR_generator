package com.bgandrew.cdr_generator.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.bgandrew.cdr_generator.Generator;
import com.google.common.base.Joiner;

/** Utility class to hold CDR representation
 *
 */

public class CDR {

	CDR (Customer caller, Customer recipient, LocalDateTime timestamp,
			Customer.CallType type, int duration, int length) {
		this.caller = caller;
		this.recipient = recipient;
		this.timestamp = timestamp;
		this.type = type;
		this.duration = duration;
		this.length = length;
	}    

	private final Customer caller;
	private final Customer recipient;

	private final LocalDateTime timestamp;

	private final Customer.CallType type;

	private final int duration; // seconds, for 
	private final int length; // symbols, for SMS

	/*
	private final static String[] CSV_HEADER = {
		"runtimeId",
		"timestamp",
		"caller MSISDN", "caller IMEI", "caller IMSI",
		"caller cell ID",
		"recipient MSISDN", "recipient IMEI", "recipient IMSI",
		"recipient cell ID",
		"type", "duration", "length" 
	};
	 */

	public String toString(HashMap<Location, Integer> location_to_cellId) {

		StringBuilder sb = new StringBuilder();

		sb.append(
				Generator.runtimeId).append(Constants.DELIMITER).

				append(formatTimestamp(timestamp)).append(Constants.DELIMITER).
				append(caller.getMSISDN()).append(Constants.DELIMITER).
				append(caller.getIMEI()).append(Constants.DELIMITER).
				append(caller.getIMSI()).append(Constants.DELIMITER).
				append(location_to_cellId.get(caller.getLocation())).append(Constants.DELIMITER).

				append(recipient.getMSISDN()).append(Constants.DELIMITER).
				append(recipient.getIMEI()).append(Constants.DELIMITER).
				append(recipient.getIMSI()).append(Constants.DELIMITER).
				append(location_to_cellId.get(recipient.getLocation())).append(Constants.DELIMITER).

				append(type).append(Constants.DELIMITER).
				append(duration).append(Constants.DELIMITER).
				append(length);        

		return sb.toString();
	}

	private static String formatTimestamp(LocalDateTime timestamp) {
		return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}


}
