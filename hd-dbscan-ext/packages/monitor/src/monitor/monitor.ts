﻿
namespace hddbscan.monitor {

    let port: chrome.runtime.Port = chrome.runtime.connect({ name: "hddbscan-monitor" });

    port.onMessage.addListener((message: any) => {
        console.log("received message: " + message);

        let action: string = message["action"];
        if (action === undefined) {
            return;
        }

        switch (action) {
            case "scan": {
                scan();
                break;
            }
            default: {
                return;
            }
        }
    });

    function scan(): any {
        let nodeList: NodeListOf<HTMLElement> = document.querySelectorAll(".cl-control");
        nodeList.forEach((element: HTMLElement) => {
            let xpath: string = getXPath(element);

            console.log("xpath: " + xpath);

        });


        return {};
    }

    function getXPath(element: HTMLElement): string {
        if (element.id !== '') {
            // Use ID for a unique and more stable XPath if available
            return '//*[@id="' + element.id + '"]';
        }
        if (element === document.body) {
            return '/html/body';
        }

        const index: number = getElementIndex(element);
        const tagName: string = element.tagName.toLowerCase();

        const parentNode: ParentNode | null = element.parentNode;

        const path = (parentNode ? getXPath(<HTMLElement>(parentNode!)) : '') + '/' + tagName + '[' + index + ']';

        return path;
    }

    function getElementIndex(element: HTMLElement): number {
        let index = 1;
        let sibling = element.previousElementSibling;
        while (sibling) {
            if (sibling.nodeType === 1 && sibling.tagName === element.tagName) {
                index++;
            }
            sibling = sibling.previousElementSibling;
        }
        return index;
    }

}


