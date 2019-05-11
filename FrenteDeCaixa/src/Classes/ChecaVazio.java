package Classes;


import java.awt.Color;
import javax.swing.JTextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class ChecaVazio {
    public void ChecaVazio(JTextField field, String valor) {
        if ("".equals(valor)) {
            field.setBackground(Color.yellow);
            field.requestFocus();
        }
    }
}
