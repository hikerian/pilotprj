﻿namespace hddbscan.inspector {
    let port: chrome.runtime.Port = chrome.runtime.connect({ name: "hddbscan-inspector" });

    port.onMessage.addListener((message: any) => {
        if (!message) {
            return;
        }
        let action: string = message['action'];
        switch (action) {
            case "collection-result": {
                let payload: any = message['payload'];

                console.log(`${action}`);
                console.log(payload);

                let tblComps: HTMLTableElement = <HTMLTableElement>document.getElementById("tblComps");
                let tbody: HTMLTableSectionElement = tblComps.tBodies[0];

                // TODO implements....
                // 선택된 컴포넌트 목록을 보여주고 선택하면 하이라이트 하게 지원...

                // BACKUP
                // let pageTitle: string = (<HTMLInputElement>document.getElementById("inpTitle")).value;

                // // send to spring server
                // let xhr: XMLHttpRequest = new XMLHttpRequest();
                // xhr.onreadystatechange = function () {
                //     if (this.readyState == XMLHttpRequest.DONE && this.status == 200) {
                //         console.log("XHR Response: " + xhr.responseText);

                //     }
                // }
                // xhr.open("POST", "http://localhost:8080/rest/page-components");
                // xhr.setRequestHeader("Content-Type", "application/json");
                // xhr.setRequestHeader("Accept", "application/json");
                // xhr.send(JSON.stringify({
                //     titile: pageTitle, payload: payload
                // }));
                break;
            }
            default: {

            }

        }
    });

    // 시점을 잡을 수 없음
    // if (document.readyState === "complete") {
    //     doOnload();
    // } else {
    //     document.onload = function () {
    //         doOnload();
    //     }
    // }

    function doOnload() {
        console.log("doOnload!");
        let btnClt: HTMLElement = document.getElementById("btnClt")!;

        btnClt.onclick = function () {
            port.postMessage({
                action: "collect"
            });
        }

    }

    doOnload();

}