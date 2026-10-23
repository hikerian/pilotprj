package com.hikerian.autotest.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;


public class RecordingWebSocketHandler {
	private final Logger log = LoggerFactory.getLogger(RecordingWebSocketHandler.class);
	
	private final TestScriptAggregator aggregator;
	
	
	public RecordingWebSocketHandler(TestScriptAggregator aggregator) {
		this.aggregator = aggregator;
	}
	
	public void handleEvent(final String method, JsonNode params) {
		/* this.Events = {
		 *   StateChanged: "stateChanged",
		 *   InspectRequested: "inspectRequested",
		 *   SourceChanged: "sourceChanged",
		 *   Paused: "paused",
		 *   SetModeRequested: "setModeRequested"
		 * };
		 */
		switch(method) {
		case "stateChanged" -> {
			// TODO process and send to aggregator
			this.log.info("Method:{} Params:{}", method, params);
			
			this.aggregator.onStateChanged(params);
		}
		case "inspectRequested" -> {
			// TODO process and send to aggregator
			this.log.info("Method:{} Params:{}", method, params);
			
			this.aggregator.onInspectRequested(params);
		}
		case "sourceChanged" -> {
			// TODO process and send to aggregator
			this.log.info("Method:{} Params:{}", method, params);
			
			this.aggregator.onSourceChanged(params);
		}
		case "paused" -> {
			// TODO process and send to aggregator
			this.log.info("Method:{} Params:{}", method, params);
			
			this.aggregator.onPaused(params);
		}
		case "setModeRequested" -> {
			// TODO process and send to aggregator
			this.log.info("Method:{} Params:{}", method, params);
			
			this.aggregator.onSetModeRequested(params);
		}
		default -> {
			this.log.warn("Unexpected Event -Method:{} -Params:{}", method, params);
		}
		}
	}


}
