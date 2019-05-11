package Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Conexao {
    AbreDados ad = new AbreDados();
    String[] str = new String [4];
    String dados[]= ad.Abrirdados(str);
    private String host = "/" + dados[0];
    private String banco = "/" + dados[1];
    private String login = dados[2];
    private String senha = dados[3];
    String url = "jdbc:mysql:/"+host+banco;
    Connection conexao;
    Statement st;
    ResultSet rs;

    public Conexao(){
        try {
            conexao = DriverManager.getConnection(url, login, senha);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
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
}
