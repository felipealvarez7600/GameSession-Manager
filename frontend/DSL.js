function createElement(tagName, attributes, ...children) {
    const element = document.createElement(tagName);

    if (attributes) {
        for (const [key, value] of Object.entries(attributes)) {
            element.setAttribute(key, value);
        }
    }

    children.forEach(child => {
        if (child instanceof HTMLElement) {
            element.appendChild(child);
        }
        else if (typeof child === "string") {
            element.appendChild(document.createTextNode(child))
        } else if (Array.isArray(child)) {
            // If the child is an array, recursively call createElement
            child.forEach(subChild => {
                if(subChild instanceof HTMLElement) {
                    element.appendChild(subChild);
                } else {
                    element.appendChild(createElement(subChild.tagName, subChild.attributes, ...subChild.children));
                }
            });
        } else {
            throw new Error("Child element must be either a string, an HTMLElement, or an array of elements.");
        }
    });
    return element;
}
export function span(...children){
    return createElement("span", null, ...children);
}
export function div(...children){
    return createElement("div", null, ...children);
}

export function div2(attributes, ...children) {
    return createElement("div", attributes, ...children.map(child => typeof child === "string" ? document.createTextNode(child) : child));
}

export function ul(...children){
    return createElement("ul", null, ...children);
}

export function ul2(attributes, ...children){
    return createElement("ul", attributes, ...children);
}

export function li(...children){
    return createElement("li", null, ...children);
}

export function li2(attributes, ...children){
    return createElement("li", attributes, ...children);

}

export function h1(...children){
    return createElement("h1", null, ...children);
}

export function h2(...children){
    return createElement("h2", null, ...children);
}

export function p(...children){
    return createElement("p", null, ...children);
}

export function p2(attributes, ...children){
    return createElement("p", attributes, ...children);
}

export function a(attributes, ...children) {
    return createElement("a", attributes, ...children);
}

export function form(attributes, eventListener, ...children) {
    const element = createElement("form", attributes, ...children);
    if (eventListener) {
        element.addEventListener("submit", eventListener);
    }
    return element;
}

export function label(attributes, ...children) {
    return createElement("label", attributes, ...children);
}

export function label2(attributes, ...children) {
    return createElement("label", attributes, ...children.map(child => typeof child === "string" ? document.createTextNode(child) : child));
}

export function button(attributes, ...children) {
    return createElement("button", attributes, ...children);
}

export function input(attributes, ...children) {
    return createElement("input", attributes, ...children);
}

export function select(attributes, ...children) {
    return createElement("select", attributes, ...children);
}

export function option(attributes, ...children) {
    return createElement("option", attributes, ...children);
}

export function hr(attributes) {
    return createElement("hr", attributes);
}

export function nav(attributes, ...children) {
    return createElement("nav", attributes, ...children);
}

export function image(attributes) {
    return createElement("img", attributes);
}