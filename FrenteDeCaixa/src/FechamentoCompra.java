
import Classes.Conexao;
import Classes.Imprimir;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class FechamentoCompra extends javax.swing.JFrame {

    /**
     * Creates new form FechamentoCompra
     */
    
    DecimalFormat df =  new DecimalFormat("0.00");
    Conexao con = new Conexao();
    Statement st;
    ResultSet rs;
    ResultSet rs2;
    int itens;
    double resultado;
    float dinheiro=0;
    double ValorCompra;
    JTable tabela = new JTable();
    Imprimir i = new Imprimir();
    
    
    public FechamentoCompra(double ValorCompra, int itens, JComponent tabela) {
        initComponents();
        ConfiguraESC();
        jTFValorCompra.setText(df.format(ValorCompra).replace(".", ","));
        this.ValorCompra=ValorCompra;
        jTFDinheiro.requestFocus();
        EnterPraGerarTroco();
        URL url = this.getClass().getResource("/imagens/ícone.png");  
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);  
        this.setIconImage(iconeTitulo);
        this.itens=itens;
        this.tabela=(JTable) tabela;
        ConfiguraCloseOperation();
    }
    
    private void ConfiguraCloseOperation(){
        addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    TelaPrincipal.AparecerTela=true;
                    dispose();
                }
            }
	);
    }
    
    private static String geraStringAleatoria() {
        int qtdeMaximaCaracteres = 9;
        String[] caracteres = { "0", "1", "b", "2", "4", "5", "6", "7", "8",
                "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
                "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
                "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z" };
       
        StringBuilder senha = new StringBuilder();

        for (int i = 0; i < qtdeMaximaCaracteres; i++) {
            int posicao = (int) (Math.random() * caracteres.length);
            senha.append(caracteres[posicao]);
        }
        return senha.toString();
    }

    
    private void ConfiguraESC(){
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                TelaPrincipal.AparecerTela=true;
                dispose();
            }
        });
    }
    
    private void EnterPraGerarTroco(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                boolean testeRuim=false;
                if (jTFDinheiro.getText().split(",").length>1){
                    try {
                        String[] dinheiroVetor=jTFDinheiro.getText().split(",");
                        dinheiro=Float.parseFloat(dinheiroVetor[0]+"."+dinheiroVetor[1]);
                    } catch (NumberFormatException n){
                        JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LC - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                        jTFDinheiro.setText("");
                        jTFDinheiro.requestFocus();
                    }
                } else {
                    try {
                        dinheiro=Float.parseFloat(jTFDinheiro.getText());
                    } catch (NumberFormatException n){
                        JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LC - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                        jTFDinheiro.setText("");
                        jTFDinheiro.requestFocus();
                        testeRuim=true;
                    }
                }
                if (dinheiro>=Float.parseFloat(jTFValorCompra.getText().replace(",", "."))){
                    resultado=dinheiro-Double.parseDouble(jTFValorCompra.getText().replace(",", "."));
                    jTFTroco.setText(df.format(resultado));
                    EnterPraImprimirEFechar();
                    jTFDinheiro.setFocusable(false);
                } else {
                    if (testeRuim==false){
                        JOptionPane.showMessageDialog(null, "Dinheiro menor que o total da compra", "LC - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                        jTFDinheiro.setText("");
                        jTFDinheiro.requestFocus();
                    }
                }
            }
        });
    }
    
    private void EnterPraImprimirEFechar(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            private MouseEvent evt;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBConfirmarActionPerformed(arg0);
            }
        });
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTFValorCompra = new javax.swing.JTextField();
        jTFDinheiro = new javax.swing.JTextField();
        jTFTroco = new javax.swing.JTextField();
        jLTotalCompra = new javax.swing.JLabel();
        jLDinheiro = new javax.swing.JLabel();
        jLTroco = new javax.swing.JLabel();
        jBConfirmar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("LC - Fechamento de Compra");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jTFValorCompra.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFValorCompra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFValorCompra.setFocusable(false);

        jTFDinheiro.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFDinheiro.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTFTroco.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFTroco.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFTroco.setFocusable(false);

        jLTotalCompra.setText("Total Compra:");

        jLDinheiro.setText("Dinheiro: ");

        jLTroco.setText("Troco:");

        jBConfirmar.setText("Confirmar");
        jBConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLTroco)
                            .addComponent(jLDinheiro)
                            .addComponent(jLTotalCompra)
                            .addComponent(jTFTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFDinheiro, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(jBConfirmar)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLTotalCompra)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLDinheiro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFDinheiro, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLTroco)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jBConfirmar)
                .addGap(40, 40, 40))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        // TODO add your handling code here:
        requestFocus();
        toFront();
    }//GEN-LAST:event_formWindowLostFocus

    private void jBConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarActionPerformed
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (jTFTroco.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LC - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
            jTFDinheiro.requestFocus();
        } else {
            long unixTime;
            DefaultTableModel val = (DefaultTableModel) tabela.getModel();
            try {
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                unixTime = System.currentTimeMillis() / 1000L;
                con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, integra_movimento) VALUES ("+TelaPrincipal.Id_CaixaAberto+", "+TelaPrincipal.Id_eventoAtivo+", "+TelaPrincipal.id_operadorAtivo+", "+TelaPrincipal.pdvAtivo+", '"+timeStamp+"', 10, 'Venda Produtos', "+itens+", "+ValorCompra+", "+dinheiro+", "+resultado+", '"+unixTime+"', 0)");
                rs=con.Select("SELECT saldo_caixa FROM fc_caixa USE INDEX (id_fc_caixa) WHERE id_fc_caixa="+TelaPrincipal.Id_CaixaAberto);
                while (rs.next()){
                    double saldoAntigo = Double.parseDouble(rs.getString("saldo_caixa").replace(",", "."));
                    double saldoAtual = saldoAntigo+ValorCompra;
                    con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET saldo_caixa="+saldoAtual+" WHERE id_fc_caixa="+TelaPrincipal.Id_CaixaAberto);
                }
                rs=con.Select("SELECT id_fc_movimento FROM fc_movimento USE INDEX (id_fc_movimento) ORDER BY id_fc_movimento DESC LIMIT 1");
                while (rs.next()){
                    for (int x=0;x<val.getRowCount();x++){
                        for (int y=0;y<Integer.parseInt((String) val.getValueAt(x, 2));y++){
                            String chave = geraStringAleatoria();
                            String preco_produto=df.format(Double.parseDouble(((String)val.getValueAt(x, 3)).replace(",", "."))/Double.parseDouble(((String)val.getValueAt(x, 2)))).replace(".", ",0");
                            unixTime = System.currentTimeMillis() / 1000L;
                            //i.ImprimeTicket(TelaPrincipal.img_eventoAtivo, TelaPrincipal.nome_eventoAtivo, (String) val.getValueAt(x, 1), preco_produto, geraStringAleatoria()+unixTime, TelaPrincipal.Id_eventoAtivo);
                            con.Insert("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, integra_movimento, sts_movimento, pdv_movimento) VALUES ("+TelaPrincipal.id_operadorAtivo+", "+TelaPrincipal.Id_CaixaAberto+", "+TelaPrincipal.Id_eventoAtivo+", "+rs.getInt("id_fc_movimento")+", "+val.getValueAt(x, 0)+", 10, '"+(String) val.getValueAt(x, 1)+"', '"+Double.toString(Double.parseDouble(((String)val.getValueAt(x, 3)).replace(",", "."))/Double.parseDouble(((String)val.getValueAt(x, 2))))+"', '"+chave+unixTime+"', "+unixTime+", 0, 0, '"+TelaPrincipal.pdvAtivo+"')");
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(FechamentoCompra.class.getName()).log(Level.SEVERE, null, ex);
            }
            TelaPrincipal.FechouCompra=true;
            TelaPrincipal.AparecerTela=true;
            dispose();
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jBConfirmarActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBConfirmar;
    private javax.swing.JLabel jLDinheiro;
    private javax.swing.JLabel jLTotalCompra;
    private javax.swing.JLabel jLTroco;
    private javax.swing.JTextField jTFDinheiro;
    private javax.swing.JTextField jTFTroco;
    private javax.swing.JTextField jTFValorCompra;
    // End of variables declaration//GEN-END:variables
}
