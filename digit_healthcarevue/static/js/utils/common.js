// common.js

import { initInstance, getContainer } from "./util";
import PortPop from "../components/PortPop";

const instanceMap = new Map();

export const portPop = (option, call) => {
    const container = getContainer();
    let opt = {
        ...option,
        onComfrim: (data) => {
            call(data);
        },
        onVanish: () => {
            render(null, container);
            instanceMap.delete(vm);
        },
    };
    const component = initInstance(PortPop, container, opt);
    const vm = component.proxy;
    component.exposed.openDialog();
    instanceMap.set(vm, { option: opt });
};
