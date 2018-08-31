package ort.proyecto_final.mvdmart.models;

public class ExtraccionMezclaDeBolsa extends Item {

    private String codigoBotellaMezcla;

    public String getCodigoBotellaMezcla() {
        return codigoBotellaMezcla;
    }

    public void setCodigoBotellaMezcla(String codigoBotellaMezcla) {
        this.codigoBotellaMezcla = codigoBotellaMezcla;
    }

    public ExtraccionMezclaDeBolsa(String codigo, int tipo, String codigoBotellaMezcla) {
        super(codigo, tipo);
        this.codigoBotellaMezcla = codigoBotellaMezcla;
    }
}
