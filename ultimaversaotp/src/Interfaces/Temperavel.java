package Interfaces;

import Exceptions.TemperaturaInvalidaException;

public interface Temperavel {
    double getTemperatura();
    void setTemperatura(double temperatura) throws TemperaturaInvalidaException;
}
