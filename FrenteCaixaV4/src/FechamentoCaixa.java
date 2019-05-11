
import Classes.Conexao;
import Classes.Imprimir;
import Classes.Pagto;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class FechamentoCaixa extends javax.swing.JFrame {

    Conexao con = new Conexao();
    Statement st;
    ResultSet rs;
    DecimalFormat df = new DecimalFormat("0.00");
    Imprimir i = new Imprimir();
    float debito = 0, credito = 0, banri = 0;
    LinkedList<Pagto> listaCombo;

    public FechamentoCaixa(double valor, double valor2) {
        initComponents();
        jTFSaldoDinheiro.setText(df.format(valor).replace(".", ","));
        jTFSaldoCartao.setText(df.format(valor2).replace(".", ","));
        EnterPraSalvar();
        ConfiguraESC();
        URL url = this.getClass().getResource("/imagens/icone.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(iconeTitulo);
        ConfiguraCloseOperation();
        jTFSaldoDinheiro.setEditable(false);
        jTFSaldoCartao.setEditable(false);
        jBConfirmar.requestFocus();
        listaCombo = new LinkedList<Pagto>();
        listaLista();
    }

    private void ConfiguraCloseOperation() {
        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                TelaPrincipal.AparecerTela = true;
                dispose();
            }
        }
        );
    }

    private void EnterPraSalvar() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBConfirmarActionPerformed(arg0);
            }
        });
    }

    private void ConfiguraESC() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                TelaPrincipal.AparecerTela = true;
                dispose();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTFSaldoDinheiro = new javax.swing.JTextField();
        jLSaldoDinheiro = new javax.swing.JLabel();
        jBConfirmar = new javax.swing.JButton();
        jTFSaldoCartao = new javax.swing.JTextField();
        jLSaldoCartao = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("LIFES CREATIVE - Fechamento de Caixa");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jTFSaldoDinheiro.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jTFSaldoDinheiro.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFSaldoDinheiro.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTFSaldoDinheiro.setFocusable(false);

        jLSaldoDinheiro.setText(" Saldo em Dinheiro:");

        jBConfirmar.setText("Confirmar");
        jBConfirmar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarActionPerformed(evt);
            }
        });

        jTFSaldoCartao.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jTFSaldoCartao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFSaldoCartao.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLSaldoCartao.setText(" Saldo em Cartão:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(jBConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLSaldoCartao)
                            .addComponent(jLSaldoDinheiro)
                            .addComponent(jTFSaldoCartao, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFSaldoDinheiro, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLSaldoDinheiro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFSaldoDinheiro, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLSaldoCartao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFSaldoCartao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jBConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarActionPerformed

        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        long unixTime = System.currentTimeMillis() / 1000L;
        try {

            con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET fechamento_caixa = '" + timeStamp + "', aberto_caixa = 1 WHERE id_fc_caixa = " + TelaPrincipal.Id_CaixaAberto);
            rs = con.Select("SELECT saldo_caixa FROM fc_caixa USE INDEX (id_fc_caixa) WHERE id_fc_caixa = " + TelaPrincipal.Id_CaixaAberto + " LIMIT 1");
            rs.first();

            con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", 0, 0, " + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 2, 'Fechamento de Caixa', 0, '-" + rs.getString("saldo_caixa") + "', 0, 0, " + unixTime + ", 1, 0)");

            rs = con.Select("SELECT id_fc_forma_pgto, valor_movimento FROM fc_movimento WHERE descricao_movimento='Venda Produtos' AND valor_movimento>0 AND id_fc_forma_pgto <> 0 AND id_fc_caixa = " + TelaPrincipal.Id_CaixaAberto);
            int contador = 0;

            while (rs.next()) {
                for (int j = 0; j < listaCombo.size(); j++) {
                    if (Integer.parseInt(rs.getString("id_fc_forma_pgto")) == listaCombo.get(j).getId()) {
                        switch (listaCombo.get(j).getNome()) {
                            case "CRÉDITO":
                                credito += Float.parseFloat(rs.getString("valor_movimento"));
                                break;
                            case "DÉBITO":
                                debito += Float.parseFloat(rs.getString("valor_movimento"));
                                break;
                            case "BANRI":
                                banri += Float.parseFloat(rs.getString("valor_movimento"));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            /*
            if (credito != 0) {
                con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ",1 ," + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 2, 'Fechamento de Caixa', 0, '-" + credito + "', 0, 0, " + unixTime + ", 1, 0)");
            } else if (debito != 0) {
                con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ",2 ," + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 2, 'Fechamento de Caixa', 0, '-" + debito + "', 0, 0, " + unixTime + ", 1, 0)");
            } else if (banri != 0) {
                con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ",3 ," + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 2, 'Fechamento de Caixa', 0, '-" + banri + "', 0, 0, " + unixTime + ", 1, 0)");
            }
            */
            String valor = df.format(Double.parseDouble(jTFSaldoDinheiro.getText().replace(",", "."))) + "+" + df.format(Double.parseDouble(String.valueOf(credito).replace(",", "."))) + "+" + df.format(Double.parseDouble(String.valueOf(debito).replace(",", "."))) + "+" + df.format(Double.parseDouble(String.valueOf(banri).replace(",", ".")));
            for (int x = 0; x < TelaPrincipal.QtFechamento; x++) {
                i.ImprimeFuncao(TelaPrincipal.Id_eventoAtivo, TelaPrincipal.nome_eventoAtivo, "FECHAMENTO CAIXA", valor, TelaPrincipal.OperadorAtivo, String.valueOf(String.format("%03d", TelaPrincipal.pdvAtivo)), TelaPrincipal.ImprimeTitulo);
            }
            TelaPrincipal.FechouCaixa = true;
            TelaPrincipal.AparecerTela = true;
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Fechamento de Caixa", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(FechamentoCaixa.class.getName()).log(Level.SEVERE, null, ex);
        }

        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jBConfirmarActionPerformed

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        requestFocus();
        toFront();
    }//GEN-LAST:event_formWindowLostFocus
    private void listaLista() {

        try {
            //SELECT nome_pgto,id_fc_forma_pgto FROM fc_forma_pgto USE INDEX(tipo_pgto) WHERE sts_pgto=1 AND tipo_pgto='C' ORDER BY nome_pgto desc
            rs = con.Select("SELECT nome_pgto,id_fc_forma_pgto FROM fc_forma_pgto WHERE sts_pgto=1 AND tipo_pgto='C' ORDER BY nome_pgto desc");
            listaCombo.clear();
            int contador = 0;
            while (rs.next()) {
                Pagto p = new Pagto(rs.getString("nome_pgto"), Integer.parseInt(rs.getString("id_fc_forma_pgto")));
                listaCombo.add(p);
                contador++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FechamentoCompra.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBConfirmar;
    private javax.swing.JLabel jLSaldoCartao;
    private javax.swing.JLabel jLSaldoDinheiro;
    private javax.swing.JTextField jTFSaldoCartao;
    private javax.swing.JTextField jTFSaldoDinheiro;
    // End of variables declaration//GEN-END:variables
}
