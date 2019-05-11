
import Classes.Conexao;
import Classes.ConexaoServer;
import Classes.Imprimir;
import Classes.Produtos;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class TelaPrincipal extends javax.swing.JFrame {

    Conexao con = new Conexao();
    Imprimir i = new Imprimir();
    ResultSet rs, rs2, rs3, rs4;
    String nome_produtos = "";
    int id_fc_produtos = 0, contadorLista = 0;
    double valor_produtos = 0;
    boolean testeRuim = false;
    static boolean FechouCompra = false;
    DecimalFormat df = new DecimalFormat("0.00");
    static boolean AbriuCaixa = false;
    static boolean AparecerTela = false;
    static boolean FechouCaixa = false;
    static String HoraAberturaCaixa = "";
    static int Id_CaixaAberto = 0;
    static int Id_eventoAtivo = 0;
    static int pdvAtivo = 0;
    static int ImprimeTitulo = 0;
    static int Codabar = 0;
    static int QtAbertura = 0;
    static int QtFechamento = 0;
    static int QtSangria = 0;
    static int QtSuprimento = 0;
    static double saldo_atual_dinheiro = 0, saldo_atual_cartao = 0;
    static String OperadorAtivo = "";
    static int id_operadorAtivo = 0;
    static boolean SangrouSuprimiu = false;
    static String nome_eventoAtivo = "";
    static int tipoOperador = 0;
    static boolean tela = false;
    static boolean Configurou = false;
    static int ServidorRemoto = 1;
    int f3 = 0;
    int TestaClique = 0;
    int espaco = 0;
    ArrayList<Produtos> listaItens;
    int TamanhoFonte = 18;
    String cor;
    Color color;
    Boolean yes = true;
    static int AtualizaServer = 0;

    public TelaPrincipal(String nome_operador, int id_operadorAtivo, int tipo_operador) throws SQLException, IOException {
        OperadorAtivo = nome_operador;
        tipoOperador = tipo_operador;
        this.id_operadorAtivo = id_operadorAtivo;
        initComponents();
        Scroll();
        Iniciador();
    }

    private void Iniciador() throws SQLException, IOException {
        jLAtualizaServer.setEnabled(false);
        jLAtualizaServer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        rs = con.Select("SELECT banco_unico from fc_config where banco_unico=1 AND host2='' AND banco2='' AND login2='' AND senha2='' LIMIT 1");
        if (!rs.next()) {
            jLAtualizaServer.setEnabled(true);
            AtualizaServer = 1;
            jLAtualizaServer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        URL url = this.getClass().getResource("/imagens/icone.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(iconeTitulo);
        ConfiguraESC();
        ConfiguraF2();
        ConfiguraF3();
        ConfiguraF4();
        ConfiguraF5();
        ConfiguraF6();
        ConfiguraF7();
        ConfiguraTextButtons();
        ConfiguraEnter();
        jBESC.setEnabled(false);
        jBF2.setEnabled(false);
        jBF4.setEnabled(false);
        jBF10.setEnabled(false);
        jBF8.setEnabled(false);
        jTabelaProdutos.setShowGrid(false);
        AjeitaTabela();
        jSPPainelLista.setVisible(false);
        jTFCodigo.setEnabled(false);
        jTFNome.setEnabled(false);
        jTFQuant.setEnabled(false);
        jTFQtdeTotal.setText("");
        jTFItens.setText("");
        jTFValorTotal.setText("");
        jLLogo.setVisible(true);
        jItens.setVisible(false);
        Scroll.setVisible(false);
        listaItens = new ArrayList<Produtos>();
        rs = con.Select("SELECT fonte_tamanho, fonte_cor, fonte_espaco, pdv, abertura,fechamento, sangria,suprimento, imprime_codebar, logo_programa FROM fc_config LIMIT 1");
        rs.first();
        espaco = Integer.parseInt(rs.getString("fonte_espaco"));
        TamanhoFonte = Integer.parseInt(rs.getString("fonte_tamanho"));
        cor = rs.getString("fonte_cor");
        color = new Color(Integer.parseInt(cor));
        jItens.setForeground(color);
        jItens.setFont(new Font("Tahoma", Font.PLAIN, TamanhoFonte));
        AdicionarLista();
        pdvAtivo = rs.getInt("pdv");
        QtAbertura = rs.getInt("abertura");
        QtFechamento = rs.getInt("fechamento");
        QtSangria = rs.getInt("sangria");
        QtSuprimento = rs.getInt("suprimento");
        //ImprimeTitulo = rs.getInt("imprime_titulo");
        Codabar = rs.getInt("imprime_codebar");
        jLLogo.setIcon(new ImageIcon(rs.getString("logo_programa")));
        
        SetaPDV();
        jLOperador.setText(jLOperador.getText() + OperadorAtivo);

        TestaSeTemCaixaAberto();
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        System.gc();
        if (jBF7.getText().equals("<html><div align=center> Abertura de <br> Caixa (F4)</div></html>")) {
            jBF7.requestFocus();
        }
        if (pdvAtivo == 000) {
            jBF7.setEnabled(false);
            jBF9.setEnabled(false);
        } else {
            jBF7.setEnabled(true);
            jBF9.setEnabled(true);
        }
    }

    private void Scroll() {
        Scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton2();
            }
            private JButton createZeroButton() {
                JButton jbutton = new JButton();
                jbutton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                jbutton.setPreferredSize(new Dimension(50, 20));
                jbutton.setBackground(new Color(0, 0, 0, 0));
                jbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/flechaCima.png")));
                return jbutton;
            }
            private JButton createZeroButton2() {
                JButton jbutton = new JButton();
                jbutton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                jbutton.setPreferredSize(new Dimension(50, 20));
                jbutton.setBackground(new Color(0, 0, 0, 0));
                jbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/flechaBaixo.png")));
                return jbutton;
            }
        });
        Scroll.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton2();
            }

            private JButton createZeroButton() {
                JButton jbutton = new JButton();
                jbutton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                jbutton.setPreferredSize(new Dimension(50, 20));
                jbutton.setBackground(new Color(0, 0, 0, 0));
                jbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/flechaEsquerda.png")));
                return jbutton;
            }

            private JButton createZeroButton2() {
                JButton jbutton = new JButton();
                jbutton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                jbutton.setPreferredSize(new Dimension(50, 20));
                jbutton.setBackground(new Color(0, 0, 0, 0));
                jbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/flechaDireita.png")));
                return jbutton;
            }
        });
    }
    private void TestaSeTemCaixaAberto() throws SQLException, IOException {
        if (pdvAtivo > 0) {
            rs = con.Select("SELECT id_fc_caixa, id_fc_evento, c.id_fc_operador, nome_operador, abertura_caixa, fechamento_caixa, aberto_caixa FROM fc_caixa c USE INDEX (id_fc_caixa) INNER JOIN fc_operador o ON c.id_fc_operador = o.id_fc_operador ORDER BY id_fc_caixa DESC LIMIT 1");
            while (rs.next()) {
                if (rs.getInt("aberto_caixa") == 0) {
                    if (rs.getInt("id_fc_operador") == id_operadorAtivo) {
                        Id_CaixaAberto = rs.getInt("id_fc_caixa");
                        Id_eventoAtivo = rs.getInt("id_fc_evento");
                        HoraAberturaCaixa = rs.getString("abertura_caixa");
                        jTFCodigo.setEnabled(true);
                        SetaSaldoDinheiro();
                        SetaSaldoCartao();
                        jTFNome.setEnabled(true);
                        jTFQuant.setEnabled(true);
                        jTFQtdeTotal.setText("0");
                        jTFItens.setText("0");
                        jTFValorTotal.setText("0,00");
                        jBF7.setText("<html><div align=center> Fechamento de <br> Caixa (F4)</div></html>");
                        jLCaixaFechado.setVisible(false);
                        jBESC.setEnabled(true);
                        jBF2.setEnabled(true);
                        jBF4.setEnabled(true);
                        jBF8.setEnabled(true);
                        jBF10.setEnabled(true);
                        SetaNomeLogoEvento();
                    } else {
                        int resposta = JOptionPane.showConfirmDialog(null, "Há um caixa aberto do operador " + rs.getString("nome_operador") + ".\nDeseja encerrá-lo?", "LIFES CREATIVE - Frente de Caixa", JOptionPane.YES_NO_OPTION);
                        if (resposta == JOptionPane.YES_OPTION) {
                            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                            long unixTime = System.currentTimeMillis() / 1000L;
                            try {
                                con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET fechamento_caixa='" + timeStamp + "', aberto_caixa = 1 WHERE id_fc_caixa = " + rs.getString("id_fc_caixa"));

                                con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + rs.getString("id_fc_caixa") + ", " + rs.getString("id_fc_evento") + ", " + TelaPrincipal.id_operadorAtivo + ", 0, " + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 2, 'Fechamento de Caixa', 0, '-" + rs.getString("saldo_caixa") + "', 0, 0, " + unixTime + ", 1, 0)");

                                for (int x = 0; x < TelaPrincipal.QtFechamento; x++) 
                                {
                                
                                //    i.ImprimeFuncao(TelaPrincipal.Id_eventoAtivo, TelaPrincipal.nome_eventoAtivo, "Fechamento Caixa", df.format(Double.parseDouble(rs.getString("saldo_caixa"))), rs.getString("nome_operador"), String.valueOf(TelaPrincipal.pdvAtivo), TelaPrincipal.ImprimeTitulo);
                                
                                }
                                TelaPrincipal.FechouCaixa = true;
                                TelaPrincipal.AparecerTela = true;
                                dispose();
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Fechamento de Caixa", JOptionPane.ERROR_MESSAGE);
                                Logger.getLogger(FechamentoCaixa.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            System.exit(0);
                        }
                    }
                }
            }
        }
    }

    private void AtualizarCorTamanho() {
        try {
            rs = con.Select("SELECT fonte_tamanho,fonte_cor,fonte_espaco FROM fc_config LIMIT 1");
            rs.first();
            TamanhoFonte = Integer.parseInt(rs.getString("fonte_tamanho"));
            cor = rs.getString("fonte_cor");
            espaco = Integer.parseInt(rs.getString("fonte_espaco"));
            color = new Color(Integer.parseInt(cor));
            jItens.setForeground(color);
            jItens.setFont(new Font("Tahoma", Font.PLAIN, TamanhoFonte));
            AdicionarLista();
        } catch (SQLException ex) {
            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private void SetaNomeLogoEvento() throws SQLException {
        rs = con.Select("SELECT nome_evento FROM fc_evento USE INDEX (id_fc_evento) WHERE id_fc_evento = " + Id_eventoAtivo + " LIMIT 1");
        rs.first();
        nome_eventoAtivo = rs.getString("nome_evento");
    }
    private void SetaPDV() throws SQLException {
        jLPDV.setText("PDV:  " + String.format("%03d", pdvAtivo));
    }
    private void ConfiguraTextButtons() {
        jBESC.setText("<html><div align=center> Fechamento da <br> Venda (ESC)</div></html>");
        jBF2.setText("<html><div align=center> Cancelamento da <br> Venda (F2)</div></html>");
        jBF4.setText("<html><div align=center> Reimpressão de <br> Venda (F3)</div></html>");
        jBF7.setText("<html><div align=center> Abertura de <br> Caixa (F4)</div></html>");
        jBF8.setText("<html><div align=center> Entrada/Saída <br> de Dinheiro (F5)</div></html>");
        jBF9.setText("<html><div align=center> Emissão de <br> Relatórios (F6)</div></html>");
        jBF10.setText("<html><div align=center> Emitir Cupom <br> Fiscal (F7)</div></html>");
    }
    private void SetaSaldoDinheiro() throws SQLException {
        rs = con.Select("SELECT SUM(valor_movimento) as saldo FROM fc_movimento USE INDEX (id_fc_caixa) WHERE id_fc_caixa = " + Id_CaixaAberto + " AND sts_movimento = 1 AND tipo_movimento <> 11 AND id_fc_forma_pgto = 1 LIMIT 1");
        rs.first();
        jLSaldo.setText("Dinheiro: " + df.format(rs.getDouble("saldo")).replace(".", ","));
        saldo_atual_dinheiro = rs.getDouble("saldo");
    }
    private void SetaSaldoCartao() throws SQLException {
        rs = con.Select("SELECT SUM(valor_movimento) as saldo FROM fc_movimento USE INDEX (id_fc_caixa) WHERE id_fc_caixa = " + Id_CaixaAberto + " AND sts_movimento = 1 AND tipo_movimento <> 11 AND id_fc_forma_pgto > 1 LIMIT 1");
        rs.first();
        jLSaldoCartao.setText("Cartão: " + df.format(rs.getDouble("saldo")).replace(".", ","));
        saldo_atual_cartao = rs.getDouble("saldo");
    }

    /*private void EnterPraNome(){
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //jTFNome.requestFocus();
                jTFCodigo.requestFocus();
            }new TelaRelatorio().setVisible(true);
        });
    }*/
 /*
    public static String ConfPDV(int num){
        
//        return "00"+num;
        // return num.length();

        System.out.println(num.length());        
    }    
     */
    private void ConfiguraF6() {
        InputMap inputMap9 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap9.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "forward9");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap9);
        this.getRootPane().getActionMap().put("forward9", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF9ActionPerformed(arg0);
            }
        });
    }
    private void ConfiguraEnter() {
        InputMap inputMap9 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap9.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward14");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap9);
        this.getRootPane().getActionMap().put("forward14", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (jBF7.getText().equals("<html><div align=center> Abertura de <br> Caixa (F4)</div></html>")) {
                    jBF7ActionPerformed(arg0);
                } else {
                    jTFQuant.requestFocus();
                }
            }
        });
    }
    private void EnterPraCadastrar() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    int quantidade = Integer.parseInt(jTFQuant.getText());
                    if (quantidade > 0) {
                        if (!jTFCodigo.getText().equals("") || (!jTFNome.getText().equals(""))) {
                            try {
                                rs = con.Select("SELECT valor_produtos FROM fc_produtos USE INDEX (id_fc_produtos) WHERE id_fc_produtos = " + jTFCodigo.getText() + " AND sts_produtos = 1 LIMIT 1");
                                rs.first();
                                valor_produtos = rs.getDouble("valor_produtos");
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            double preco = valor_produtos * Integer.parseInt(jTFQuant.getText());
                            if (!jTFNome.getText().equals("") && !jTFQuant.getText().equals("")) {
                                DefaultTableModel val = (DefaultTableModel) jTabelaProdutos.getModel();
                                val.addRow(new String[]{jTFCodigo.getText(), jTFNome.getText(), jTFQuant.getText(), (df.format(preco).replace(".", ","))});
                                Atualiza3jTF(quantidade, preco);
                                nome_produtos = "";
                                id_fc_produtos = 0;
                                valor_produtos = 0;
                                contadorLista = 0;
                                testeRuim = false;
                            }
                            jTFNome.setText("");
                            jTFCodigo.setText("");
                            jTFQuant.setText("");
                            jTFCodigo.requestFocus();
                        } else {
                            JOptionPane.showMessageDialog(null, "Por favor, informe valores válidos", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                            jTFCodigo.setText("");
                            jTFNome.setText("");
                            jTFCodigo.requestFocus();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Por favor, digite uma quantidade válida", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                        jTFQuant.setText("");
                        jTFQuant.requestFocus();
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Os valores informados são inválidos", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                    jTFQuant.setText("");
                    jTFQuant.requestFocus();
                }
            }
        });
        System.gc();
    }

    private void EnterFazNada() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {

            }
        });
        System.gc();
    }

    private void ConfiguraSeta() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jListaProdutos.requestFocus();
                jListaProdutos.setSelectedIndex(contadorLista);
                EnterFazjList1Evento();
            }
        });
    }

    private void EnterPraQuantMod() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    rs = con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE sts_produtos = 1 AND nome_produtos = '" + jTFNome.getText() + "' LIMIT 1");
                    while (rs.next()) {
                        jTFNome.setText(nome_produtos);
                        jTFCodigo.setText(rs.getString("id_fc_produtos"));
                        jSPPainelLista.setVisible(false);
                        jTFQuant.requestFocus();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        System.gc();
    }

    private void AjeitaTabela() {
        TableColumn col = jTabelaProdutos.getColumnModel().getColumn(0);
        col.setPreferredWidth(15);
        col = jTabelaProdutos.getColumnModel().getColumn(1);
        col.setPreferredWidth(250);
        col = jTabelaProdutos.getColumnModel().getColumn(2);
        col.setPreferredWidth(30);
        col = jTabelaProdutos.getColumnModel().getColumn(3);
        col.setPreferredWidth(50);
    }

    private void ZeraTudo() {
        DefaultTableModel val = (DefaultTableModel) jTabelaProdutos.getModel();
        nome_produtos = "";
        id_fc_produtos = 0;
        valor_produtos = 0;
        contadorLista = 0;
        testeRuim = false;
        jTFCodigo.setText("");
        jTFNome.setText("");
        jTFItens.setText("0");
        jTFQtdeTotal.setText("0");
        jTFQuant.setText("");
        jTFValorTotal.setText("0,00");
        for (int x = val.getRowCount(); x > 0; x--) {
            val.removeRow(x - 1);
        }
        ConfiguraEnter();
    }

    public static void copyFile(File source, File destination) throws IOException {
        if (destination.exists()) {
            destination.delete();
        }
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destinationChannel = new FileOutputStream(destination).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(),
                    destinationChannel);
        } finally {
            if (sourceChannel != null && sourceChannel.isOpen()) {
                sourceChannel.close();
            }
            if (destinationChannel != null && destinationChannel.isOpen()) {
                destinationChannel.close();
            }
        }
    }

    private void EnterFazjList1Evento() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFNome.setText(jListaProdutos.getSelectedValue());
                jSPPainelLista.setVisible(false);
                try {
                    con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE nome_produtos = '" + jTFNome.getText() + "' AND sts_produtos = 1 LIMIT 1");
                    while (rs.next()) {
                        id_fc_produtos = rs.getInt("id_fc_produtos");
                    }
                    jTFCodigo.setText(Integer.toString(id_fc_produtos));
                } catch (SQLException ex) {
                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
                jTFQuant.requestFocus();
            }
        });
        System.gc();
    }

    private void Atualiza3jTF(int quantidad, double prec) {
        int quantItensExistente = Integer.parseInt(jTFItens.getText());
        quantItensExistente++;
        jTFItens.setText(Integer.toString(quantItensExistente));
        int quantTotalExistente = Integer.parseInt(jTFQtdeTotal.getText());
        quantTotalExistente = quantTotalExistente + quantidad;
        jTFQtdeTotal.setText(Integer.toString(quantTotalExistente));
        double precoExistente = Double.parseDouble(jTFValorTotal.getText().replaceAll(",", "."));
        precoExistente = precoExistente + prec;
        jTFValorTotal.setText((df.format(precoExistente)).replace(".", ","));
    }

    private void ConfiguraESC() {
        InputMap inputMap3 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap3.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "forward3");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap3);
        this.getRootPane().getActionMap().put("forward3", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBESCActionPerformed(arg0);
            }
        });
    }

    private void DesaparecerTela() {
        jPanel1.setVisible(false);
        jPanel2.setVisible(false);
        jBESC.setVisible(false);
        jLLogo.setVisible(false);
        jItens.setVisible(false);
        jBF2.setVisible(false);
        jBF4.setVisible(false);
        jBF7.setVisible(false);
        jBF8.setVisible(false);
        jBF9.setVisible(false);
        jBF10.setVisible(false);
        yes = false;
        tela = true;
        System.gc();
    }

    private void ConfiguraF7() {
        InputMap inputMap4 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap4.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "forward12");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap4);
        this.getRootPane().getActionMap().put("forward12", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF10ActionPerformed(arg0);
            }
        });
    }

    private void ConfiguraF4() {
        InputMap inputMap4 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap4.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "forward4");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap4);
        this.getRootPane().getActionMap().put("forward4", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF7ActionPerformed(arg0);
            }
        });
    }

    private void ConfiguraF2() {
        InputMap inputMap5 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap5.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "forward5");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap5);
        this.getRootPane().getActionMap().put("forward5", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF2ActionPerformed(arg0);
            }
        });
    }

    private void ConfiguraF5() {
        InputMap inputMap6 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap6.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "forward6");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap6);
        this.getRootPane().getActionMap().put("forward6", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF8ActionPerformed(arg0);
            }
        });
    }

    private void ConfiguraF3() {
        InputMap inputMap7 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap7.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "forward7");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap7);
        this.getRootPane().getActionMap().put("forward7", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF4ActionPerformed(arg0);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLCaixaFechado = new javax.swing.JLabel();
        jSPPainelLista = new javax.swing.JScrollPane();
        jListaProdutos = new javax.swing.JList<>();
        jLCodigo = new javax.swing.JLabel();
        jTFCodigo = new javax.swing.JTextField();
        jLQuant = new javax.swing.JLabel();
        jTFQuant = new javax.swing.JTextField();
        jLNome = new javax.swing.JLabel();
        jTFNome = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabelaProdutos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jTFItens = new javax.swing.JTextField();
        jLItens = new javax.swing.JLabel();
        jTFQtdeTotal = new javax.swing.JTextField();
        jTFValorTotal = new javax.swing.JTextField();
        jLQtdeTotal = new javax.swing.JLabel();
        jLValorTotal = new javax.swing.JLabel();
        jPainelLogoItens = new javax.swing.JPanel();
        jLLogo = new javax.swing.JLabel();
        Scroll = new javax.swing.JScrollPane();
        jItens = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLOperador = new javax.swing.JLabel();
        jLPDV = new javax.swing.JLabel();
        jLSaldoCartao = new javax.swing.JLabel();
        jLAtualizaServer = new javax.swing.JLabel();
        jLEngrenagem = new javax.swing.JLabel();
        jLSaldo = new javax.swing.JLabel();
        jLCancelar = new javax.swing.JLabel();
        jBF7 = new javax.swing.JButton();
        jBESC = new javax.swing.JButton();
        jBF2 = new javax.swing.JButton();
        jBF8 = new javax.swing.JButton();
        jBF4 = new javax.swing.JButton();
        jBF9 = new javax.swing.JButton();
        jBF10 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LIFES CREATIVE - Frente de Caixa");
        setMinimumSize(new java.awt.Dimension(1021, 559));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLCaixaFechado.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLCaixaFechado.setForeground(new java.awt.Color(255, 0, 0));
        jLCaixaFechado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLCaixaFechado.setText("Caixa Fechado");
        jPanel1.add(jLCaixaFechado, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 480, 260));

        jListaProdutos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListaProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListaProdutosMouseReleased(evt);
            }
        });
        jSPPainelLista.setViewportView(jListaProdutos);

        jPanel1.add(jSPPainelLista, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 480, 80));

        jLCodigo.setText(" Código Produto");
        jPanel1.add(jLCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jTFCodigo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodigoFocusGained(evt);
            }
        });
        jTFCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFCodigoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTFCodigoKeyTyped(evt);
            }
        });
        jPanel1.add(jTFCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 230, 35));

        jLQuant.setText(" Quantidade");
        jPanel1.add(jLQuant, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, -1, -1));

        jTFQuant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFQuantFocusGained(evt);
            }
        });
        jTFQuant.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFQuantKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTFQuantKeyTyped(evt);
            }
        });
        jPanel1.add(jTFQuant, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 40, 230, 35));

        jLNome.setText(" Nome Produto");
        jPanel1.add(jLNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        jTFNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFNomeKeyReleased(evt);
            }
        });
        jPanel1.add(jTFNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 480, 35));

        jTabelaProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cód.", "Nome", "Quant.", "Preço"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTabelaProdutos.setRowHeight(25);
        jTabelaProdutos.getTableHeader().setResizingAllowed(false);
        jTabelaProdutos.getTableHeader().setReorderingAllowed(false);
        jTabelaProdutos.setVerifyInputWhenFocusTarget(false);
        jTabelaProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTabelaProdutosMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTabelaProdutos);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 480, 260));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setMaximumSize(new java.awt.Dimension(417, 94));

        jTFItens.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFItens.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFItens.setText("0");
        jTFItens.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTFItens.setFocusable(false);
        jTFItens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFItensActionPerformed(evt);
            }
        });

        jLItens.setText(" Quant. Itens");

        jTFQtdeTotal.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFQtdeTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFQtdeTotal.setText("0");
        jTFQtdeTotal.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTFQtdeTotal.setFocusable(false);
        jTFQtdeTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFQtdeTotalActionPerformed(evt);
            }
        });

        jTFValorTotal.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jTFValorTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFValorTotal.setText("0,00");
        jTFValorTotal.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTFValorTotal.setFocusable(false);

        jLQtdeTotal.setText(" Quant. Ticket");

        jLValorTotal.setText(" Valor TOTAL");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFItens, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLItens))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFQtdeTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLQtdeTotal))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLValorTotal))
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLItens)
                    .addComponent(jLQtdeTotal)
                    .addComponent(jLValorTotal))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFItens, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFQtdeTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        jPainelLogoItens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPainelLogoItensMouseClicked(evt);
            }
        });
        jPainelLogoItens.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLLogo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPainelLogoItens.add(jLLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -2, 490, 330));

        Scroll.setBorder(null);
        Scroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ScrollMouseClicked(evt);
            }
        });

        jItens.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jItens.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jItens.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jItens.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Scroll.setViewportView(jItens);

        jPainelLogoItens.add(Scroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 480, 330));

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));

        jLOperador.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLOperador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/operador.png"))); // NOI18N
        jLOperador.setText(" Operador: ");

        jLPDV.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLPDV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/pdv.png"))); // NOI18N
        jLPDV.setText(" PDV: ");
        jLPDV.setToolTipText("Ponto de Venda ativo");

        jLSaldoCartao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLSaldoCartao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cartao.png"))); // NOI18N
        jLSaldoCartao.setText("Cartão:");

        jLAtualizaServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save.png"))); // NOI18N
        jLAtualizaServer.setToolTipText("Atualizar Servidor");
        jLAtualizaServer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLAtualizaServer.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLAtualizaServer.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLAtualizaServerMouseMoved(evt);
            }
        });
        jLAtualizaServer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLAtualizaServerMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLAtualizaServerMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLAtualizaServerMouseReleased(evt);
            }
        });

        jLEngrenagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/engrenagem.png"))); // NOI18N
        jLEngrenagem.setToolTipText("Configurações do sistema");
        jLEngrenagem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLEngrenagem.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLEngrenagemMouseMoved(evt);
            }
        });
        jLEngrenagem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLEngrenagemMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLEngrenagemMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLEngrenagemMouseReleased(evt);
            }
        });

        jLSaldo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLSaldo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/dolar.png"))); // NOI18N
        jLSaldo.setText("Dinheiro:");

        jLCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cancel.png"))); // NOI18N
        jLCancelar.setToolTipText("Cancelamento de Ticket");
        jLCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLCancelar.setMaximumSize(new java.awt.Dimension(30, 30));
        jLCancelar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLCancelarMouseMoved(evt);
            }
        });
        jLCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLCancelarMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLEngrenagem)
                .addGap(12, 12, 12)
                .addComponent(jLCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLAtualizaServer)
                .addGap(28, 28, 28)
                .addComponent(jLOperador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLPDV, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLSaldoCartao, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLPDV, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLSaldoCartao, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLOperador, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLAtualizaServer)
                            .addComponent(jLEngrenagem))
                        .addGap(9, 9, 9)))
                .addContainerGap())
        );

        jBF7.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt4.png"))); // NOI18N
        jBF7.setText("Abertura");
        jBF7.setToolTipText("Abertura");
        jBF7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBF7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF7ActionPerformed(evt);
            }
        });

        jBESC.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBESC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt1.png"))); // NOI18N
        jBESC.setText("Fechamento");
        jBESC.setToolTipText("Fechamento");
        jBESC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBESC.setFocusPainted(false);
        jBESC.setFocusable(false);
        jBESC.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBESC.setPreferredSize(new java.awt.Dimension(130, 40));
        jBESC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBESCActionPerformed(evt);
            }
        });

        jBF2.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt2.png"))); // NOI18N
        jBF2.setText("Cancelamento");
        jBF2.setToolTipText("Cancelamento");
        jBF2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBF2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF2ActionPerformed(evt);
            }
        });

        jBF8.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/dinheiro.png"))); // NOI18N
        jBF8.setText("Dinheiro");
        jBF8.setToolTipText("Dinheiro");
        jBF8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBF8.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF8ActionPerformed(evt);
            }
        });

        jBF4.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt3.png"))); // NOI18N
        jBF4.setText("Reimprimir");
        jBF4.setToolTipText("Reimprimir");
        jBF4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBF4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF4ActionPerformed(evt);
            }
        });

        jBF9.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt6.png"))); // NOI18N
        jBF9.setText("Relatórios");
        jBF9.setToolTipText("Relatório");
        jBF9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBF9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF9ActionPerformed(evt);
            }
        });

        jBF10.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cupom.png"))); // NOI18N
        jBF10.setText("Cupom");
        jBF10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBF10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBESC, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jBF2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jBF4, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jBF7, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jBF8, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jBF9, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jBF10, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPainelLogoItens, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPainelLogoItens, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jBF2, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jBESC, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jBF4, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jBF7, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jBF8, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jBF9, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jBF10, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodigoKeyReleased
        ConfiguraEnter();
    }//GEN-LAST:event_jTFCodigoKeyReleased

    private void jTFNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNomeKeyReleased
        // TODO add your handling code here:
        if (!jTFNome.getText().equals("")) {
            EnterPraQuantMod();
            jSPPainelLista.setVisible(true);
            try {
                rs = con.Select("SELECT id_fc_produtos, nome_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE sts_produtos = 1");
                DefaultListModel model = new DefaultListModel();
                boolean TestaSeTem = false;
                while (rs.next()) {
                    nome_produtos = rs.getString("nome_produtos");
                    if (nome_produtos.contains(jTFNome.getText().toUpperCase())) {
                        this.jListaProdutos.setModel(model);
                        model.addElement(nome_produtos);
                        TestaSeTem = true;
                    }
                }
                if (TestaSeTem == false) {
                    this.jListaProdutos.setModel(model);
                    jSPPainelLista.setVisible(false);
                }
            } catch (SQLException ex) {
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            ConfiguraSeta();
        } else {
            EnterFazNada();
            jSPPainelLista.setVisible(false);
        }
    }//GEN-LAST:event_jTFNomeKeyReleased

    private void jTFQuantKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFQuantKeyReleased
        // TODO add your handling code here:
        if ((!jTFCodigo.getText().equals("") || !jTFNome.getText().equals("")) && !jTFQuant.getText().equals("")) {
            EnterPraCadastrar();
        } else {
            EnterFazNada();
        }
    }//GEN-LAST:event_jTFQuantKeyReleased

    private void jTFQuantFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFQuantFocusGained
        // TODO add your handling code here:
        jSPPainelLista.setVisible(false);
        try {
            rs = con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (id_fc_produtos) WHERE id_fc_produtos = " + Integer.parseInt(jTFCodigo.getText()) + " AND sts_produtos = 1 LIMIT 1");
            if (rs.next()) {
                try {
                    jTFQuant.requestFocus();
                    rs = con.Select("SELECT nome_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE id_fc_produtos = " + jTFCodigo.getText() + " AND sts_produtos = 1");
                    while (rs.next()) {
                        nome_produtos = rs.getString("nome_produtos");
                    }
                    jTFNome.setText(nome_produtos);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Por favor, digite um código válido", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                    jTFCodigo.setText("");
                    jTFCodigo.requestFocus();
                    testeRuim = true;
                }
            } else if (!jTFNome.getText().equals("")) {
                try {
                    rs = con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE nome_produtos = '" + jTFNome.getText() + "' AND sts_produtos=1");
                    while (rs.next()) {
                        id_fc_produtos = rs.getInt("id_fc_produtos");
                    }
                    jTFCodigo.setText(Integer.toString(id_fc_produtos));
                } catch (SQLException ex) {
                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (testeRuim == false) {
                    JOptionPane.showMessageDialog(null, "Produto INEXISTENTE ou INATIVO", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                    jTFCodigo.requestFocus();
                    jTFCodigo.setText("");
                    testeRuim = true;
                }
            }
        } catch (SQLException e) {

        }
    }//GEN-LAST:event_jTFQuantFocusGained

    private void jTabelaProdutosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabelaProdutosMouseReleased
        // TODO add your handling code here:
        DefaultTableModel val = (DefaultTableModel) jTabelaProdutos.getModel();
        switch (jTabelaProdutos.getSelectedRow()) {
            case -1:
                break;
            default:
                //int resposta = JOptionPane.showConfirmDialog(null, "Deseja excluir o produto ?", "LIFES CREATIVE - Frente de Caixa", JOptionPane.YES_NO_OPTION);
                //if (resposta==JOptionPane.YES_OPTION){
                int valorLinha = 0;
                int qtdeLinha = 0;
                int linha = jTabelaProdutos.getSelectedRow();
                valorLinha = Integer.parseInt(((String) val.getValueAt(linha, 3)).replaceAll(",00", ""));
                qtdeLinha = Integer.parseInt((String) val.getValueAt(linha, 2));
                int quantTotalExistente = Integer.parseInt(jTFQtdeTotal.getText());
                quantTotalExistente = quantTotalExistente - qtdeLinha;
                jTFQtdeTotal.setText(Integer.toString(quantTotalExistente));
                int precoExistente = Integer.parseInt(jTFValorTotal.getText().replaceAll(",00", ""));
                precoExistente = precoExistente - valorLinha;
                jTFValorTotal.setText(Integer.toString(precoExistente) + ",00");
                int quantItensExistente = Integer.parseInt(jTFItens.getText());
                quantItensExistente--;
                jTFItens.setText(Integer.toString(quantItensExistente));
                val.removeRow(jTabelaProdutos.getSelectedRow());
                jTFCodigo.requestFocus();
                //} else if (resposta==JOptionPane.NO_OPTION){
                //jTFCodigo.requestFocus();
                //}
                break;
        }
    }//GEN-LAST:event_jTabelaProdutosMouseReleased

    private void jListaProdutosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListaProdutosMouseReleased
        // TODO add your handling code here:
        jTFNome.setText(jListaProdutos.getSelectedValue());
        jSPPainelLista.setVisible(false);
        try {
            rs = con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE nome_produtos = '" + jTFNome.getText() + "' AND sts_produtos = 1");
            while (rs.next()) {
                id_fc_produtos = rs.getInt("id_fc_produtos");
            }
            jTFCodigo.setText(Integer.toString(id_fc_produtos));
        } catch (SQLException ex) {
            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        jTFQuant.requestFocus();
    }//GEN-LAST:event_jListaProdutosMouseReleased

    private void jTFCodigoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodigoFocusGained
        // TODO add your handling code here:
        testeRuim = false;
    }//GEN-LAST:event_jTFCodigoFocusGained

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        // TODO add your handling code here:
        if (AparecerTela == true) {
            if (pdvAtivo == 000) {
                jBF7.setEnabled(false);
                jBF9.setEnabled(false);
            } else {
                jBF7.setEnabled(true);
                jBF9.setEnabled(true);
            }
            yes = true;
            jPanel1.setVisible(true);
            jPanel2.setVisible(true);
            jBESC.setVisible(true);
            if (AtualizaServer == 0) {
                jLAtualizaServer.setEnabled(false);
                jLAtualizaServer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else {
                jLAtualizaServer.setEnabled(true);
                jLAtualizaServer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            switch (TestaClique) {
                case 0:
                    jLLogo.setVisible(true);
                    jItens.setVisible(false);
                    Scroll.setVisible(false);
                    break;
                case 1:
                    jLLogo.setVisible(false);
                    jItens.setVisible(true);
                    Scroll.setVisible(true);
                    break;
                default:
                    break;
            }
            jBF2.setVisible(true);
            jBF4.setVisible(true);
            jBF7.setVisible(true);
            jBF8.setVisible(true);
            jBF9.setVisible(true);
            jBF10.setVisible(true);
            AtualizarCorTamanho();
            //jLAtualizaServer.setVisible(true);
            //jLEngrenagem.setEnabled(true);
            tela = false;
            jTFCodigo.requestFocus();
            if (jTFCodigo.isEnabled()) {
                jLCaixaFechado.setVisible(false);
            } else {
                jLCaixaFechado.setVisible(true);
            }
            try {
                SetaPDV();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            AparecerTela = false;
        }
        if (FechouCompra == true) {
            try {
                SetaSaldoDinheiro();
                SetaSaldoCartao();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            ZeraTudo();
            FechouCompra = false;
            jTFCodigo.requestFocus();
            jSPPainelLista.setVisible(false);
            jLCaixaFechado.setVisible(false);
        }
        if (AbriuCaixa == true && pdvAtivo > 0) {
            jTFCodigo.setEnabled(true);
            jTFNome.setEnabled(true);
            jTFQuant.setEnabled(true);
            jTFQtdeTotal.setText("0");
            jTFItens.setText("0");
            jTFValorTotal.setText("0,00");
            jLCaixaFechado.setVisible(false);
            jBESC.setEnabled(true);
            jBF2.setEnabled(true);
            jBF4.setEnabled(true);
            jBF8.setEnabled(true);
            jBF10.setEnabled(true);
            jBF7.setText("<html><div align=center> Fechamento de <br> Caixa (F4)</div></html>");
            try {
                SetaSaldoDinheiro();
                SetaSaldoCartao();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            AbriuCaixa = false;
        }
        if (FechouCaixa == true) {
            jBF7.setText("<html><div align=center> Abertura de <br> Caixa (F4)</div></html>");
            jTabelaProdutos.setShowGrid(false);
            jSPPainelLista.setVisible(false);
            jTFCodigo.setEnabled(false);
            jTFNome.setEnabled(false);
            jTFQuant.setEnabled(false);
            jBESC.setEnabled(false);
            jBF2.setEnabled(false);
            jBF4.setEnabled(false);
            jBF8.setEnabled(false);
            jBF10.setEnabled(false);
            jTFQtdeTotal.setText("");
            jTFItens.setText("");
            jTFValorTotal.setText("");
            jLCaixaFechado.setVisible(true);
            jLSaldo.setText("Dinheiro: ");
            jLSaldoCartao.setText("Cartão: ");
            jTFCodigo.setText("");
            jTFNome.setText("");
            jTFQuant.setText("");
            FechouCaixa = false;
        }
        if (SangrouSuprimiu == true) {
            try {
                SetaSaldoDinheiro();
                SetaSaldoCartao();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            jLCaixaFechado.setVisible(false);
            SangrouSuprimiu = false;
        }
        if (Configurou == true) {
            JOptionPane.showMessageDialog(null, "Dados modificados com sucesso. O programa será reiniciado.", "LIFES CREATIVE - Frente de Caixa", JOptionPane.INFORMATION_MESSAGE);
            String comando = "java -jar " + new File("").getAbsolutePath() + "\\FrenteDeCaixa.jar";
            try {
                Process Processo = Runtime.getRuntime().exec(comando);
            } catch (IOException MensagemdeErro) {
                JOptionPane.showMessageDialog(null, "Erro ao reiniciar o programa", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
            Configurou = false;
        }
    }//GEN-LAST:event_formWindowGainedFocus

    private void jBESCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBESCActionPerformed
        // TODO add your handling code here:
        if (jTabelaProdutos.getModel().getRowCount() > 0) {
            new FechamentoCompra(Double.parseDouble(jTFValorTotal.getText().replace(",", ".")), Integer.parseInt(jTFQtdeTotal.getText()), jTabelaProdutos).setVisible(true);
            DesaparecerTela();
        }
    }//GEN-LAST:event_jBESCActionPerformed

    private void jBF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBF2ActionPerformed
        if (jBF7.getText().equals("<html><div align=center> Fechamento de <br> Caixa (F4)</div></html>")) {
//            int resposta = JOptionPane.showConfirmDialog(null, "Deseja cancelar a venda?", "LIFES CREATIVE - Frente de Caixa", JOptionPane.YES_NO_OPTION);
//            if (resposta==JOptionPane.YES_OPTION){
            jPanel1.setVisible(true);
            jPanel2.setVisible(true);
            jBESC.setVisible(true);
            switch (TestaClique) {
                case 0:
                    jLLogo.setVisible(true);
                    break;
                case 1:
                    jItens.setVisible(true);
                    Scroll.setVisible(true);
                    break;
                default:
                    TestaClique = 0;
                    break;
            }
            jBF2.setVisible(true);
            jBF4.setVisible(true);
            jBF7.setVisible(true);
            jBF8.setVisible(true);
            jBF9.setVisible(true);
            jBF10.setVisible(true);
            jTFCodigo.requestFocus();
            jTFQtdeTotal.setText("0");
            jTFItens.setText("0");
            jTFValorTotal.setText("0,00");
            DefaultTableModel val = (DefaultTableModel) jTabelaProdutos.getModel();
            for (int x = jTabelaProdutos.getRowCount() - 1; x >= 0; x--) {
                val.removeRow(x);
            }
            jTFCodigo.setText("");
            jTFNome.setText("");
            jTFQuant.setText("");
            // }
        }
    }//GEN-LAST:event_jBF2ActionPerformed

    private void jBF4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBF4ActionPerformed
        // TODO add your handling code here:
        if (jBF7.getText().equals("<html><div align=center> Fechamento de <br> Caixa (F4)</div></html>")) {
            if (tipoOperador >= 2) {
                new TelaReimpressao().setVisible(true);
                DesaparecerTela();
            } else {
                new TelaSupervisor("TelaReimpressao").setVisible(true);
                DesaparecerTela();
            }
        }
    }//GEN-LAST:event_jBF4ActionPerformed

    private void jBF9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBF9ActionPerformed
        // TODO add your handling code here:
        new TelaRelatorio().setVisible(true);
        DesaparecerTela();
    }//GEN-LAST:event_jBF9ActionPerformed

    private void jBF7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBF7ActionPerformed
        if (jBF7.getText().equals("<html><div align=center> Abertura de <br> Caixa (F4)</div></html>")) {
            try {
                new AberturaCaixa().setVisible(true);
                DesaparecerTela();
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                System.out.println(s);
            }
        } else {
            /*try {
                BufferedImage img = new BufferedImage(TelaPrincipal.this.getWidth(), TelaPrincipal.this.getHeight(), BufferedImage.TYPE_INT_RGB);
                TelaPrincipal.this.paint(img.getGraphics());
                File outputfile = new File("src/imagens/background.png");
                ImageIO.write(img, "png", outputfile);
            } catch (IOException ex) {
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            jTFQtdeTotal.setText("0");
            jTFItens.setText("0");
            jTFValorTotal.setText("0,00");
            DefaultTableModel val = (DefaultTableModel) jTabelaProdutos.getModel();
            for (int x = jTabelaProdutos.getRowCount() - 1; x >= 0; x--) {
                val.removeRow(x);
            }
            jTFCodigo.setText("");
            jTFNome.setText("");
            jTFQuant.setText("");

            String[] auxiliar = jLSaldo.getText().split(": ");
            new FechamentoCaixa(Double.parseDouble(String.valueOf(saldo_atual_dinheiro).replace(",", ".")), Double.parseDouble(String.valueOf(saldo_atual_cartao).replace(",", "."))).setVisible(true);
            DesaparecerTela();
        }
    }//GEN-LAST:event_jBF7ActionPerformed

    private void jBF8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBF8ActionPerformed
        // TODO add your handling code here:
        if (jBF7.getText().equals("<html><div align=center> Fechamento de <br> Caixa (F4)</div></html>")) {
            new EntradaSaidaDinheiro().setVisible(true);
            DesaparecerTela();
        }
    }//GEN-LAST:event_jBF8ActionPerformed

    private void jTFItensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFItensActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFItensActionPerformed

    private void jTFQtdeTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFQtdeTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFQtdeTotalActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void jPainelLogoItensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPainelLogoItensMouseClicked
        if (yes) {
            switch (TestaClique) {
                case 0:
                    TestaClique = 1;
                    jLLogo.setVisible(false);
                    jItens.setVisible(true);
                    Scroll.setVisible(true);
                    break;
                case 1:
                    TestaClique = 0;
                    jLLogo.setVisible(true);
                    jItens.setVisible(false);
                    Scroll.setVisible(false);
                    break;
                default:
                    TestaClique = 0;
                    break;
            }
        }
    }//GEN-LAST:event_jPainelLogoItensMouseClicked

    private void ScrollMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ScrollMouseClicked
        if (yes) {
            switch (TestaClique) {
                case 0:
                    TestaClique = 1;
                    jLLogo.setVisible(false);
                    jItens.setVisible(true);
                    Scroll.setVisible(true);
                    break;
                case 1:
                    TestaClique = 0;
                    jLLogo.setVisible(true);
                    jItens.setVisible(false);
                    Scroll.setVisible(false);
                    break;
                default:
                    TestaClique = 0;
                    break;
            }
        }
    }//GEN-LAST:event_ScrollMouseClicked

    private void jTFQuantKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFQuantKeyTyped
        String caracteres = "0987654321";
        if (!caracteres.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }
    }//GEN-LAST:event_jTFQuantKeyTyped

    private void jTFCodigoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodigoKeyTyped
        String caracteres = "0987654321";
        if (!caracteres.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }
    }//GEN-LAST:event_jTFCodigoKeyTyped

    private void jBF10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBF10ActionPerformed
        jBF10.requestFocus();
        JOptionPane.showMessageDialog(rootPane, "Você acabou de acionar o botão EXTRA", "Acionamento", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jBF10ActionPerformed

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        jLEngrenagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/engrenagem.png")));
        jLCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cancel.png")));
        jLAtualizaServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save.png")));
    }//GEN-LAST:event_formMouseMoved

    private void jLCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLCancelarMouseClicked
        if (tela == false) {
            if (tipoOperador > 1 && tela == false) {
                new TelaCancelamento().setVisible(true);
                DesaparecerTela();
            } else {
                new TelaSupervisor("TelaCancelamento").setVisible(true);
                DesaparecerTela();
            }
        }
    }//GEN-LAST:event_jLCancelarMouseClicked

    private void jLCancelarMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLCancelarMouseMoved
        jLEngrenagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/engrenagem.png")));
        jLCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cancel_over.png")));
        jLAtualizaServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save.png")));
    }//GEN-LAST:event_jLCancelarMouseMoved

    private void jLEngrenagemMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLEngrenagemMouseReleased
        // TODO add your handling code here:
        if (tela == false) {
            if (tipoOperador > 1 && tela == false) {
                new TelaConfiguracoes().setVisible(true);
                DesaparecerTela();
            } else {
                new TelaSupervisor("TelaConfiguracoes").setVisible(true);
                DesaparecerTela();
            }
        }
    }//GEN-LAST:event_jLEngrenagemMouseReleased

    private void jLEngrenagemMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLEngrenagemMouseExited
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLEngrenagemMouseExited

    private void jLEngrenagemMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLEngrenagemMouseEntered
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLEngrenagemMouseEntered

    private void jLEngrenagemMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLEngrenagemMouseMoved
        jLEngrenagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/engrenagem_over.png")));
        jLCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cancel.png")));
        jLAtualizaServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save.png")));
    }//GEN-LAST:event_jLEngrenagemMouseMoved

    private void jLAtualizaServerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaServerMouseReleased
        if (AtualizaServer == 0) {
            JOptionPane.showMessageDialog(rootPane, "Não há um banco de dados secundário", "Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } else {
            if (tela == false) {
                getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                ConexaoServer conServer = new ConexaoServer();
                try {
                    boolean conexao = conServer.TestaConexao();
                    if (conexao == true) {

                        String value = "";
                        //===================== IMPORTA FC_EVENTO
                        con.Insert("TRUNCATE fc_evento");
                        rs = conServer.Select("SELECT id_fc_evento, data_evento, nome_evento, status_evento, imp_titulo_evento FROM fc_evento ORDER BY id_fc_evento");
                        value = "";
                        while (rs.next()) {
                            if (value.length() > 50000) {
                                con.Insert("INSERT INTO fc_evento (id_fc_evento, data_evento, nome_evento, status_evento, imp_titulo_evento) VALUES " + value);
                                value = "(" + rs.getString("id_fc_evento") + ", '" + rs.getString("data_evento") + "', '" + rs.getString("nome_evento") + "', '" + rs.getString("status_evento") + "', '" + rs.getString("imp_titulo_evento") + "')";
                            } else {
                                if (!value.equals("")) {
                                    value += ", ";
                                }
                                value += "(" + rs.getString("id_fc_evento") + ", '" + rs.getString("data_evento") + "', '" + rs.getString("nome_evento") + "', '" + rs.getString("status_evento") + "', '" + rs.getString("imp_titulo_evento") + "')";
                            }
                        }
                        if (value.length() > 0) 
                            con.Insert("INSERT INTO fc_evento (id_fc_evento, data_evento, nome_evento, status_evento, imp_titulo_evento) VALUES " + value);

                        //===================== IMPORTA FC_FORMA_PGTO
                        con.Insert("TRUNCATE fc_forma_pgto");
                        rs = conServer.Select("SELECT id_fc_forma_pgto, nome_pgto, tipo_pgto, sts_pgto FROM fc_forma_pgto ORDER BY id_fc_forma_pgto");
                        value = "";
                        while (rs.next()) {
                            if (value.length() > 50000) {
                                con.Insert("INSERT INTO fc_forma_pgto (id_fc_forma_pgto, nome_pgto, tipo_pgto, sts_pgto) VALUES " + value);
                                value = "(" + rs.getString("id_fc_forma_pgto") + ", '" + rs.getString("nome_pgto") + "', '" + rs.getString("tipo_pgto") + "', " + rs.getString("sts_pgto") + ")";
                            } else {
                                if (!value.equals("")) {
                                    value += ", ";
                                }
                                value += "(" + rs.getString("id_fc_forma_pgto") + ", '" + rs.getString("nome_pgto") + "', '" + rs.getString("tipo_pgto") + "', " + rs.getString("sts_pgto") + ")";
                            }
                        }
                        if (value.length() > 0)
                            con.Insert("INSERT INTO fc_forma_pgto (id_fc_forma_pgto, nome_pgto, tipo_pgto, sts_pgto) VALUES " + value);

                        //==================== IMPORTA FC_OPERADOR
                        con.Insert("TRUNCATE fc_operador");
                        rs = conServer.Select("SELECT id_fc_operador, nome_operador, login_operador, senha_operador, tipo_operador, validade_operador FROM fc_operador");
                        value = "";

                        while (rs.next()) {
                            if (value.length() > 50000) {
                                con.Insert("INSERT INTO fc_operador (id_fc_operador, nome_operador, login_operador, senha_operador, tipo_operador, validade_operador) VALUES " + value);
                                value = "(" + rs.getString("id_fc_operador") + ", '" + rs.getString("nome_operador") + "', '" + rs.getString("login_operador") + "', '" + rs.getString("senha_operador") + "', " + rs.getString("tipo_operador") + ", '" + rs.getString("validade_operador") + "')";
                            } else {
                                if (!value.equals("")) {
                                    value += ", ";
                                }
                                value += "(" + rs.getString("id_fc_operador") + ", '" + rs.getString("nome_operador") + "', '" + rs.getString("login_operador") + "', '" + rs.getString("senha_operador") + "', " + rs.getString("tipo_operador") + ", '" + rs.getString("validade_operador") + "')";
                            }
                        }
                        if (value.length() > 0)
                            con.Insert("INSERT INTO fc_operador (id_fc_operador, nome_operador, login_operador, senha_operador, tipo_operador, validade_operador) VALUES " + value);
                        
                        //==================== IMPORTA FC_PRODUTOS
                        con.Insert("TRUNCATE fc_produtos");
                        rs = conServer.Select("SELECT pr.id_fc_produtos, nome_produtos, impressao_produtos, valor_produtos, imp_valor_tipo, ticket_produtos, sts_produtos FROM fc_produtos_pdv pdv USE INDEX (id_fc_pdv) INNER JOIN fc_produtos pr ON pdv.id_fc_produtos = pr.id_fc_produtos INNER JOIN fc_produtos_tipo tp ON pr.id_fc_produtos_tipo = tp.id_fc_produtos_tipo WHERE id_fc_pdv = " + TelaPrincipal.pdvAtivo + " ORDER BY pr.id_fc_produtos");
                        value = "";

                        while (rs.next()) {
                            if (value.length() > 50000) {
                                con.Insert("INSERT INTO fc_produtos (id_fc_produtos, nome_produtos, impressao_produtos, valor_produtos, imp_valor_produtos, ticket_produtos, sts_produtos) VALUES " + value);
                                value = "(" + rs.getString("id_fc_produtos") + ", '" + rs.getString("nome_produtos") + "', '" + rs.getString("impressao_produtos") + "', '" + rs.getString("valor_produtos") + "', '" + rs.getString("imp_valor_tipo") + "', " + rs.getString("ticket_produtos") + ", '" + rs.getString("sts_produtos") + "')";
                            } else {
                                if (!value.equals("")) {
                                    value += ", ";
                                }
                                value += "(" + rs.getString("id_fc_produtos") + ", '" + rs.getString("nome_produtos") + "', '" + rs.getString("impressao_produtos") + "', '" + rs.getString("valor_produtos") + "', '" + rs.getString("imp_valor_tipo") + "', " + rs.getString("ticket_produtos") + ", '" + rs.getString("sts_produtos") + "')";
                            }
                        }
                        if (value.length() > 0) 
                            con.Insert("INSERT INTO fc_produtos (id_fc_produtos, nome_produtos, impressao_produtos, valor_produtos, imp_valor_produtos, ticket_produtos, sts_produtos) VALUES " + value);

                        //=================== EXPORTA FC_CAIXA
                        rs = con.Select("SELECT id_fc_caixa, id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, aberto_caixa, mktime_caixa, sts_caixa FROM fc_caixa USE INDEX (integra_caixa) WHERE integra_caixa = 0");
                        while (rs.next()) {
                            conServer.Insert("INSERT INTO fc_caixa (id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, aberto_caixa, saldo_caixa, mktime_caixa, sts_caixa, origem_caixa) VALUES (" + rs.getString("id_fc_evento") + ", " + rs.getString("id_fc_operador") + ", " + rs.getString("pdv_caixa") + ", '" + rs.getString("abertura_caixa") + "', '" + rs.getString("fechamento_caixa") + "', " + rs.getString("aberto_caixa") + ", '0', '" + rs.getString("mktime_caixa") + "', '" + rs.getString("sts_caixa") + "', '" + rs.getString("id_fc_caixa") + "')");
                            con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET integra_caixa = 1 WHERE id_fc_caixa = " + rs.getString("id_fc_caixa") + " LIMIT 1");
                        }

                        //=================== UPDATE FC_CAIXA
                        rs = con.Select("SELECT id_fc_caixa, pdv_caixa, fechamento_caixa, aberto_caixa, saldo_caixa FROM fc_caixa USE INDEX (aberto_caixa, update_caixa) WHERE aberto_caixa = 1 AND update_caixa = 0");
                        while (rs.next()) {
                            conServer.Insert("UPDATE fc_caixa USE INDEX (pdv_caixa, origem_caixa) SET fechamento_caixa = '" + rs.getString("fechamento_caixa") + "', aberto_caixa = '" + rs.getString("aberto_caixa") + "', saldo_caixa = '" + rs.getString("saldo_caixa") + "' WHERE pdv_caixa = " + rs.getString("pdv_caixa") + " AND origem_caixa = " + rs.getString("id_fc_caixa") + " LIMIT 1");
                            con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET update_caixa = 1 WHERE id_fc_caixa = " + rs.getString("id_fc_caixa") + " LIMIT 1");
                        }

                        //=================== EXPORTA FC_MOVIMENTO
                        rs = con.Select("SELECT id_fc_movimento, id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento FROM fc_movimento USE INDEX (integra_movimento) WHERE integra_movimento = 0");
                        value = "";
                        while (rs.next()) {
                            if (value.length() > 100000) {
                                conServer.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, origem_movimento) VALUES " + value);
                                value = "(" + rs.getString("id_fc_caixa") + ", " + rs.getString("id_fc_evento") + ", " + rs.getString("id_fc_operador") + ", " + rs.getString("id_fc_forma_pgto") + ", " + rs.getString("id_fc_vendedor") + "," + rs.getString("pdv_movimento") + ", '" + rs.getString("datahora_movimento") + "', " + rs.getString("tipo_movimento") + ", '" + rs.getString("descricao_movimento") + "', " + rs.getString("itens_movimento") + ", '" + rs.getString("valor_movimento") + "', '" + rs.getString("dinheiro_movimento") + "', '" + rs.getString("troco_movimento") + "', '" + rs.getString("mktime_movimento") + "', '" + rs.getString("sts_movimento") + "', '" + rs.getString("id_fc_movimento") + "')";
                            } else {
                                if (!value.equals("")) {
                                    value += ", ";
                                }
                                value += "("+ rs.getString("id_fc_caixa") +", "+ rs.getString("id_fc_evento") +", "+ rs.getString("id_fc_operador") +", "+ rs.getString("id_fc_forma_pgto") +", "+ rs.getString("id_fc_vendedor")+ ", "+ rs.getString("pdv_movimento") +", '"+ rs.getString("datahora_movimento") +"', "+ rs.getString("tipo_movimento") +", '"+ rs.getString("descricao_movimento") +"', "+ rs.getString("itens_movimento") +", '"+ rs.getString("valor_movimento") +"', '"+ rs.getString("dinheiro_movimento") +"', '"+ rs.getString("troco_movimento") +"', '"+ rs.getString("mktime_movimento") +"', "+ rs.getString("sts_movimento") +", "+ rs.getString("id_fc_movimento") +")";
                            }
                        }
                        if (value.length() > 0)
                            conServer.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, origem_movimento) VALUES " + value);
                        
                        con.Insert("UPDATE fc_movimento USE INDEX (integra_movimento) SET integra_movimento = 1 WHERE integra_movimento = 0");

                        //=================== EXPORTA FC_MOVIMENTO_ITEM
                        rs = con.Select("SELECT id_fc_movimento_item, id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, sts_movimento FROM fc_movimento_item USE INDEX (integra_movimento) WHERE integra_movimento = 0");
                        value = "";
                        while (rs.next()) {
                            if (value.length() > 100000) {
                                conServer.Insert("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, sts_movimento, origem_movimento) VALUES " + value);
                                value = "("+ rs.getString("id_fc_operador") +", "+ rs.getString("id_fc_caixa")+ ", "+ rs.getString("id_fc_evento") +", "+ rs.getString("id_fc_movimento")+ ", "+ rs.getString("id_fc_produtos")+ ", "+ rs.getString("id_fc_vendedor")+ ", "+ rs.getString("pdv_movimento")+ ", '"+ rs.getString("datahora_movimento")+ "', '"+ rs.getString("tipo_movimento")+ "', '"+ rs.getString("descricao_movimento")+ "', '"+ rs.getString("valor_movimento") +"', '"+ rs.getString("chave_movimento") +"', '"+ rs.getString("mktime_movimento") +"', '"+ rs.getString("sts_movimento")+ "', '"+ rs.getString("id_fc_movimento_item")+ "')";
                            } 
                            else {
                                if (!value.equals("")) {
                                    value += ", ";
                                }
                                value += "("+ rs.getString("id_fc_operador") +", "+ rs.getString("id_fc_caixa")+ ", "+ rs.getString("id_fc_evento") +", "+ rs.getString("id_fc_movimento")+ ", "+ rs.getString("id_fc_produtos")+ ", "+ rs.getString("id_fc_vendedor")+ ", "+ rs.getString("pdv_movimento")+ ", '"+ rs.getString("datahora_movimento")+ "', '"+ rs.getString("tipo_movimento")+ "', '"+ rs.getString("descricao_movimento")+ "', '"+ rs.getString("valor_movimento") +"', '"+ rs.getString("chave_movimento") +"', '"+ rs.getString("mktime_movimento") +"', '"+ rs.getString("sts_movimento")+ "', '"+ rs.getString("id_fc_movimento_item")+ "')";
                            }
                        }
                        if (value.length() > 0) 
                            conServer.Insert("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, sts_movimento, origem_movimento) VALUES " + value);

                        con.Insert("UPDATE fc_movimento_item USE INDEX (integra_movimento) SET integra_movimento = 1 WHERE integra_movimento = 0");

                        //=================== LOG SINCRONIZADOR
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        con.Insert("INSERT INTO fc_sync (pdv_sync, datahora_sync) VALUES (" + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "')");
                        conServer.Insert("INSERT INTO fc_sync (pdv_sync, arquivo_sync, datahora_sync, ip_inclusao) VALUES ("+ TelaPrincipal.pdvAtivo +", '', '"+ timeStamp +"', '')");

                        AdicionarLista();
                        JOptionPane.showMessageDialog(null, "Dados atualizados com sucesso", "LIFES CREATIVE - Frente de Caixa", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jLAtualizaServerMouseReleased

    private void jLAtualizaServerMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaServerMouseExited
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLAtualizaServerMouseExited

    private void jLAtualizaServerMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaServerMouseEntered
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLAtualizaServerMouseEntered

    private void jLAtualizaServerMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaServerMouseMoved
        jLEngrenagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/engrenagem.png")));
        jLCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cancel.png")));
        jLAtualizaServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save_over.png")));
    }//GEN-LAST:event_jLAtualizaServerMouseMoved


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane Scroll;
    private javax.swing.JButton jBESC;
    private javax.swing.JButton jBF10;
    private javax.swing.JButton jBF2;
    private javax.swing.JButton jBF4;
    private javax.swing.JButton jBF7;
    private javax.swing.JButton jBF8;
    private javax.swing.JButton jBF9;
    private javax.swing.JLabel jItens;
    private javax.swing.JLabel jLAtualizaServer;
    private javax.swing.JLabel jLCaixaFechado;
    private javax.swing.JLabel jLCancelar;
    private javax.swing.JLabel jLCodigo;
    private javax.swing.JLabel jLEngrenagem;
    private javax.swing.JLabel jLItens;
    private javax.swing.JLabel jLLogo;
    private javax.swing.JLabel jLNome;
    private javax.swing.JLabel jLOperador;
    private javax.swing.JLabel jLPDV;
    private javax.swing.JLabel jLQtdeTotal;
    private javax.swing.JLabel jLQuant;
    private javax.swing.JLabel jLSaldo;
    private javax.swing.JLabel jLSaldoCartao;
    private javax.swing.JLabel jLValorTotal;
    private javax.swing.JList<String> jListaProdutos;
    private javax.swing.JPanel jPainelLogoItens;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jSPPainelLista;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFCodigo;
    private javax.swing.JTextField jTFItens;
    private javax.swing.JTextField jTFNome;
    private javax.swing.JTextField jTFQtdeTotal;
    private javax.swing.JTextField jTFQuant;
    private javax.swing.JTextField jTFValorTotal;
    private javax.swing.JTable jTabelaProdutos;
    // End of variables declaration//GEN-END:variables

    private void AdicionarLista() {
        try {
            jItens.setText("");
            listaItens.clear();
            DecimalFormat format = new DecimalFormat("#,##0.00");
            String h = "<html><div align='left' style='float: left; margin-left:0px; padding-left:0px'>";
            ResultSet Rs = con.Select("SELECT id_fc_produtos,nome_produtos,valor_produtos FROM fc_produtos WHERE sts_produtos = 1");
            while (Rs.next()) {
                Produtos p = new Produtos(Integer.parseInt(Rs.getString("id_fc_produtos")), Float.parseFloat(Rs.getString("valor_produtos")), Rs.getString("nome_produtos"));
                listaItens.add(p);
            }
            h += "<table align='left' width='460' style='border-collapse: separate; float: left;padding-left:0px'>";
            for (int j = 0; j < listaItens.size(); j++) {
                h += "<tr align='center'><td align='left' style='padding-bottom:" + espaco + "px display: table; float:left; width:20px;'>" + listaItens.get(j).getId() + "</td><td align='left' style='padding-bottom:" + espaco + "px; display: table; float:left;'>" + listaItens.get(j).getNome() + "</td><td align='right' style='padding-bottom:" + espaco + "px'>" + format.format(listaItens.get(j).getPreco()) + "</td></tr>";
            }
            h += "</table></div></html>";
            jItens.setText(h);
        } catch (SQLException ex) {
            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
