﻿namespace hddbscan.inspector {
    let pageComps: any = {};

    // connect collector
    let port: chrome.runtime.Port | undefined = undefined;
    function connect() {
        const portNm = "hddbscan-inspector";

        port = chrome.runtime.connect({ name: portNm });

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

                    break;
                }
                default: {

                }

            }
        });

        port.onDisconnect.addListener(() => {
            console.log("inspector port disconnected, try connecting...");
            port = chrome.runtime.connect({ name: portNm });
            connect();
        });
    }
    connect();

    function createCompNode(tbody: HTMLTableSectionElement, compList: any[]) {
        Array.from(tbody.childNodes).forEach((node: ChildNode) => {
            tbody.removeChild(node);
        });

        compList.forEach((comp: any) => {
            let selector: string = comp['selector'];
            let classNameList: string[] = comp['classNames'];
            let classNames: string = Array.isArray(classNameList) ? classNameList.join(",") : "" + classNameList;
            let text: string = comp['text'];
            let major: boolean = comp['major'];
            let clientRect: string = JSON.stringify(comp['clientRect']);

            let tr: HTMLTableRowElement = document.createElement("tr");
            tr.appendChild(createCompFeature(selector, 350)); // <th>Selector</th>
            tr.appendChild(createCompFeature(classNames, 300)); // <th>ClassNames</th>
            tr.appendChild(createCompFeature(text, 110)); // <th>Text</th>
            tr.appendChild(createCompFeature(clientRect, 200)); // <th>ClientRect</th>
            tr.appendChild(createCheckbox(major, selector)); // <th>Major</th>
            tr.appendChild(createSelectButton(selector)); // <th>Select</th>

            tbody.appendChild(tr);
        });
    }

    function createCompFeature(featureValue: string, width: number): HTMLTableCellElement {
        let td: HTMLTableCellElement = document.createElement("td");
        let div: HTMLDivElement = document.createElement("div");
        td.appendChild(div);

        let textNode: Text = document.createTextNode(featureValue);
        div.appendChild(textNode);

        div.classList.add("feature");
        div.style.width = `${width}px`;
        div.setAttribute("title", featureValue);

        return td;
    }

    function createSelectButton(selector: string): HTMLTableCellElement {
        let td: HTMLTableCellElement = document.createElement("td");
        let btn: HTMLButtonElement = document.createElement("button");
        td.appendChild(btn);

        btn.setAttribute("data-selector", selector);
        btn.style.width = '80px';
        let textNode: Text = document.createTextNode("Select");
        btn.appendChild(textNode);
        btn.onclick = doOnCompSelect;

        return td;
    }
    let doOnCompSelect = function (this: any) {
        console.log(this);
        let btn: HTMLButtonElement = this as HTMLButtonElement;
        let selector: string = btn.getAttribute("data-selector")!;

        port!.postMessage({
            action: "highlight"
            , selector: selector
        });
    }

    function createCheckbox(value: boolean, selector: string): HTMLTableCellElement {
        let td: HTMLTableCellElement = document.createElement("td");
        let chk: HTMLInputElement = document.createElement("input");
        chk.type = "checkbox";
        chk.value = "true";
        chk.checked = value;
        td.appendChild(chk);

        chk.setAttribute("data-selector", selector);
        chk.style.width = '50px';
        chk.onclick = doOnCompCheck;

        return td;
    }
    let doOnCompCheck = function (this: any) {
        console.log(this);
        let chk: HTMLInputElement = this as HTMLInputElement;
        let selector: string = chk.getAttribute("data-selector")!;
        let checked: boolean = chk.checked;

        let compList: any[] = pageComps['targetList'];

        compList.some((comp: any) => {
            if (comp['selector'] === selector) {
                comp['major'] = checked;

                document.getElementById("msg-viewer")!.innerText = `matched checked:${checked}`;

                return true;
            }

            return false;
        });
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
            port!.postMessage({
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
                title: pageTitle, payload: pageComps
            }));
        }

    }

    doOnload();

}
