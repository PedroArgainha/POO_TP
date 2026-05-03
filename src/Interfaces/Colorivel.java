package Interfaces;

import Exceptions.CorInvalidaException;

public interface Colorivel {
    int getCor();
    void setCor(int kelvin) throws CorInvalidaException;
}
