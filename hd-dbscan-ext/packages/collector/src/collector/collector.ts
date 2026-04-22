﻿
namespace hddbscan.collector {
    let inspectorPort: chrome.runtime.Port | undefined = undefined;
    let monitorPort: chrome.runtime.Port | undefined = undefined;

    function postAccessTree() {
        // 일부 크롬확장의 경우 콘텐츠 페이지에 iframe을 생성하는 것이 있는데 이럴 경우 동작하지 않음.
        chrome.debugger.attach({ tabId: activeTabId }, "1.3", () => {
            chrome.debugger.sendCommand({ tabId: activeTabId }, "Accessibility.enable", {}, () => {
                chrome.debugger.sendCommand({ tabId: activeTabId }, "Accessibility.getFullAXTree", {}, (axTree) => {
                    chrome.debugger.detach({ tabId: activeTabId });

                    inspectorPort?.postMessage({
                        action: "accessTree",
                        axTree: axTree
                    });
                });
            });
        });
    }

    chrome.runtime.onConnect.addListener((port: chrome.runtime.Port) => {
        let portName: string = port.name;
        console.log("connected port:" + portName);

        switch (portName) {
            case "hddbscan-monitor": {
                console.log("Monitor Connected!");
                monitorPort = port;
                monitorPort.onMessage.addListener((message: any) => {
                    if (!message) {
                        return;
                    }
                    let action: string = message['action'];
                    switch (action) {
                        case "page-components": {
                            let payload: any = message['payload'];

                            console.log(`${action}`);
                            console.log(payload);

                            inspectorPort!.postMessage({
                                action: "collection-result",
                                payload: payload
                            });
                            break;
                        }
                        default: {

                        }
                    }
                });
                break;
            }
            case "hddbscan-inspector": {
                console.log("Inspector Connected!");
                inspectorPort = port;
                inspectorPort.onMessage.addListener((message: any) => {
                    if (!message) {
                        return;
                    }
                    let action: string = message['action'];
                    switch (action) {
                        case "collect": {
                            if (monitorPort === undefined) {
                                console.log("not connected");
                                // TODO send feedback
                            } else {
                                monitorPort.postMessage({
                                    action: "scan"
                                });
                            }

                            break;
                        }
                        case "highlight": {
                            if (monitorPort === undefined) {
                                console.log("not connected");
                                // TODO send feedback
                            } else {
                                let selector: string = message['selector'];

                                monitorPort.postMessage({
                                    action: "highlight"
                                    , selector: selector
                                });
                            }

                            break;
                        }
                        case "accessTree": {
                            postAccessTree();
                            break;
                        }
                        default: {

                        }
                    }
                });

                break;
            }
            default: {
                console.log(`${portName} is connection prevent`);
                port.disconnect();
                return;
            }

        }
    });

    let inspectorWindowId: number | undefined;
    let activeTabId: number | undefined;
    chrome.action.onClicked.addListener((tab: chrome.tabs.Tab) => {
        console.log("ActionClicked: ");
        console.log(tab);

        activeTabId = tab.id;

        if (inspectorWindowId) {
            chrome.windows.update(inspectorWindowId!, { focused: true }).catch(() => {
                createNewInspector();
            });
        } else {
            createNewInspector();
        }

    });

    function createNewInspector() {
        chrome.windows.create({
            url: chrome.runtime.getURL("inspector/inspector.html")
            , type: "popup"
            , width: 1200
            , height: 700
        }
            , (insWindow?: chrome.windows.Window | undefined) => {
                console.log(insWindow);
                inspectorWindowId = insWindow?.id;
            }
        );
    }


}
