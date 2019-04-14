
import Classes.Conexao;
import Classes.ConexaoServer;
import Classes.Imprimir;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class TelaPrincipal extends javax.swing.JFrame {

    /**
     * Creates new form TelaPrincipal
     */
    
    Conexao con = new Conexao();
    Imprimir i = new Imprimir();
    ResultSet rs, rs2, rs3, rs4;    
    String nome_produtos="";
    int id_fc_produtos=0, contadorLista=0;
    double valor_produtos=0;
    boolean testeRuim=false;
    static boolean FechouCompra = false;
    DecimalFormat df =  new DecimalFormat("0.00");
    static boolean AbriuCaixa = false;
    static boolean AparecerTela = false;
    static boolean FechouCaixa = false;
    static String HoraAberturaCaixa = "";
    static int Id_CaixaAberto = 0;
    static int Id_eventoAtivo = 0;
    static int pdvAtivo=0;
    static double saldo_atual=0;
    static String OperadorAtivo="";
    static int id_operadorAtivo = 0;
    static boolean SangrouSuprimiu = false;
    static String nome_eventoAtivo="";
    static int tipoOperador=0;
    static boolean Configurou=false;
    static String img_eventoAtivo="";
    
    public TelaPrincipal(String nome_operador, int id_operadorAtivo, int tipo_operador) throws SQLException, IOException {
        rs=con.Select("SELECT id_fc_produtos FROM fc_produtos LIMIT 1");
        while (rs.next()){
            int teste=rs.getInt("id_fc_produtos");
        }
        OperadorAtivo=nome_operador;
        tipoOperador=tipo_operador;
        this.id_operadorAtivo=id_operadorAtivo;
        initComponents();
        Iniciador();
    }

    private void Iniciador() throws SQLException, IOException{
        URL url = this.getClass().getResource("/imagens/ícone.png");  
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);  
        this.setIconImage(iconeTitulo);
        EscolheLogo();
        F1ProNome();
        ConfiguraESC();
        ConfiguraF7();
        ConfiguraF2();
        ConfiguraF4();
        ConfiguraF8();
        ConfiguraF9();
        ConfiguraTextButtons();
        jBESC.setEnabled(false);
        jBF2.setEnabled(false);
        jBF4.setEnabled(false);
        jBF8.setEnabled(false);
        jTabelaProdutos.setShowGrid(false);
        AjeitaTabela();
        EnterPraQuant();
        jSPPainelLista.setVisible(false);
        jTFCodigo.setEnabled(false);
        jTFNome.setEnabled(false);
        jTFQuant.setEnabled(false);
        jTFQtdeTotal.setText("");
        jTFItens.setText("");
        jTFValorTotal.setText("");
        rs=con.Select("SELECT pdv FROM fc_config LIMIT 1");
        while (rs.next()){
            pdvAtivo=rs.getInt("pdv");
        }
        SetaPDV();
        jLOperador.setText(jLOperador.getText()+OperadorAtivo);
        TestaSeTemCaixaAberto();
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    private void TestaSeTemCaixaAberto() throws SQLException, IOException {
        rs=con.Select("SELECT * FROM fc_caixa USE INDEX (id_fc_caixa) ORDER BY id_fc_caixa DESC LIMIT 1");
        while (rs.next()){
            if (rs.getString("fechamento_caixa").contains("0001-01-01 00:00:00") && rs.getInt("sts_caixa") == 0){
                if (rs.getInt("id_fc_operador")==id_operadorAtivo){
                    Id_CaixaAberto=rs.getInt("id_fc_caixa");
                    Id_eventoAtivo=rs.getInt("id_fc_evento");
                    HoraAberturaCaixa=rs.getString("abertura_caixa");
                    jTFCodigo.setEnabled(true);
                    SetaSaldo();
                    jTFNome.setEnabled(true);
                    jTFQuant.setEnabled(true);
                    jTFQtdeTotal.setText("0");
                    jTFItens.setText("0");
                    jTFValorTotal.setText("0,00");
                    jBF7.setText("<html><div align=center> Fechamento de <br> Caixa (F7)</div></html>");
                    jLCaixaFechado.setVisible(false);
                    jBESC.setEnabled(true);
                    jBF2.setEnabled(true);
                    jBF4.setEnabled(true);
                    jBF8.setEnabled(true);
                    SetaNomeLogoEvento();
                } else {
                    rs2=con.Select("SELECT nome_operador FROM fc_operador USE INDEX (id_fc_operador) WHERE id_fc_operador="+rs.getString("id_fc_operador"));
                    while (rs2.next()){
                        int resposta = JOptionPane.showConfirmDialog(null, "Há um caixa aberto do operador "+rs2.getString("nome_operador")+".\nDeseja encerrá-lo?", "LC - Frente de Caixa", JOptionPane.YES_NO_OPTION);
                        if (resposta==JOptionPane.YES_OPTION){
                            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                            long unixTime = System.currentTimeMillis() / 1000L;
                            try {
                                rs3=con.Select("SELECT saldo_caixa, id_fc_caixa, id_fc_evento FROM fc_caixa USE INDEX (id_fc_caixa) ORDER BY id_fc_caixa DESC LIMIT 1");
                                while (rs3.next()){
                                    con.Insert("UPDATE fc_caixa USE INDEX (sts_caixa) SET fechamento_caixa='"+timeStamp+"', sts_caixa=1 WHERE sts_caixa=0");
                                    con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, integra_movimento) VALUES ("+rs3.getString("id_fc_caixa")+", "+rs3.getString("id_fc_evento")+", "+TelaPrincipal.id_operadorAtivo+", "+TelaPrincipal.pdvAtivo+", '"+timeStamp+"', 2, 'Fechamento de Caixa', 0, 0, 0, 0, "+unixTime+", 0)");
                                    rs4=con.Select("SELECT fechamento FROM fc_config LIMIT 1");
                                    while (rs4.next()){
                                        for (int x=0;x<rs4.getInt("fechamento"); x++){
                                            //i.ImprimeFuncao(TelaPrincipal.img_eventoAtivo, TelaPrincipal.Id_eventoAtivo, TelaPrincipal.nome_eventoAtivo, "Fechamento Caixa", df.format(Double.parseDouble(rs3.getString("saldo_caixa"))), rs2.getString("nome_operador"), String.valueOf(TelaPrincipal.pdvAtivo), TelaPrincipal.img_eventoAtivo);
                                        }
                                    }
                                    TelaPrincipal.FechouCaixa=true;
                                    TelaPrincipal.AparecerTela=true;
                                    dispose();
                                }
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Fechamento de Caixa", JOptionPane.ERROR_MESSAGE);
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
    
    private void SetaNomeLogoEvento() throws SQLException{
        rs=con.Select("SELECT nome_evento, img_evento FROM fc_evento USE INDEX (id_fc_evento) WHERE id_fc_evento = "+Id_eventoAtivo);
        while (rs.next()){
            nome_eventoAtivo=rs.getString("nome_evento");
            img_eventoAtivo=rs.getString("img_evento");
        }
    }
    
    private void SetaPDV() throws SQLException{
        jLPDV.setText("PDV: "+pdvAtivo);
    }
    
    private void ConfiguraTextButtons(){
        jBESC.setText("<html><div align=center> Fechamento da <br> Venda (ESC)</div></html>");
        jBF2.setText("<html><div align=center> Cancelamento da <br> Venda (F2)</div></html>");
        jBF4.setText("<html><div align=center> Reimpressão de <br> Venda (F4)</div></html>");
        jBF7.setText("<html><div align=center> Abertura de <br> Caixa (F7)</div></html>");
        jBF8.setText("<html><div align=center> Entrada/Saída <br> de Dinheiro (F8)</div></html>");
        jBF9.setText("<html><div align=center> Relatório de <br> Vendas (F9)</div></html>");
    }
    
    private void EscolheLogo() throws SQLException{
        rs=con.Select("SELECT logo_programa FROM fc_config LIMIT 1");
        while (rs.next()){
            jLLogo.setIcon(new ImageIcon(rs.getString("logo_programa")));
        }
    }
    
    private void SetaSaldo() throws SQLException{
        rs = con.Select("SELECT SUM(valor_movimento) as saldo FROM fc_movimento USE INDEX (id_fc_caixa) WHERE id_fc_caixa="+Id_CaixaAberto+" AND tipo_movimento <> 11");
        while (rs.next()){
            jLSaldo.setText("Saldo: "+df.format(rs.getDouble("saldo")).replace(".",","));
            saldo_atual=rs.getDouble("saldo");
        }
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
    
    private void ConfiguraF9(){
        InputMap inputMap9 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap9.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"forward9");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap9);
        this.getRootPane().getActionMap().put("forward9", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF9ActionPerformed(arg0);
            }
        });
    }
    
    private void F1ProNome(){
        InputMap inputMap8 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap8.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),"forward8");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap8);
        this.getRootPane().getActionMap().put("forward8", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFNome.requestFocus();
                nome_produtos="";
                id_fc_produtos=0;
                EnterFazjList1Evento();
            }
        });
    }
    
    private void EnterPraQuant(){
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                /*try {
                    int codigo=Integer.parseInt(jTFCodigo.getText());
                    if (codigo>=1 && codigo<=46){
                        jTFQuant.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(null, "Código inexistente. Por favor, digite um código válido", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                        jTFCodigo.setText("");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Por favor, digite um código válido", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                    jTFCodigo.setText("");
                }*/
                jTFQuant.requestFocus();
            }
        });
    }
    
    private void EnterPraCadastrar(){
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    int quantidade=Integer.parseInt(jTFQuant.getText());
                    if (quantidade>0){
                        if (!jTFCodigo.getText().equals("") || (!jTFNome.getText().equals(""))){
                            try {
                                rs=con.Select("SELECT valor_produtos FROM fc_produtos USE INDEX (id_fc_produtos) WHERE id_fc_produtos="+jTFCodigo.getText()+" AND nome_produtos='"+jTFNome.getText()+"' AND sts_produtos=1");
                                while (rs.next()){
                                    valor_produtos=rs.getDouble("valor_produtos");
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            double preco=valor_produtos*Integer.parseInt(jTFQuant.getText());
                            if (!jTFNome.getText().equals("") && !jTFQuant.getText().equals("")){
                                DefaultTableModel val = (DefaultTableModel) jTabelaProdutos.getModel();
                                val.addRow(new String[]{jTFCodigo.getText(), jTFNome.getText(), jTFQuant.getText(), (df.format(preco).replace(".", ","))});
                                Atualiza3jTF(quantidade, preco);
                                nome_produtos="";
                                id_fc_produtos=0;
                                valor_produtos=0;
                                contadorLista=0;
                                testeRuim=false;
                            }
                            jTFNome.setText("");
                            jTFCodigo.setText("");
                            jTFQuant.setText("");
                            jTFCodigo.requestFocus();
                        } else {
                            JOptionPane.showMessageDialog(null, "Por favor, informe valores válidos", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                            jTFCodigo.setText("");
                            jTFNome.setText("");
                            jTFCodigo.requestFocus();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Por favor, digite uma quantidade válida", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                        jTFQuant.setText("");
                        jTFQuant.requestFocus();
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Os valores informados são inválidos", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                    jTFQuant.setText("");
                    jTFQuant.requestFocus();
                }
            }
        });
    }
    
    private void EnterFazNada(){
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                
            }
        });
    }
    
    private void ConfiguraSeta(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jListaProdutos.requestFocus();
                jListaProdutos.setSelectedIndex(contadorLista);
                EnterFazjList1Evento();
            }
        });
    }
    
    private void EnterPraQuantMod(){
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    rs=con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE sts_produtos=1 AND nome_produtos='"+jTFNome.getText()+"'");
                    while (rs.next()){
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
    }
    
    private void AjeitaTabela(){
        TableColumn col = jTabelaProdutos.getColumnModel().getColumn(0);
        col.setPreferredWidth(15);
        col = jTabelaProdutos.getColumnModel().getColumn(1);
        col.setPreferredWidth(250);
        col = jTabelaProdutos.getColumnModel().getColumn(2);
        col.setPreferredWidth(30);
        col = jTabelaProdutos.getColumnModel().getColumn(3);
        col.setPreferredWidth(50);
    }
    
    private void ZeraTudo(){
        DefaultTableModel val = (DefaultTableModel) jTabelaProdutos.getModel();
        nome_produtos="";
        id_fc_produtos=0;
        valor_produtos=0;
        contadorLista=0;
        testeRuim=false;
        jTFCodigo.setText("");
        jTFNome.setText("");
        jTFItens.setText("0");
        jTFQtdeTotal.setText("0");
        jTFQuant.setText("");
        jTFValorTotal.setText("0,00");
        for (int x=val.getRowCount(); x>0; x--){
            val.removeRow(x-1);
        }
        EnterPraQuant();
    }
    
    public static void copyFile(File source, File destination) throws IOException {
        if (destination.exists())
            destination.delete();
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destinationChannel = new FileOutputStream(destination).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(),
                    destinationChannel);
        } finally {
            if (sourceChannel != null && sourceChannel.isOpen())
                sourceChannel.close();
            if (destinationChannel != null && destinationChannel.isOpen())
                destinationChannel.close();
        }
    }
    
    private void EnterFazjList1Evento(){
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFNome.setText(jListaProdutos.getSelectedValue());
                jSPPainelLista.setVisible(false);
                try {
                    con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE nome_produtos='"+jTFNome.getText()+"' AND sts_produtos=1");
                    while (rs.next()){
                        id_fc_produtos=rs.getInt("id_fc_produtos");
                    }
                    jTFCodigo.setText(Integer.toString(id_fc_produtos));
                } catch (SQLException ex) {
                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
                jTFQuant.requestFocus();
            }
        });
    }
    
    private void Atualiza3jTF(int quantidad, double prec){
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
    
    private void ConfiguraESC(){
        InputMap inputMap3 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap3.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"forward3");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap3);
        this.getRootPane().getActionMap().put("forward3", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBESCActionPerformed(arg0);
            }
        });
    }
    
    private void DesaparecerTela(){
        jPanel1.setVisible(false);
        jPanel2.setVisible(false);
        jBESC.setVisible(false);
        jLLogo.setVisible(false);
        jBF2.setVisible(false);
        jBF4.setVisible(false);
        jBF7.setVisible(false);
        jBF8.setVisible(false);
        jBF9.setVisible(false);
        jLAtualizaServer.setVisible(false);
        jLExportaSQL.setVisible(false);
        jLEngrenagem.setVisible(false);
    }
    
    private void ConfiguraF7(){
        InputMap inputMap4 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap4.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),"forward4");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap4);
        this.getRootPane().getActionMap().put("forward4", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF7ActionPerformed(arg0);
            }
        });
    }
    
    private void ConfiguraF2(){
        InputMap inputMap5 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap5.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),"forward5");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap5);
        this.getRootPane().getActionMap().put("forward5", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF2ActionPerformed(arg0);
            }
        });
    }
    
    private void ConfiguraF8(){
        InputMap inputMap6 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap6.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0),"forward6");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap6);
        this.getRootPane().getActionMap().put("forward6", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF8ActionPerformed(arg0);
            }
        });
    }
    
    private void ConfiguraF4(){
        InputMap inputMap7 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap7.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0),"forward7");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap7);
        this.getRootPane().getActionMap().put("forward7", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBF4ActionPerformed(arg0);
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
        jBESC = new javax.swing.JButton();
        jBF2 = new javax.swing.JButton();
        jBF4 = new javax.swing.JButton();
        jBF9 = new javax.swing.JButton();
        jBF7 = new javax.swing.JButton();
        jBF8 = new javax.swing.JButton();
        jLLogo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLOperador = new javax.swing.JLabel();
        jLPDV = new javax.swing.JLabel();
        jLSaldo = new javax.swing.JLabel();
        jLAtualizaServer = new javax.swing.JLabel();
        jLEngrenagem = new javax.swing.JLabel();
        jLExportaSQL = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LC - Frente de Caixa");
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLCaixaFechado.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLCaixaFechado.setForeground(new java.awt.Color(255, 0, 0));
        jLCaixaFechado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLCaixaFechado.setText("Caixa Fechado");
        jPanel1.add(jLCaixaFechado, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 460, 200));

        jListaProdutos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListaProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListaProdutosMouseReleased(evt);
            }
        });
        jSPPainelLista.setViewportView(jListaProdutos);

        jPanel1.add(jSPPainelLista, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 130, 450, 80));

        jLCodigo.setText("Cód. Produto");
        jPanel1.add(jLCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, -1, -1));

        jTFCodigo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFCodigoFocusGained(evt);
            }
        });
        jTFCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFCodigoKeyReleased(evt);
            }
        });
        jPanel1.add(jTFCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 33, 217, 35));

        jLQuant.setText("Quantidade");
        jPanel1.add(jLQuant, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 16, -1, -1));

        jTFQuant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFQuantFocusGained(evt);
            }
        });
        jTFQuant.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFQuantKeyReleased(evt);
            }
        });
        jPanel1.add(jTFQuant, new org.netbeans.lib.awtextra.AbsoluteConstraints(247, 33, 222, 35));

        jLNome.setText("Nome Produto");
        jPanel1.add(jLNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        jTFNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFNomeKeyReleased(evt);
            }
        });
        jPanel1.add(jTFNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 99, 457, 35));

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
        jTabelaProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTabelaProdutosMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTabelaProdutos);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 460, 220));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setMaximumSize(new java.awt.Dimension(417, 94));

        jTFItens.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFItens.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFItens.setText("0");
        jTFItens.setFocusable(false);

        jLItens.setText("Itens");

        jTFQtdeTotal.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFQtdeTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFQtdeTotal.setText("0");
        jTFQtdeTotal.setFocusable(false);

        jTFValorTotal.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFValorTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFValorTotal.setText("0,00");
        jTFValorTotal.setFocusable(false);

        jLQtdeTotal.setText("Quantidade Total");

        jLValorTotal.setText("Valor Total");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFItens, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLItens))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFQtdeTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLQtdeTotal))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLValorTotal)
                    .addComponent(jTFValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jBESC.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBESC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt1.png"))); // NOI18N
        jBESC.setText("Fechamento");
        jBESC.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBESC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBESCActionPerformed(evt);
            }
        });

        jBF2.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt2.png"))); // NOI18N
        jBF2.setText("Cancelamento");
        jBF2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF2ActionPerformed(evt);
            }
        });

        jBF4.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt3.png"))); // NOI18N
        jBF4.setText("Reimprimir");
        jBF4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF4ActionPerformed(evt);
            }
        });

        jBF9.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt6.png"))); // NOI18N
        jBF9.setText("Relatório");
        jBF9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF9ActionPerformed(evt);
            }
        });

        jBF7.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt4.png"))); // NOI18N
        jBF7.setText("Abertura");
        jBF7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF7ActionPerformed(evt);
            }
        });

        jBF8.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jBF8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt5.png"))); // NOI18N
        jBF8.setText("Dinheiro");
        jBF8.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jBF8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBF8ActionPerformed(evt);
            }
        });

        jLLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));

        jLOperador.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLOperador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/operador.png"))); // NOI18N
        jLOperador.setText("Operador(a): ");

        jLPDV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLPDV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/pdv.png"))); // NOI18N
        jLPDV.setText("PDV: ");
        jLPDV.setToolTipText("Ponto de Venda ativo");

        jLSaldo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLSaldo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/saldo.png"))); // NOI18N
        jLSaldo.setText("Saldo: ");

        jLAtualizaServer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLAtualizaServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save.png"))); // NOI18N
        jLAtualizaServer.setText("Atualiza Servidor:    ");
        jLAtualizaServer.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
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

        jLEngrenagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLEngrenagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/engrenagem.png"))); // NOI18N
        jLEngrenagem.setToolTipText("Configurações do sistema");
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

        jLExportaSQL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLExportaSQL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/export.png"))); // NOI18N
        jLExportaSQL.setToolTipText("Exporta os movimentos como .sql");
        jLExportaSQL.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLExportaSQL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLExportaSQLMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLExportaSQLMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLExportaSQLMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLEngrenagem, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLOperador, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jLPDV, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jLSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jLAtualizaServer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(jLExportaSQL)
                .addGap(20, 20, 20))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLOperador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLPDV, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(jLSaldo, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(jLAtualizaServer)
                    .addComponent(jLEngrenagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLExportaSQL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBESC, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBF2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBF4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBF7, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBF8, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBF9, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBESC, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBF2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBF4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBF7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBF8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBF9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTFCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCodigoKeyReleased
        EnterPraQuant();
    }//GEN-LAST:event_jTFCodigoKeyReleased

    private void jTFNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFNomeKeyReleased
        // TODO add your handling code here:
        if (!jTFNome.getText().equals("")){
            EnterPraQuantMod();
            jSPPainelLista.setVisible(true);
            try {
                rs=con.Select("SELECT nome_produtos, id_fc_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE sts_produtos=1");
                DefaultListModel model = new DefaultListModel();
                boolean TestaSeTem=false;
                while (rs.next()){
                    nome_produtos=rs.getString("nome_produtos");
                    if (nome_produtos.contains(jTFNome.getText().toUpperCase())){
                        this.jListaProdutos.setModel(model);
                        model.addElement(nome_produtos);
                        TestaSeTem=true;
                    }
                }
                if (TestaSeTem==false){
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
        if ((!jTFCodigo.getText().equals("") || !jTFNome.getText().equals("")) && !jTFQuant.getText().equals("")){
            EnterPraCadastrar();
        } else {
            EnterFazNada();
        }
    }//GEN-LAST:event_jTFQuantKeyReleased

    private void jTFQuantFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFQuantFocusGained
        // TODO add your handling code here:
        jSPPainelLista.setVisible(false);
        try {
            rs=con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (id_fc_produtos) WHERE id_fc_produtos="+Integer.parseInt(jTFCodigo.getText())+" AND sts_produtos=1");
            if (rs.next()){
                try {
                    jTFQuant.requestFocus();
                    rs=con.Select("SELECT nome_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE id_fc_produtos="+jTFCodigo.getText()+" AND sts_produtos=1");
                    while (rs.next()){
                        nome_produtos=rs.getString("nome_produtos");
                    }
                    jTFNome.setText(nome_produtos);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Por favor, digite um código válido", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                    jTFCodigo.setText("");
                    jTFCodigo.requestFocus();
                    testeRuim=true;
                }
            } else if (!jTFNome.getText().equals("")){
                try {
                    rs=con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE nome_produtos='"+jTFNome.getText()+"' AND sts_produtos=1");
                    while (rs.next()){
                        id_fc_produtos=rs.getInt("id_fc_produtos");
                    }
                    jTFCodigo.setText(Integer.toString(id_fc_produtos));
                } catch (SQLException ex) {
                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (testeRuim==false){
                    JOptionPane.showMessageDialog(null, "Por favor, digite um código ou um nome válido", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                    jTFCodigo.requestFocus();
                    jTFCodigo.setText("");
                    testeRuim=true;
                }
            }
        } catch (SQLException e){
            
        }
    }//GEN-LAST:event_jTFQuantFocusGained

    private void jTabelaProdutosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabelaProdutosMouseReleased
        // TODO add your handling code here:
        DefaultTableModel val = (DefaultTableModel) jTabelaProdutos.getModel();
        switch (jTabelaProdutos.getSelectedRow()){
            case -1:
                break;
            default:
                int resposta = JOptionPane.showConfirmDialog(null, "Deseja excluir o produto?", "LC - Frente de Caixa", JOptionPane.YES_NO_OPTION);
                if (resposta==JOptionPane.YES_OPTION){
                    int valorLinha=0;
                    int qtdeLinha=0;
                    int linha=jTabelaProdutos.getSelectedRow();
                    valorLinha=Integer.parseInt(((String) val.getValueAt(linha, 3)).replaceAll(",00", ""));
                    qtdeLinha=Integer.parseInt((String) val.getValueAt(linha, 2));
                    int quantTotalExistente = Integer.parseInt(jTFQtdeTotal.getText());
                    quantTotalExistente = quantTotalExistente - qtdeLinha;
                    jTFQtdeTotal.setText(Integer.toString(quantTotalExistente));
                    int precoExistente = Integer.parseInt(jTFValorTotal.getText().replaceAll(",00", ""));
                    precoExistente = precoExistente - valorLinha;
                    jTFValorTotal.setText(Integer.toString(precoExistente)+",00");
                    int quantItensExistente = Integer.parseInt(jTFItens.getText());
                    quantItensExistente--;
                    jTFItens.setText(Integer.toString(quantItensExistente));
                    val.removeRow(jTabelaProdutos.getSelectedRow());
                    jTFCodigo.requestFocus();
                } else if (resposta==JOptionPane.NO_OPTION){
                    jTFCodigo.requestFocus();
                }
                break;
        }
    }//GEN-LAST:event_jTabelaProdutosMouseReleased

    private void jListaProdutosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListaProdutosMouseReleased
        // TODO add your handling code here:
        jTFNome.setText(jListaProdutos.getSelectedValue());
        jSPPainelLista.setVisible(false);
        try {
            rs=con.Select("SELECT id_fc_produtos FROM fc_produtos USE INDEX (sts_produtos) WHERE nome_produtos='"+jTFNome.getText()+"' AND sts_produtos=1");
            while (rs.next()){
                id_fc_produtos=rs.getInt("id_fc_produtos");
            }
            jTFCodigo.setText(Integer.toString(id_fc_produtos));
        } catch (SQLException ex) {
            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        jTFQuant.requestFocus();
    }//GEN-LAST:event_jListaProdutosMouseReleased
  
    private void jTFCodigoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCodigoFocusGained
        // TODO add your handling code here:
        testeRuim=false;
    }//GEN-LAST:event_jTFCodigoFocusGained

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        // TODO add your handling code here:
        if (AparecerTela==true){
            jPanel1.setVisible(true);
            jPanel2.setVisible(true);
            jBESC.setVisible(true);
            jLLogo.setVisible(true);
            jBF2.setVisible(true);
            jBF4.setVisible(true);
            jBF7.setVisible(true);
            jBF8.setVisible(true);
            jBF9.setVisible(true);
            jLAtualizaServer.setVisible(true);
            jLExportaSQL.setVisible(true);
            jLEngrenagem.setVisible(true);
            jTFCodigo.requestFocus();
            if (jTFCodigo.isEnabled()){
                jLCaixaFechado.setVisible(false);
            } else {
                jLCaixaFechado.setVisible(true);
            }
            try {
                SetaPDV();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            AparecerTela=false;
        }
        if (FechouCompra==true){
            try {
                SetaSaldo();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            ZeraTudo();
            FechouCompra=false;
            jTFCodigo.requestFocus();
            jSPPainelLista.setVisible(false);
            jLCaixaFechado.setVisible(false);
        }
        if (AbriuCaixa==true){
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
            jBF7.setText("<html><div align=center> Fechamento de <br> Caixa (F7)</div></html>");
            try {
                SetaSaldo();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            AbriuCaixa=false;
        }
        if (FechouCaixa==true){
            jBF7.setText("<html><div align=center> Abertura de <br> Caixa (F7)</div></html>");
            jTabelaProdutos.setShowGrid(false);
            jSPPainelLista.setVisible(false);
            jTFCodigo.setEnabled(false);
            jTFNome.setEnabled(false);
            jTFQuant.setEnabled(false);
            jBESC.setEnabled(false);
            jBF2.setEnabled(false);
            jBF4.setEnabled(false);
            jBF8.setEnabled(false);
            jTFQtdeTotal.setText("");
            jTFItens.setText("");
            jTFValorTotal.setText("");
            jLCaixaFechado.setVisible(true);
            jLSaldo.setText("Saldo: ");
            jTFCodigo.setText("");
            jTFNome.setText("");
            jTFQuant.setText("");
            FechouCaixa=false;
        }
        if (SangrouSuprimiu==true){
            try {
                SetaSaldo();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            jLCaixaFechado.setVisible(false);
            SangrouSuprimiu=false;
        }
        if (Configurou==true){
            JOptionPane.showMessageDialog(null, "Dados modificados com sucesso. O programa será reiniciado.", "LC - Frente de Caixa", JOptionPane.INFORMATION_MESSAGE);
            String comando = "java -jar " + new File("").getAbsolutePath() + "\\FrenteDeCaixa.jar";
            try {
                Process Processo = Runtime.getRuntime().exec(comando);
            } catch (IOException MensagemdeErro) {
                JOptionPane.showMessageDialog(null, "Erro ao reiniciar o programa", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
            Configurou=false;
        }
    }//GEN-LAST:event_formWindowGainedFocus

    private void jBESCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBESCActionPerformed
        // TODO add your handling code here:
        if (jTabelaProdutos.getModel().getRowCount()>0){
            new FechamentoCompra(Double.parseDouble(jTFValorTotal.getText().replace(",", ".")), Integer.parseInt(jTFQtdeTotal.getText()), jTabelaProdutos).setVisible(true);
            /*jPanel1.setVisible(false);
            jPanel2.setVisible(false);
            jBESC.setVisible(false);
            jLLogo.setVisible(false);
            jBF2.setVisible(false);
            jBF4.setVisible(false);
            jBF7.setVisible(false);
            jBF8.setVisible(false);
            jBF9.setVisible(false);*/
            DesaparecerTela();
        }
    }//GEN-LAST:event_jBESCActionPerformed

    private void jBF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBF2ActionPerformed
        // TODO add your handling code here:
        if (jBF7.getText().equals("<html><div align=center> Fechamento de <br> Caixa (F7)</div></html>")){
            int resposta = JOptionPane.showConfirmDialog(null, "Deseja cancelar a venda?", "LC - Frente de Caixa", JOptionPane.YES_NO_OPTION);
            if (resposta==JOptionPane.YES_OPTION){
                jPanel1.setVisible(true);
                jPanel2.setVisible(true);
                jBESC.setVisible(true);
                jLLogo.setVisible(true);
                jBF2.setVisible(true);
                jBF4.setVisible(true);
                jBF7.setVisible(true);
                jBF8.setVisible(true);
                jBF9.setVisible(true);
                jTFCodigo.requestFocus();
                jTFQtdeTotal.setText("0");
                jTFItens.setText("0");
                jTFValorTotal.setText("0,00");
                DefaultTableModel val = (DefaultTableModel) jTabelaProdutos.getModel();
                for (int x=jTabelaProdutos.getRowCount()-1;x>=0;x--){
                    val.removeRow(x);
                }
                jTFCodigo.setText("");
                jTFNome.setText("");
                jTFQuant.setText("");
            }
        }
    }//GEN-LAST:event_jBF2ActionPerformed

    private void jBF4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBF4ActionPerformed
        // TODO add your handling code here:
        if (jBF7.getText().equals("<html><div align=center> Fechamento de <br> Caixa (F7)</div></html>")){
            if (tipoOperador>=2){
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
        // TODO add your handling code here:
        if (jBF7.getText().equals("<html><div align=center> Abertura de <br> Caixa (F7)</div></html>")){
            try {
                new AberturaCaixa().setVisible(true);
                DesaparecerTela();
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados: "+s, "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            String[] auxiliar = jLSaldo.getText().split(": ");
            new FechamentoCaixa(Double.parseDouble(String.valueOf(saldo_atual).replace(",", "."))).setVisible(true);
            DesaparecerTela();
        }
    }//GEN-LAST:event_jBF7ActionPerformed

    private void jBF8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBF8ActionPerformed
        // TODO add your handling code here:
        if (jBF7.getText().equals("<html><div align=center> Fechamento de <br> Caixa (F7)</div></html>")){
            new EntradaSaidaDinheiro().setVisible(true);
            /*jPanel1.setVisible(false);
            jPanel2.setVisible(false);
            jBESC.setVisible(false);
            jLLogo.setVisible(false);
            jBF2.setVisible(false);
            jBF4.setVisible(false);
            jBF7.setVisible(false);
            jBF8.setVisible(false);
            jBF9.setVisible(false);*/
            DesaparecerTela();
        }
    }//GEN-LAST:event_jBF8ActionPerformed

    private void jLAtualizaServerMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaServerMouseEntered
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLAtualizaServerMouseEntered

    private void jLAtualizaServerMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaServerMouseExited
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLAtualizaServerMouseExited

    private void jLAtualizaServerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaServerMouseReleased
        // TODO add your handling code here:
        if (true){
            new TelaPedeCPF(Float.parseFloat(jTFValorTotal.getText().replace(",", "."))).setVisible(true);
            return;
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ConexaoServer conServer = new ConexaoServer();
        try {
            boolean conexao = conServer.TestaConexao();
            if (conexao==true){
                
                //===================== IMPORTA FC_EVENTO
                con.Insert("TRUNCATE fc_evento");
                rs=conServer.Select("SELECT id_fc_evento, id_usuarios, nome_evento, data_evento, img_evento, horario_inclusao FROM fc_evento");
                while (rs.next()){
                    con.Insert("INSERT INTO fc_evento (id_fc_evento, id_usuarios, nome_evento, data_evento, img_evento, horario_inclusao) VALUES ("+rs.getString("id_fc_evento")+", "+rs.getString("id_usuarios")+", '"+rs.getString("nome_evento")+"', '"+rs.getString("data_evento")+"', '"+rs.getString("img_evento")+"', '"+rs.getString("horario_inclusao")+"')");
                }
                
                //==================== IMPORTA FC_OPERADOR
                con.Insert("TRUNCATE fc_operador");
                rs=conServer.Select("SELECT id_fc_operador, id_usuarios, nome_operador, login_operador, senha_operador, tipo_operador, validade_operador, horario_inclusao FROM fc_operador");
                while (rs.next()){
                    con.Insert("INSERT INTO fc_operador (id_fc_operador, id_usuarios, nome_operador, login_operador, senha_operador, tipo_operador, validade_operador, horario_inclusao) VALUES ("+rs.getString("id_fc_operador")+", "+rs.getString("id_usuarios")+", '"+rs.getString("nome_operador")+"', '"+rs.getString("login_operador")+"', '"+rs.getString("senha_operador")+"', "+rs.getString("tipo_operador")+", '"+rs.getString("validade_operador")+"', '"+rs.getString("horario_inclusao")+"')");
                }
                
                //==================== IMPORTA FC_PRODUTOS
                con.Insert("TRUNCATE fc_produtos");
                rs=conServer.Select("SELECT id_fc_produtos, id_usuarios, nome_produtos, valor_produtos, sts_produtos FROM fc_produtos");
                while (rs.next()){
                    con.Insert("INSERT INTO fc_produtos (id_fc_produtos, id_usuarios, nome_produtos, valor_produtos, sts_produtos) VALUES ("+rs.getString("id_fc_produtos")+", "+rs.getString("id_usuarios")+", '"+rs.getString("nome_produtos")+"', '"+rs.getString("valor_produtos")+"', "+rs.getString("sts_produtos")+")");
                }
                
                /*//=================== EXPORTA FC_CAIXA
                rs=con.Select("SELECT id_fc_caixa, id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, sts_caixa, saldo_caixa, mktime_caixa FROM fc_caixa USE INDEX (integra_caixa) WHERE integra_caixa = 0");
                while (rs.next()){
                    conServer.Insert("INSERT INTO fc_caixa (id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, sts_caixa, saldo_caixa, mktime_caixa, origem_caixa) VALUES ("+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_caixa")+", '"+rs.getString("abertura_caixa")+"', '"+rs.getString("fechamento_caixa")+"', "+rs.getString("sts_caixa")+", '"+rs.getString("saldo_caixa")+"', '"+rs.getString("mktime_caixa")+"', '"+rs.getString("id_fc_caixa")+"') ");
                }
                con.Insert("UPDATE fc_caixa USE INDEX (integra_caixa) SET integra_caixa = 1 WHERE integra_caixa = 0");
                
                //=================== EXPORTA FC_MOVIMENTO
                rs=con.Select("SELECT id_fc_movimento, id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento FROM fc_movimento USE INDEX (integra_movimento) WHERE integra_movimento = 0");
                while (rs.next()){
                    conServer.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, origem_movimento) VALUES ("+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_movimento")+", '"+rs.getString("datahora_movimento")+"', "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', "+rs.getString("itens_movimento")+", '"+rs.getString("valor_movimento")+"', '"+rs.getString("dinheiro_movimento")+"', '"+rs.getString("troco_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("id_fc_movimento")+"')");
                }
                con.Insert("UPDATE fc_movimento USE INDEX (integra_movimento) SET integra_movimento = 1 WHERE integra_movimento = 0");
                
                //=================== EXPORTA FC_MOVIMENTO_ITEM
                rs=con.Select("SELECT id_fc_movimento_item, id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, sts_movimento, pdv_movimento FROM fc_movimento_item USE INDEX (integra_movimento) WHERE integra_movimento = 0");
                while (rs.next()){
                    conServer.Insert("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, pdv_movimento, origem_movimento) VALUES ("+rs.getString("id_fc_operador")+", "+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_movimento")+", "+rs.getString("id_fc_produtos")+", "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', '"+rs.getString("valor_movimento")+"', '"+rs.getString("chave_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("pdv_movimento")+"', '"+rs.getString("id_fc_movimento_item")+"')");
                }
                con.Insert("UPDATE fc_movimento_item USE INDEX (integra_movimento) SET integra_movimento = 1 WHERE integra_movimento = 0");
                */
                String value="";
            
                //=================== EXPORTA FC_CAIXA
                rs=con.Select("SELECT id_fc_caixa, id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, sts_caixa, saldo_caixa, mktime_caixa FROM fc_caixa USE INDEX (integra_caixa) WHERE integra_caixa = 0");
                value="";
                while (rs.next()){
                    if (value.length() > 200000) {
                        conServer.Insert("INSERT INTO fc_caixa (id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, sts_caixa, saldo_caixa, mktime_caixa, origem_caixa) VALUES "+value);
                        value = "("+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_caixa")+", '"+rs.getString("abertura_caixa")+"', '"+rs.getString("fechamento_caixa")+"', "+rs.getString("sts_caixa")+", '"+rs.getString("saldo_caixa")+"', '"+rs.getString("mktime_caixa")+"', '"+rs.getString("id_fc_caixa")+"')";
                    } else {
                        if (!value.equals(""))                 
                            value+=", ";
                        value += "("+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_caixa")+", '"+rs.getString("abertura_caixa")+"', '"+rs.getString("fechamento_caixa")+"', "+rs.getString("sts_caixa")+", '"+rs.getString("saldo_caixa")+"', '"+rs.getString("mktime_caixa")+"', '"+rs.getString("id_fc_caixa")+"')";
                    }
                }
                if(value.length() > 0)
                    conServer.Insert("INSERT INTO fc_caixa (id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, sts_caixa, saldo_caixa, mktime_caixa, origem_caixa) VALUES "+value);
                con.Insert("UPDATE fc_caixa USE INDEX (integra_caixa) SET integra_caixa = 1 WHERE integra_caixa = 0");

                //=================== EXPORTA FC_MOVIMENTO
                rs=con.Select("SELECT id_fc_movimento, id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento FROM fc_movimento USE INDEX (integra_movimento) WHERE integra_movimento = 0");
                value="";
                while (rs.next()){
                    if (value.length() > 200000) {
                        conServer.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, origem_movimento) VALUES "+value);
                        value = "("+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_movimento")+", '"+rs.getString("datahora_movimento")+"', "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', "+rs.getString("itens_movimento")+", '"+rs.getString("valor_movimento")+"', '"+rs.getString("dinheiro_movimento")+"', '"+rs.getString("troco_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("id_fc_movimento")+"')";
                    } else {
                        if (!value.equals(""))                 
                            value+=", ";
                        value += "("+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_movimento")+", '"+rs.getString("datahora_movimento")+"', "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', "+rs.getString("itens_movimento")+", '"+rs.getString("valor_movimento")+"', '"+rs.getString("dinheiro_movimento")+"', '"+rs.getString("troco_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("id_fc_movimento")+"')";
                    }
                }
                if(value.length() > 0)
                    conServer.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, origem_movimento) VALUES "+value);
                con.Insert("UPDATE fc_movimento USE INDEX (integra_movimento) SET integra_movimento = 1 WHERE integra_movimento = 0");

                //=================== EXPORTA FC_MOVIMENTO_ITEM
                rs=con.Select("SELECT id_fc_movimento_item, id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, sts_movimento, pdv_movimento FROM fc_movimento_item USE INDEX (integra_movimento) WHERE integra_movimento = 0");
                value = "";
                while (rs.next()){
                    if (value.length() > 200000) {
                        conServer.Insert("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, pdv_movimento, origem_movimento) VALUES "+value);
                        value = "("+rs.getString("id_fc_operador")+", "+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_movimento")+", "+rs.getString("id_fc_produtos")+", "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', '"+rs.getString("valor_movimento")+"', '"+rs.getString("chave_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("pdv_movimento")+"', '"+rs.getString("id_fc_movimento_item")+"')";
                    } else {
                        if (!value.equals(""))                 
                            value+=", ";
                        value += "("+rs.getString("id_fc_operador")+", "+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_movimento")+", "+rs.getString("id_fc_produtos")+", "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', '"+rs.getString("valor_movimento")+"', '"+rs.getString("chave_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("pdv_movimento")+"', '"+rs.getString("id_fc_movimento_item")+"')";
                    }   
                }
                if(value.length() > 0)
                    conServer.Insert("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, pdv_movimento, origem_movimento) VALUES "+value);
                con.Insert("UPDATE fc_movimento_item USE INDEX (integra_movimento) SET integra_movimento = 1 WHERE integra_movimento = 0");
                
                
                //=================== COPIA OS LOGOS DOS PROGRAMAS PRO LOCAL
                /*rs=con.Select("SELECT img_evento FROM fc_evento");
                
                while (rs.next()){
                    rs2=con.Select("SELECT pasta_arquivos FROM fc_config LIMIT 1");
                    while (rs2.next()){
                        File destino = new File("arq/eventos/"+rs.getString("img_evento"));
                        File origem = new File(rs2.getString("pasta_arquivos").replace("/", "\\")+"/"+rs.getString("img_evento"));
                        copyFile(origem, destino);
                    }
                }
               */ 
                JOptionPane.showMessageDialog(null, "Dados atualizados com sucesso", "LC - Frente de Caixa", JOptionPane.INFORMATION_MESSAGE);
               
                //================== REINICIA O PROGRAMA
                
                /*JOptionPane.showMessageDialog(null, "Dados atualizados com sucesso. O programa será reiniciado", "LC - Frente de Caixa", JOptionPane.INFORMATION_MESSAGE);
                String comando = "java -jar " + new File("").getAbsolutePath() + "/FrenteDeCaixa.jar";
                try {
                    Process Processo = Runtime.getRuntime().exec(comando);
                } catch (IOException MensagemdeErro) {
                    JOptionPane.showMessageDialog(null, "Erro ao reiniciar o programa", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
                }
                System.exit(0);*/
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }/* catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao copiar as imagens", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }//GEN-LAST:event_jLAtualizaServerMouseReleased

    private void jLEngrenagemMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLEngrenagemMouseReleased
        // TODO add your handling code here:
        if (tipoOperador>1){
            new TelaConfiguracoes().setVisible(true);
            DesaparecerTela();
        } else {
            new TelaSupervisor("TelaConfiguracoes").setVisible(true);
            DesaparecerTela();
        }
    }//GEN-LAST:event_jLEngrenagemMouseReleased

    private void jLEngrenagemMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLEngrenagemMouseEntered
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLEngrenagemMouseEntered

    private void jLEngrenagemMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLEngrenagemMouseExited
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLEngrenagemMouseExited

    private void jLExportaSQLMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLExportaSQLMouseEntered
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLExportaSQLMouseEntered

    private void jLExportaSQLMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLExportaSQLMouseExited
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLExportaSQLMouseExited

    private void jLExportaSQLMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLExportaSQLMouseReleased
        // TODO add your handling code here:
        try {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(Calendar.getInstance().getTime());
            FileWriter fw = new FileWriter("sql/exporta-pdv"+pdvAtivo+"-"+timeStamp+".sql", true);
            BufferedWriter bw = new BufferedWriter(fw);
            String value="";
            
            //=================== EXPORTA FC_CAIXA
            rs=con.Select("SELECT id_fc_caixa, id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, sts_caixa, saldo_caixa, mktime_caixa FROM fc_caixa USE INDEX (integra_caixa) WHERE integra_caixa = 0");
            value="";
            while (rs.next()){
                if (value.length() > 200000) {
                    bw.write("INSERT INTO fc_caixa (id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, sts_caixa, saldo_caixa, mktime_caixa, origem_caixa) VALUES "+value+";\r\n");
                    value = "("+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_caixa")+", '"+rs.getString("abertura_caixa")+"', '"+rs.getString("fechamento_caixa")+"', "+rs.getString("sts_caixa")+", '"+rs.getString("saldo_caixa")+"', '"+rs.getString("mktime_caixa")+"', '"+rs.getString("id_fc_caixa")+"')";
                } else {
                    if (!value.equals(""))                 
                        value+=", ";
                    value += "("+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_caixa")+", '"+rs.getString("abertura_caixa")+"', '"+rs.getString("fechamento_caixa")+"', "+rs.getString("sts_caixa")+", '"+rs.getString("saldo_caixa")+"', '"+rs.getString("mktime_caixa")+"', '"+rs.getString("id_fc_caixa")+"')";
                }
            }
            if(value.length() > 0)
                bw.write("INSERT INTO fc_caixa (id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, sts_caixa, saldo_caixa, mktime_caixa, origem_caixa) VALUES "+value+";\r\n");
            con.Insert("UPDATE fc_caixa USE INDEX (integra_caixa) SET integra_caixa = 1 WHERE integra_caixa = 0");

            //=================== EXPORTA FC_MOVIMENTO
            rs=con.Select("SELECT id_fc_movimento, id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento FROM fc_movimento USE INDEX (integra_movimento) WHERE integra_movimento = 0");
            value="";
            while (rs.next()){
                if (value.length() > 200000) {
                    bw.write("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, origem_movimento) VALUES "+value+";\r\n");
                    value = "("+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_movimento")+", '"+rs.getString("datahora_movimento")+"', "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', "+rs.getString("itens_movimento")+", '"+rs.getString("valor_movimento")+"', '"+rs.getString("dinheiro_movimento")+"', '"+rs.getString("troco_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("id_fc_movimento")+"')";
                } else {
                    if (!value.equals(""))                 
                        value+=", ";
                    value += "("+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_operador")+", "+rs.getString("pdv_movimento")+", '"+rs.getString("datahora_movimento")+"', "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', "+rs.getString("itens_movimento")+", '"+rs.getString("valor_movimento")+"', '"+rs.getString("dinheiro_movimento")+"', '"+rs.getString("troco_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("id_fc_movimento")+"')";
                }
            }
            if(value.length() > 0)
                bw.write("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, origem_movimento) VALUES "+value+";\r\n");
            con.Insert("UPDATE fc_movimento USE INDEX (integra_movimento) SET integra_movimento = 1 WHERE integra_movimento = 0");

            //=================== EXPORTA FC_MOVIMENTO_ITEM
            rs=con.Select("SELECT id_fc_movimento_item, id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, sts_movimento, pdv_movimento FROM fc_movimento_item USE INDEX (integra_movimento) WHERE integra_movimento = 0");
            value = "";
            while (rs.next()){
                if (value.length() > 200000) {
                    bw.write("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, pdv_movimento, origem_movimento) VALUES "+value+";\r\n");
                    value = "("+rs.getString("id_fc_operador")+", "+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_movimento")+", "+rs.getString("id_fc_produtos")+", "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', '"+rs.getString("valor_movimento")+"', '"+rs.getString("chave_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("pdv_movimento")+"', '"+rs.getString("id_fc_movimento_item")+"')";
                } else {
                    if (!value.equals(""))                 
                        value+=", ";
                    value += "("+rs.getString("id_fc_operador")+", "+rs.getString("id_fc_caixa")+", "+rs.getString("id_fc_evento")+", "+rs.getString("id_fc_movimento")+", "+rs.getString("id_fc_produtos")+", "+rs.getString("tipo_movimento")+", '"+rs.getString("descricao_movimento")+"', '"+rs.getString("valor_movimento")+"', '"+rs.getString("chave_movimento")+"', '"+rs.getString("mktime_movimento")+"', '"+rs.getString("pdv_movimento")+"', '"+rs.getString("id_fc_movimento_item")+"')";
                }   
            }
            if(value.length() > 0)
                bw.write("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, tipo_movimento, descricao_movimento, valor_movimento, chave_movimento, mktime_movimento, pdv_movimento, origem_movimento) VALUES "+value+";\r\n");
            con.Insert("UPDATE fc_movimento_item USE INDEX (integra_movimento) SET integra_movimento = 1 WHERE integra_movimento = 0");
            
            bw.close();
            JOptionPane.showMessageDialog(null, "Dados exportados com sucesso", "LC - Frente de Caixa", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar os dados. Não foi possível encontrar a pasta sql", "LC - Frente de Caixa", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaValidaDadosBanco.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jLExportaSQLMouseReleased

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBESC;
    private javax.swing.JButton jBF2;
    private javax.swing.JButton jBF4;
    private javax.swing.JButton jBF7;
    private javax.swing.JButton jBF8;
    private javax.swing.JButton jBF9;
    private javax.swing.JLabel jLAtualizaServer;
    private javax.swing.JLabel jLCaixaFechado;
    private javax.swing.JLabel jLCodigo;
    private javax.swing.JLabel jLEngrenagem;
    private javax.swing.JLabel jLExportaSQL;
    private javax.swing.JLabel jLItens;
    private javax.swing.JLabel jLLogo;
    private javax.swing.JLabel jLNome;
    private javax.swing.JLabel jLOperador;
    private javax.swing.JLabel jLPDV;
    private javax.swing.JLabel jLQtdeTotal;
    private javax.swing.JLabel jLQuant;
    private javax.swing.JLabel jLSaldo;
    private javax.swing.JLabel jLValorTotal;
    private javax.swing.JList<String> jListaProdutos;
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
}
