package Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

public class ConexaoServer {
    Conexao con = new Conexao();
    
    Connection conexao;
    Statement st;
    ResultSet rs, rs2;

    public ConexaoServer(){
        
        try {
            rs=con.Select("SELECT banco_unico FROM fc_config");
            rs.first();
            
            if (rs.getInt("banco_unico")==0){
                rs2=con.Select("SELECT host2, banco2, login2, senha2 FROM fc_config");
                while (rs2.next()){
                    String host = "/" + rs2.getString("host2");
                    String banco = "/" + rs2.getString("banco2");
                    String login = rs2.getString("login2");
                    String senha = rs2.getString("senha2");
                    String url = "jdbc:mysql:/"+host+banco;
                    Cript cripta = new Cript();
                    login=cripta.descrip(login);
                    senha=cripta.descrip(senha);
                    conexao = DriverManager.getConnection(url, login, senha);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Não há um banco de dados secundário", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            JOptionPane.showMessageDialog(null, "Erro na criptografia dos dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public ResultSet Select(String query) throws SQLException {
        st=conexao.createStatement();
        st.executeQuery(query);
        rs=st.getResultSet();
        return rs;
    }
    
    public void Insert (String query) throws SQLException {
        st=conexao.createStatement();
        st.executeUpdate(query);
    }
    
    public boolean TestaConexao() throws SQLException {
        if (conexao.isValid(0)){
            return true;
        } else {
            return false;
        }
    }
}