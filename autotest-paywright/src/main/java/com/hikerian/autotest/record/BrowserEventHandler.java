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
	
	private final TestScriptAggregator aggregator;
	
	
	public BrowserEventHandler(TestScriptAggregator aggregator) {
		this.aggregator = aggregator;
	}
	
	public void init(BrowserContext context) {
		this.log.info("init: {}", context);
		
		// register event handler
		this.aggregator.setBrowserContext(context);
		
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
	
	public void openEntrypoint(Page page) {
		this.onPage(page); // Connection 전에 만들어진 페이지이기 때문에 초기 opPage를 호출
		
		// TODO 이벤트가 두번씩 오는지 체크 필요
		page.onLoad(this::onPageLoad);
		page.onClose(this::onPageClose);
		
		page.onRequest(this::onRequest);
		page.onRequestFailed(this::onRequestFailed);
		page.onRequestFinished(this::onRequestFinished);
		page.onResponse(this::onResponse);
		
		page.onDownload(this::onDownload);
		page.onDialog(this::onDialog);
		page.onDialogClosed(this::onDialogClosed);
		
		
		page.navigate(this.aggregator.getEntrypoint());
	}
	
	void onPage(Page page) {
		this.log.info("onPage: {}", page.url());
		// TODO process and send to aggregator
		this.aggregator.pushPage(page);
	}
	
	void onPageLoad(Page page) {
		this.log.info("onPageLoad: {}", page.url());
		// TODO process and send to aggregator
	}
	
	void onPageClose(Page page) {
		this.log.info("onPageClose: {}", page);
		// TODO process and send to aggregator
	}
	
	void onRequest(Request request) {
		this.log.info("onRequest: {}", request.url());
		// TODO process and send to aggregator
	}
	
	void onRequestFailed(Request request) {
		this.log.debug("onRequestFailed: {}", request.url());
		// TODO process and send to aggregator
	}
	
	void onRequestFinished(Request request) {
		this.log.debug("onRequestFinished: {}", request.url());
		// TODO process and send to aggregator
	}
	
	void onResponse(Response response) {
		this.log.info("onResponse", response.url());
		// TODO process and send to aggregator
	}
	
	void onWebError(WebError error) {
		this.log.debug("onWebError: {}", error);
		// TODO process and send to aggregator
	}
	
	void onFrameAttached(Frame frame) {
		this.log.debug("onFrameAttached: {}", frame);
		// TODO process and send to aggregator
	}
	
	void onFrameDetached(Frame frame) {
		this.log.debug("onFrameDetached: {}", frame);
		// TODO process and send to aggregator
	}
	
	void onFrameNavigated(Frame frame) {
		this.log.debug("onFrameNavigated: {}", frame);
		// TODO process and send to aggregator
	}
	
	void onDownload(Download download) {
		this.log.debug("onDownload: {}", download);
		// TODO process and send to aggregator
	}
	
	void onDialog(Dialog dialog) {
		this.log.debug("onDialog: {}", dialog);
		// TODO process and send to aggregator
	}
	
	void onDialogClosed(Dialog dialog) {
		this.log.debug("onDialogClosed: {}", dialog);
		// TODO process and send to aggregator
	}
	
	void onConsoleMessage(ConsoleMessage consoleMessage) {
		this.log.debug("onConsoleMessage: {}", consoleMessage.text());
		// TODO process and send to aggregator
	}

	void onClose(BrowserContext context) {
		this.log.debug("onClose: {}", context);
		// TODO process and send to aggregator
	}






}
