// util.js

import { h, render } from 'vue';

export function initInstance(component, container, option) {
    const vNode = h(component, option);
    render(vNode, container);
    document.body.appendChild(container.firstElementChild);
    return vNode.component;
}

export function getContainer() {
    return document.createElement('div');
}