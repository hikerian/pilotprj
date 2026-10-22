package com.hikerian.autotest.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Selectors;


/**
 * com.microsoft.playwright.Playwright의 생명주기를 관리하는 Class.
 */
public final class PlaywrightManager {
	private final Logger log = LoggerFactory.getLogger(PlaywrightManager.class);
	
	private PlaywrightWrapper playwrightWrapper;
	
	private static final PlaywrightManager instance = new PlaywrightManager();
	
	
	public static final PlaywrightManager getInstance() {
		return PlaywrightManager.instance;
	}
	
	private PlaywrightManager() {
	}
	
	public synchronized Playwright createPlaywright() throws AlreadyExistsException {
		/*
		 * Playwright CreateOption이 필요하면 여기에 구현 추가
		 */
		if(this.playwrightWrapper == null || this.playwrightWrapper.isClosed()) {
			this.playwrightWrapper = this.new PlaywrightWrapper(Playwright.create());
			
			return this.playwrightWrapper;
		}
		
		this.log.warn("already exists active playwright");
		
		throw new AlreadyExistsException("Playwright Instance Already Exists");
	}
	
	public void closePlaywright() {
		if(this.playwrightWrapper != null && this.playwrightWrapper.isClosed() == false) {
			this.playwrightWrapper.close();
		}
	}
	
	class PlaywrightWrapper implements Playwright {
		private final Playwright playwright;
		private boolean closed = false;
		
		
		public PlaywrightWrapper(Playwright playwright) {
			this.playwright = playwright;
		}

		@Override
		public BrowserType chromium() {
			return this.playwright.chromium();
		}
		@Override
		public BrowserType firefox() {
			return this.playwright.firefox();
		}
		@Override
		public APIRequest request() {
			return this.playwright.request();
		}
		@Override
		public Selectors selectors() {
			return this.playwright.selectors();
		}
		@Override
		public BrowserType webkit() {
			return this.playwright.webkit();
		}
		@Override
		public void close() {
			this.playwright.close();

			this.closed = true;
			PlaywrightManager.this.playwrightWrapper = null;
		}
		
		public boolean isClosed() {
			return this.closed;
		}
		
	}
}
