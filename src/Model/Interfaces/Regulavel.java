package Model.Interfaces;

import Model.Exceptions.NivelInvalidoException;

// uma interface serve para definir um tipo de dados comportamentais
// é um contrato de métodos a que certas classes têm de responder
// permitem agrupar comportamento comum entre classes
public interface Regulavel {

    int getNivel();
    void setNivel(int valor) throws NivelInvalidoException;


}
