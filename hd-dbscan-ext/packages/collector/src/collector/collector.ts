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
            if (!message) {
                return;
            }
            let action: string = message['action'];
            switch (action) {
                case "page-components": {
                    let payload: any = message['payload'];

                    console.log(`${action}`);
                    console.log(payload);

                    // send to spring server

                    break;
                }
                default: {

                }
            }
        });
    });

    chrome.action.onClicked.addListener((tab: chrome.tabs.Tab) => {
        console.log("ActionClicked: ");
        console.log(tab);
        if (monitorPort === undefined) {
            console.log("not connected");
        }

        monitorPort!.postMessage({
            action: "scan"
        });
    });

}
