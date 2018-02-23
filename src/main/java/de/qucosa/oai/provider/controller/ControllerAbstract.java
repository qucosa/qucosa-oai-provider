package de.qucosa.oai.provider.controller;

public abstract class ControllerAbstract {
    private boolean test = false;
    
    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }
}
