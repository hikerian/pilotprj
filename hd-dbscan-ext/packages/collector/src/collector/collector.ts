﻿
namespace hddbscan.collector {
    let monitorPort: chrome.runtime.Port | undefined = undefined;

    chrome.runtime.onConnect.addListener((port: chrome.runtime.Port) => {
        let portName: string = port.name;
        console.log("connected port:" + portName);

        if (portName != "hddbscan-monitor") {
            return;
        }

        monitorPort = port;

        monitorPort.onMessage.addListener((message: any) => {

        });
    });

    chrome.action.onClicked.addListener((tab: chrome.tabs.Tab) => {
        console.log("ActionClicked: " + tab);
        if (monitorPort === undefined) {
            console.log("not connected");
        }

        monitorPort!.postMessage({
            action: "scan"
        });
    });

}
