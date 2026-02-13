﻿
namespace hddbscan.collector {
    let inspectorPort: chrome.runtime.Port | undefined = undefined;
    let monitorPort: chrome.runtime.Port | undefined = undefined;

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

    chrome.action.onClicked.addListener((tab: chrome.tabs.Tab) => {
        console.log("ActionClicked: ");
        console.log(tab);

        chrome.windows.create({
            url: chrome.runtime.getURL("inspector/inspector.html")
            , type: "popup"
            , width: 900
            , height: 700
        }
            , (window?: chrome.windows.Window | undefined) => {
                console.log(window);
            }
        );

    });

}
