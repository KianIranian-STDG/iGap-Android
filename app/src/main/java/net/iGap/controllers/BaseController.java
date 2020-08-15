package net.iGap.controllers;

import net.iGap.observers.eventbus.EventManager;

public class BaseController {
    private int currentAccount;

    public BaseController(int currentAccount) {
        this.currentAccount = currentAccount;
    }

    public EventManager getEventManager() {
        return EventManager.getInstance();
    }
}
