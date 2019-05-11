package Classes;

import java.awt.Color;
import javax.swing.JTextField;

public class ChecaVazio {
    public void ChecaVazio(JTextField field, String valor) {
        if ("".equals(valor)) {
            field.setBackground(Color.yellow);
            field.requestFocus();
        }
    }
}
