﻿
namespace hddbscan.monitor {

    // selected element highlight
    let highlightCssClassName: string = 'hd-dbscan-highlight';
    let style: HTMLStyleElement = document.createElement("style");
    style.innerHTML = `.${highlightCssClassName} {border: 2px dashed red !important;}`;
    document.head.appendChild(style);

    // connect collector
    let port: chrome.runtime.Port | undefined = undefined;
    function connect() {
        const portNm = "hddbscan-monitor";

        port = chrome.runtime.connect({ name: portNm });

        port.onMessage.addListener((message: any) => {
            console.log("received message: ");
            console.log(message);

            let action: string = message["action"];
            if (action === undefined) {
                return;
            }

            switch (action) {
                case "scan": {
                    let comps: any = scan();
                    port!.postMessage({
                        action: "page-components",
                        payload: comps
                    });
                    break;
                }
                case "highlight": {
                    let selector: string = message["selector"];
                    doHighlight(selector);
                    break;
                }
                default: {
                    console.error(`action ${action} is not supported`);
                }
            }
        });

        port.onDisconnect.addListener(() => {
            console.log("monitor port disconnected, try connecting...");
            port = chrome.runtime.connect({ name: portNm });
            connect();
        });
    }
    connect();

    // scan
    // 인덕대 MDI에서 스캔베이스
    const scanBase = "body > div#uuid-3h > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div#uuid-57 > div.cl-layout > div.cl-layout-content > div#uuid-58 > div.content > div.cl-tabfolder-body";
    function scan(): any {
        let comps: any = {
            targetList: []
        };
        let targetList: any[] = comps['targetList'];

        let el: HTMLElement | null = document.querySelector(scanBase);
        if (!el) {
            console.error(`scan base ${scanBase} not found`);
            return comps;
        }

        el = el.firstElementChild as HTMLElement | null;
        if (!el) {
            console.error(`scan base ${scanBase} child not found`);
            return comps;
        }

        while (el) {
            if (el.style.display != 'none') {
                break;
            }
            el = el.nextElementSibling as HTMLElement | null;
        }
        if (!el) {
            console.error(`scan base ${scanBase} shown child not found`);
            return comps;
        }

        // let nodeList: NodeListOf<HTMLElement> = document.querySelectorAll(".cl-control:not(.cl-container,.cl-notifier)");
        let nodeList: NodeListOf<HTMLElement> = el.querySelectorAll(".cl-control:not(.cl-container,.cl-notifier,.cl-embeddedapp)");
        nodeList.forEach((element: HTMLElement) => {
            let targetInfo: any = {};

            // 보이지 않는 node는 skip
            let style: CSSStyleDeclaration = window.getComputedStyle(element);
            if (style.display === 'none') {
                console.log('hidden element');
                console.log(element);
                return;
            }

            let selector: string = generateCSSSelector(element);
            targetInfo["selector"] = selector;
            // console.log(`selector: ${selector}`);

            targetInfo["classNames"] = getClassNames(element);
            targetInfo["text"] = getText(element);
            targetInfo["clientRect"] = element.getBoundingClientRect();

            targetList[targetList.length] = targetInfo;
        });

        console.log(targetList);

        return comps;
    }

    function getText(element: HTMLElement): string {
        let text: string | null = element.textContent;
        if (!text || text == "") {
            text = element.getAttribute("title");
        }
        if (!text || text == "") {
            text = element.getAttribute("aria-label");
        }

        return text ? text : "";
    }

    function getClassNames(element: HTMLElement): string[] {
        let classNames: string[] = Array.from(element.classList);
        return classNames ? classNames : [];
    }

    function generateCSSSelector(element: HTMLElement): string {
        if (!element || !element.parentElement) {
            return ""; // Return empty if no element or no parent
        }

        let selector: string = "";

        // Traverse up the DOM from the element to its parent
        while (element.parentElement) {
            let tagName: string = element.tagName.toLowerCase();
            let segment: string = tagName;

            // Add Id if it exists(IDs should be unique)
            if (element.id) {
                let elementId: string = element.id;
                elementId = elementId.replaceAll('/', '\\/');

                segment += "#" + elementId;
            } else {
                // Add classes if no ID, separating with dots
                let classNames: string = Array.from(element.classList).join(".");
                if (classNames) {
                    segment += "." + classNames;
                }
            }

            // Add nth-of-type to handle sibling elements of the same type without unique ID/classes
            const parent: HTMLElement = element.parentElement;
            const siblings: Element[] = Array.from(parent.children);
            const sameTagSiblings: Element[] = siblings.filter(el => el.tagName === element.tagName);

            if (!element.id && sameTagSiblings.length > 1) {
                // Use : nth-of-type() for specificity among similar siblings
                const index = sameTagSiblings.indexOf(element) + 1;
                segment += `:nth-of-type(${index})`;
            }

            // Prepend the segment to the selector
            selector = segment + (selector ? " > " + selector : "");

            // Move the the parent element
            element = parent;
        }

        return selector.trim();
    }

    // highlight
    function doHighlight(selector: string) {
        try {
            let element: HTMLElement | null = document.querySelector(selector);
            if (!element) {
                console.error(`${selector} not found`);
                return;
            }
            let classList: DOMTokenList = element.classList;
            // toggle
            if (classList.contains(highlightCssClassName)) {
                console.debug("highlight off")

                classList.remove(highlightCssClassName);
            } else {
                console.debug("highlight on")

                classList.add(highlightCssClassName);
            }
        } catch (e) {
            console.error(e);
            throw e;
        }
    }

}


