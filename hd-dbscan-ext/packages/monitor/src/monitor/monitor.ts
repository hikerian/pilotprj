﻿
namespace hddbscan.monitor {

    let port: chrome.runtime.Port = chrome.runtime.connect({ name: "hddbscan-monitor" });

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
                port.postMessage({
                    action: "page-components",
                    payload: comps
                });
                break;
            }
            default: {

            }
        }
    });

    function scan(): any {
        let comps: any = {
            targetList: []
        };
        let targetList: any[] = comps['targetList'];

        let nodeList: NodeListOf<HTMLElement> = document.querySelectorAll(".cl-control:not(.cl-container,.cl-notifier)");
        nodeList.forEach((element: HTMLElement) => {
            let eventTarget: any = {
                target: {}
            };
            let targetInfo: any = eventTarget['target'];

            let selector: string = generateCSSSelector(element);
            targetInfo["selector"] = selector;
            console.log(`selector: ${selector}`);

            targetInfo["classNames"] = getClassNames(element);
            targetInfo["text"] = element.textContent;
            targetInfo["clientRect"] = element.getBoundingClientRect();

            targetList[targetList.length] = eventTarget;
        });

        return comps;
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
                segment += "#" + element.id;
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







}


