package com.hikerian.autotest.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.WebError;


public class BrowserEventHandler {
	private final Logger log = LoggerFactory.getLogger(BrowserEventHandler.class);
	
	
	public BrowserEventHandler() {
	}
	
	public void init(BrowserContext context) {
		// register event handler
		context.onPage(this::onPage);
		context.onPageLoad(this::onPageLoad);
		context.onPageClose(this::onPageClose);
		
		context.onRequest(this::onRequest);
		context.onRequestFailed(this::onRequestFailed);
		context.onRequestFinished(this::onRequestFinished);
		context.onResponse(this::onResponse);
		
		context.onDownload(this::onDownload);
		context.onDialog(this::onDialog);
		context.onDialogClosed(this::onDialogClosed);
		
		context.onClose(this::onClose);
	}
	
	void onPage(Page page) {
		
	}
	
	void onPageLoad(Page page) {
		
	}
	
	void onPageClose(Page page) {
		
	}
	
	void onRequest(Request request) {
		
	}
	
	void onRequestFailed(Request request) {
		
	}
	
	void onRequestFinished(Request request) {
		
	}
	
	void onResponse(Response response) {
		
	}
	
	void onWebError(WebError error) {
		
	}
	
	void onFrameAttached(Frame frame) {
		
	}
	
	void onFrameDetached(Frame frame) {
		
	}
	
	void onFrameNavigated(Frame frame) {
		
	}
	
	void onDownload(Download download) {
		
	}
	
	void onDialog(Dialog dialog) {
		
	}
	
	void onDialogClosed(Dialog dialog) {
		
	}
	
	void onConsoleMessage(ConsoleMessage consoleMessage) {
		
	}

	void onClose(BrowserContext context) {
		
	}






}
