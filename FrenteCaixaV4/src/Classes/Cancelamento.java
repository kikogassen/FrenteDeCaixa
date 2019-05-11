/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

/**
 *
 * @author WEB02
 */
public class Cancelamento {
    private String vendedor;
    private int soma;

    public Cancelamento(String mov, int soma) {
        this.vendedor = mov;
        this.soma = soma;
    }

    public String getMov() {
        return vendedor;
    }

    public void setMov(String mov) {
        this.vendedor = mov;
    }

    public int getSoma() {
        return soma;
    }

    public void setSoma(int soma) {
        this.soma = soma;
    }
    
}
