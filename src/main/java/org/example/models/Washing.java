package org.example.models;

import org.example.Observed;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

public class Washing extends Observed implements ObslugaFunkcjonalnosciDomu {
    boolean isActive = false;
    boolean isFinished = false;

    @Override public void turnOn()  { if (!isActive) isActive = true;  }
    @Override public void turnOff() { if (isActive)  isActive = false; }
    @Override public boolean checkStatus() { return isActive; }

    //setter do prania, bez tego nie zadziala(Death loop)
    public void setFinished(boolean finished) { this.isFinished = finished; }

    public void isWashingFinished() {
        if (isFinished) {
            notifyObservers(Washing.class.toString(), "Pranie skonczone");
        }
    }

    @Override
    public String toString() {
        return "Washing{isActive=" + isActive + ", isFinished=" + isFinished + "}";
    }
}
