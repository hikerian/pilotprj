package com.hikerian.autotest.record;

import java.util.List;

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
		this.log.debug("init: {}", context);
		
    	context.onPage(page -> {
    		System.out.println("###### Init New Page: " + page.url());
    	});
    	context.onPageLoad(page -> {
    		System.out.println("###### Init Page Load: " + page.url());
    	});
    	context.onPageClose(page -> {
    		System.out.println("###### Init Page Closed: " + page.url());
    	});
    	context.onClose(ctx -> {
    		System.out.println("###### Init Context Closed: " + ctx);
    	});
    	context.onDialog(dialog -> {
    		System.out.println("###### Init Dialog Load: " + dialog.message());
    		dialog.accept();
    	});
    	context.onRequest(req -> {
    		System.out.println("###### Init Request: " + req.url() + ":" + req + ":" + req.frame().page());
    	});
    	context.onResponse(res -> {
    		System.out.println("###### Init Response: " + res.url() + ":" + res + ":" + res.frame().page());
    		
    	});
		
//		// register event handler
//		context.onPage(this::onPage);
//		context.onPageLoad(this::onPageLoad);
//		context.onPageClose(this::onPageClose);
//		
////		context.onRequest(this::onRequest);
//		context.onRequest(req -> {
//			System.out.println("************** onRequest ******************");
//			this.onRequest(req);
//		});
//		context.onRequestFailed(this::onRequestFailed);
//		context.onRequestFinished(this::onRequestFinished);
//		context.onResponse(this::onResponse);
//		
//		context.onDownload(this::onDownload);
//		context.onDialog(this::onDialog);
//		context.onDialogClosed(this::onDialogClosed);
//		
//		context.onClose(this::onClose);
//		
//		// 최초에 열린 페이지에 대해 이벤트 강제 발생
//		List<Page> pageList = context.pages();
//		for(Page page : pageList) {
//			this.onPage(page);
//			
//			page.onLoad(this::onPageLoad);
//			page.onClose(this::onPageClose);
//			page.onRequest(this::onRequest);
//			page.onRequestFailed(this::onRequestFailed);
//			page.onRequestFinished(this::onRequestFinished);
//			page.onResponse(this::onResponse);
//			
//			page.onDownload(this::onDownload);
//			page.onDialog(this::onDialog);
//			page.onDialogClosed(this::onDialogClosed);
//		}
	}
	
	void onPage(Page page) {
		this.log.debug("onPage: {}", page.url());
		// TODO process and send to aggregator
	}
	
	void onPageLoad(Page page) {
		this.log.debug("onPageLoad: {}", page.url());
		// TODO process and send to aggregator
	}
	
	void onPageClose(Page page) {
		this.log.debug("onPageClose: {}", page);
		// TODO process and send to aggregator
	}
	
	void onRequest(Request request) {
		this.log.debug("onRequest: {}", request);
		// TODO process and send to aggregator
	}
	
	void onRequestFailed(Request request) {
		this.log.debug("onRequestFailed: {}", request);
		// TODO process and send to aggregator
	}
	
	void onRequestFinished(Request request) {
		this.log.debug("onRequestFinished: {}", request);
		// TODO process and send to aggregator
	}
	
	void onResponse(Response response) {
		this.log.debug("onResponse", response);
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
