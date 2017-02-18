/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class Plot {

    private BuildOff bo;
    private Contestant contestant;
    private int plotNr;

    public Plot(BuildOff bo, int plotNr) {
        this.bo = bo;
        this.plotNr = plotNr;
        this.contestant = null;
    }

    public Plot(BuildOff bo, int plotNr, Contestant contestant) {
        this.bo = bo;
        this.contestant = contestant;
        this.plotNr = plotNr;
    }

    public void setContestant(Contestant contestant) {
        this.contestant = contestant;
    }

    public Contestant getContestant() {
        return contestant;
    }

    public void reset() {
        bo.addResetContestant(contestant);
        this.contestant = null;
    }

    private void clear() {

    }
}
