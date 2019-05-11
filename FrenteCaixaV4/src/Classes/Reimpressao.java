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
public class Reimpressao {
        private String DataHora, descricao, chave, id, valor;
        private int fcVendedor;

    public Reimpressao(String DataHora, String descricao, String chave, String id, String valor, int fcVendedor) {
        this.DataHora = DataHora;
        this.descricao = descricao;
        this.chave = chave;
        this.id = id;
        this.valor = valor;
        this.fcVendedor = fcVendedor;
    }

    public String getDataHora() {
        return DataHora;
    }

    public void setDataHora(String DataHora) {
        this.DataHora = DataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getFcVendedor() {
        return fcVendedor;
    }

    public void setFcVendedor(int fcVendedor) {
        this.fcVendedor = fcVendedor;
    }


    


        
}
