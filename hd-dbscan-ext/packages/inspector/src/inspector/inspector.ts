﻿namespace hddbscan.inspector {
    let port: chrome.runtime.Port = chrome.runtime.connect({ name: "hddbscan-inspector" });

    let pageComps: any = {};

    port.onMessage.addListener((message: any) => {
        if (!message) {
            return;
        }
        let action: string = message['action'];
        switch (action) {
            case "collection-result": {
                let payload: any = message['payload'];
                pageComps = payload;

                console.log(`${action}`);
                console.log(payload);

                let tblComps: HTMLTableElement = <HTMLTableElement>document.getElementById("tblComps");
                let tbody: HTMLTableSectionElement = tblComps.tBodies[0];

                let compList: any[] = payload['targetList'];

                createCompNode(tbody, compList);

                // TODO implements....
                // 선택된 컴포넌트 목록을 보여주고 선택하면 하이라이트 하게 지원...


                break;
            }
            default: {

            }

        }
    });

    function createCompNode(tbody: HTMLTableSectionElement, compList: any[]) {
        Array.from(tbody.childNodes).forEach((node: ChildNode) => {
            tbody.removeChild(node);
        });

        compList.forEach((comp: any) => {
            let selector: string = comp['selector'];
            let classNameList: string[] = comp['classNames'];
            let classNames: string = Array.isArray(classNameList) ? classNameList.join(",") : "" + classNameList;
            let text: string = comp['text'];
            let clientRect: string = JSON.stringify(comp['clientRect']);

            let tr: HTMLTableRowElement = document.createElement("tr");
            tr.appendChild(createCompFeature(selector)); // <th>Selector</th>
            tr.appendChild(createCompFeature(classNames)); // <th>ClassNames</th>
            tr.appendChild(createCompFeature(text)); // <th>Text</th>
            tr.appendChild(createCompFeature(clientRect)); // <th>ClientRect</th>
            tr.appendChild(createSelectButton(selector)); // <th>Select</th>

            tbody.appendChild(tr);
        });
    }

    function createCompFeature(featureValue: string): HTMLTableCellElement {
        let td: HTMLTableCellElement = document.createElement("td");
        let div: HTMLDivElement = document.createElement("div");
        div.style.display = "inline-block";
        div.style.width = "100%";
        div.style.whiteSpace = "nowrap";
        div.style.overflow = "hidden";
        td.appendChild(div);

        let textNode: Text = document.createTextNode(featureValue);
        div.appendChild(textNode);

        return td;
    }

    function createSelectButton(selector: string): HTMLTableCellElement {
        let td: HTMLTableCellElement = document.createElement("td");
        let btn: HTMLButtonElement = document.createElement("button");
        td.appendChild(btn);

        btn.setAttribute("data-selector", selector);
        let textNode: Text = document.createTextNode("Select");
        btn.appendChild(textNode);
        btn.onclick = doOnCompSelect;

        return td;
    }

    let doOnCompSelect = function (this: any) {
        console.log(this);
    }

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

        let btnSave: HTMLElement = document.getElementById("btnSave")!;
        btnSave.onclick = function () {
            let pageTitle: string = (<HTMLInputElement>document.getElementById("inpTitle")).value;

            // send to spring server
            let xhr: XMLHttpRequest = new XMLHttpRequest();
            xhr.onreadystatechange = function () {
                if (this.readyState == XMLHttpRequest.DONE && this.status == 200) {
                    console.log("XHR Response: " + xhr.responseText);

                }
            }
            xhr.open("POST", "http://localhost:8080/rest/page-components");
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("Accept", "application/json");
            xhr.send(JSON.stringify({
                titile: pageTitle, payload: pageComps
            }));
        }

    }

    doOnload();

}
