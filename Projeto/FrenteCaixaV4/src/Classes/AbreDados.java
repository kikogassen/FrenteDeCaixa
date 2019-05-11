package Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AbreDados {
    public AbreDados (){}
    //abre, lê e descriptografa dados
    public String[] Abrirdados (String dados[]){
        try {
            try (BufferedReader in = new BufferedReader(new FileReader("dados.txt"))) {
                String str;
                Cript cript = new Cript();
                for (int x=0;x<4;x++){
                    dados[x]= in.readLine();
                    try {
                        dados[x]=cript.descrip(dados[x]);
                    } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
                        Logger.getLogger(AbreDados.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (IOException e) {
            Logger.getLogger(AbreDados.class.getName()).log(Level.SEVERE, null, e);
        }
        return dados;
    }
}