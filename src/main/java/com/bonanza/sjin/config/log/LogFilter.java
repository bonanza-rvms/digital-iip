package com.bonanza.sjin.config.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LogFilter extends Filter<ILoggingEvent> {

	@Override
	public FilterReply decide(ILoggingEvent event) {
		String message = event.getMessage();
		if (message.contains("ConsumerConfig") || message.contains("ProducerConfig")) {
			return FilterReply.DENY;
		} else {
			return FilterReply.ACCEPT;
		}
	}
}
