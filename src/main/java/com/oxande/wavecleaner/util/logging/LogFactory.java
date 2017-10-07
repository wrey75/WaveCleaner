package com.oxande.wavecleaner.util.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogFactory {
	public static final Logger getLog(final Class<?> clazz) {
		Logger log = LogManager.getLogger(clazz);
		return log;
	}
}
