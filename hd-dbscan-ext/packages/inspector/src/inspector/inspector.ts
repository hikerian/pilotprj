﻿namespace hddbscan.inspector {
    const urlBase: string = "http://localhost:8080";
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
            requestAnimationFrame(() => connect());
            // port = chrome.runtime.connect({ name: portNm });
            // connect();
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
            , selector: [selector]
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

    function clearChild(tbodyElement: HTMLElement) {
        Array.from(tbodyElement.childNodes).forEach((node) => {
            tbodyElement.removeChild(node);
        });
    }

    function createChk(pageId: string) {
        var td = document.createElement("td");
        var inpChk = document.createElement("input");
        inpChk.type = "checkbox";
        inpChk.value = pageId;
        inpChk.name = "chkPageId";
        inpChk.checked = false;

        td.appendChild(inpChk);
        return td;
    }

    function createTd(txt: string) {
        var td = document.createElement("td");
        td.title = txt;

        if (txt.length > 100) {
            txt = "..." + txt.substring(txt.length - 90, txt.length);
        }
        var textNode = document.createTextNode(txt);
        td.appendChild(textNode);
        return td;
    }

    let groupSelectors: any = {};

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
            xhr.open("POST", urlBase + "/rest/page-components");
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("Accept", "application/json");
            xhr.send(JSON.stringify({
                title: pageTitle, payload: pageComps
            }));
        }

        $("#btnPageLoad").on("click", () => {
            $.ajax({
                type: "GET",
                contentType: "application/json",
                headers: {},
                url: urlBase + "/rest/ui-pages",
                success: (data: any) => {
                    let tbodyElement: HTMLElement = document.getElementById("pageListBody")!;
                    clearChild(tbodyElement);

                    let uiPageList = data.payload.uiPageList;
                    uiPageList.forEach((uiPage: any) => {
                        let tr: HTMLTableRowElement = document.createElement("tr");

                        let pageId: string = uiPage["pageId"];
                        let pageNm: string = uiPage["pageNm"];
                        let pageDesc: string = uiPage["pageDesc"];

                        tr.appendChild(createChk(pageId));
                        tr.appendChild(createTd(pageId));
                        tr.appendChild(createTd(pageNm));
                        tr.appendChild(createTd(pageDesc));

                        tbodyElement.appendChild(tr);
                    });
                }
            });
        });

        $("#btnModelGroupLoad").on("click", () => {
            var checkedValues = $('input[name="chkPageId"]:checked').map(function () {
                return $(this).val();
            }).get();

            $.ajax({
                type: "GET",
                contentType: "application/json",
                headers: {},
                url: urlBase + "/rest/model-group/" + checkedValues[0],
                success: (data: any) => {
                    let groupElement: HTMLElement = document.getElementById("modelGroups")!;
                    clearChild(groupElement);

                    groupSelectors = {};

                    let groupList = data.payload.groups;
                    groupList.forEach((group: any) => {
                        let groupId: string = group["id"];
                        let rangeTxt: string = group["rangeText"];
                        let elementList: any[] = group["uiElementList"];

                        let selectors: string[] = [];
                        groupSelectors[groupId] = selectors;

                        let div: HTMLDivElement = document.createElement("div");

                        createGroupSummary(div, groupId, rangeTxt);
                        elementList.forEach((uiElement) => {
                            createUiElementList(div, uiElement);
                            selectors.push(uiElement['selectorText']);
                        });

                        groupElement.appendChild(div);
                    });
                }
            });
        });
    }

    function createGroupSummary(parentNode: HTMLElement, groupId: string, rangeTxt: string) {
        let groupIdSpan: HTMLSpanElement = document.createElement("span");
        groupIdSpan.appendChild(document.createTextNode(groupId));
        parentNode.appendChild(groupIdSpan);

        let rangeSpan: HTMLSpanElement = document.createElement("span");
        rangeSpan.appendChild(document.createTextNode(rangeTxt));
        parentNode.appendChild(rangeSpan);

        let btn: HTMLButtonElement = document.createElement("button");
        btn.appendChild(document.createTextNode("Select"));
        btn.setAttribute("data-group-id", groupId);
        btn.onclick = doHighlight;
        parentNode.appendChild(btn);
    }

    function doHighlight(this: any) {
        let groupId: string = this.getAttribute("data-group-id");

        port!.postMessage({
            action: "highlight"
            , selector: groupSelectors[groupId]
        });

    }

    function createUiElementList(parentNode: HTMLElement, uiElement: any) {
        let div: HTMLDivElement = document.createElement("div");

        let info: any = {
            classNames: uiElement['classNames']
            , left: uiElement['posLeft']
            , top: uiElement['posTop']
        };

        div.appendChild(document.createTextNode(JSON.stringify(info)));

        parentNode.appendChild(div);
    }

    doOnload();

}
