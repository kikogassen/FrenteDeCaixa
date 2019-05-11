
import Classes.AbreDados;
import Classes.ChecaVazio;
import Classes.Conexao;
import Classes.Cript;
import Classes.MD5;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class TelaConfiguracoes extends javax.swing.JFrame {

    /**
     * Creates new form TelaConfiguracoes
     */
    
    Conexao con = new Conexao();
    ResultSet rs;
    String CaminhoQuandoEntrou="";
    
    public TelaConfiguracoes() {
        initComponents();
        URL url = this.getClass().getResource("/imagens/ícone.png");  
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);  
        this.setIconImage(iconeTitulo);
        ConfiguraCloseOperation();
        ConfiguraESC();
        Iniciador();
    }
    
    private void Iniciador(){
        jTFPDV.setText("");
        try {
            rs=con.Select("SELECT banco_unico, host2, banco2, login2, senha2, pdv, imprime_codebar, pasta_arquivos, imprime_titulo, abertura, fechamento, sangria, suprimento FROM fc_config LIMIT 1");
            while (rs.next()){
                if (rs.getInt("banco_unico")==0){
                    Cript cripta = new Cript();
                    jRBSimServidor.setSelected(true);
                    HabilitaTFs();
                    jTFHost.setText(rs.getString("host2"));
                    jTFBD.setText(rs.getString("banco2"));
                    jTFUser.setText(cripta.descrip(rs.getString("login2")));
                    jPFSenha.setText(cripta.descrip(rs.getString("senha2")));
                } else {
                    jRBNaoServidor.setSelected(true);
                    DesabilitaTFs();
                }
                if (rs.getInt("imprime_codebar")==1){
                    jRBSimCodebar.setSelected(true);
                } else {
                    jRBNaoCodebar.setSelected(true);
                }
                if (rs.getInt("imprime_titulo")==1){
                    jRBSimTitulo.setSelected(true);
                } else {
                    jRBNaoTitulo.setSelected(true);
                }
                jTFPDV.setText(rs.getString("pdv"));
                jTFCaminho.setText(rs.getString("pasta_arquivos"));
                CaminhoQuandoEntrou=rs.getString("pasta_arquivos");
                jCBAbertura.setSelectedIndex(rs.getInt("abertura"));
                jCBFechamento.setSelectedIndex(rs.getInt("fechamento"));
                jCBSangria.setSelectedIndex(rs.getInt("sangria"));
                jCBSuprimento.setSelectedIndex(rs.getInt("suprimento"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            JOptionPane.showMessageDialog(null, "Erro na criptografia dos dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
        }
        jLOBS.setText("<html> Substituir contrabarra (\\) por barra normal (/).</html>");
    }
    
    private void ConfiguraCloseOperation(){
        addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    try {
                        int pdv = Integer.parseInt(jTFPDV.getText());
                        if (pdv>0){
                            TelaPrincipal.pdvAtivo = Integer.parseInt((String)jTFPDV.getText());
                            TelaPrincipal.AparecerTela=true;
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico positivo", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                            jTFPDV.setText("");
                            jTFPDV.requestFocus();
                            jTFPDV.setBackground(Color.yellow);
                        }
                    } catch (NumberFormatException n){
                        JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                        jTFPDV.setText("");
                        jTFPDV.requestFocus();
                        jTFPDV.setBackground(Color.yellow);
                    }
                }
            }
	);
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
    
    private void EnterProBD(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFBD.requestFocus();
                EnterProUser();
            }
        });
    }
    
    private void EnterProUser(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFUser.requestFocus();
                EnterPraSenha();
            }
        });
    }
    
    private void EnterPraSenha(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jPFSenha.requestFocus();
                EnterPraSalvar();
            }
        });
    }
    
    private void EnterPraSalvar(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBSalvarActionPerformed(arg0);
            }
        });
    }
    
    private void HabilitaTFs(){
        jLabel1.setEnabled(true);
        jLabel2.setEnabled(true);
        jLabel3.setEnabled(true);
        jLabel4.setEnabled(true);
        jTFBD.setEnabled(true);
        jTFHost.setEnabled(true);
        jTFUser.setEnabled(true);
        jPFSenha.setEnabled(true);
        jTFHost.requestFocus();
        jLTicket.setEnabled(true);
        jTFCaminho.setEnabled(true);
    }
    
    private void DesabilitaTFs(){
        jLabel1.setEnabled(false);
        jLabel2.setEnabled(false);
        jLabel3.setEnabled(false);
        jLabel4.setEnabled(false);
        jTFBD.setEnabled(false);
        jTFHost.setEnabled(false);
        jTFUser.setEnabled(false);
        jPFSenha.setEnabled(false);
        jLTicket.setEnabled(false);
        jTFCaminho.setEnabled(false);
        jTFBD.setText("");
        jTFHost.setText("");
        jTFUser.setText("");
        jPFSenha.setText("");
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bGCodebar = new javax.swing.ButtonGroup();
        bGServidor = new javax.swing.ButtonGroup();
        bGTitulo = new javax.swing.ButtonGroup();
        jBSoftware = new javax.swing.JButton();
        jLSoftware = new javax.swing.JLabel();
        jLTicket = new javax.swing.JLabel();
        jLCodebar = new javax.swing.JLabel();
        jLServidorRemoto = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTFUser = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPFSenha = new javax.swing.JPasswordField();
        jTFHost = new javax.swing.JTextField();
        jBSalvar = new javax.swing.JButton();
        jTFBD = new javax.swing.JTextField();
        jTFCaminho = new javax.swing.JTextField();
        jLOBS = new javax.swing.JLabel();
        jRBSimCodebar = new javax.swing.JRadioButton();
        jRBNaoCodebar = new javax.swing.JRadioButton();
        jRBSimServidor = new javax.swing.JRadioButton();
        jRBNaoServidor = new javax.swing.JRadioButton();
        jLCodebar1 = new javax.swing.JLabel();
        jRBNaoTitulo = new javax.swing.JRadioButton();
        jRBSimTitulo = new javax.swing.JRadioButton();
        jLPDV = new javax.swing.JLabel();
        jTFPDV = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jCBAbertura = new javax.swing.JComboBox<>();
        jCBSangria = new javax.swing.JComboBox<>();
        jCBFechamento = new javax.swing.JComboBox<>();
        jCBSuprimento = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("LC - Configurações");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jBSoftware.setText("Escolher");
        jBSoftware.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSoftwareActionPerformed(evt);
            }
        });

        jLSoftware.setText("Escolher Imagem Software:");

        jLTicket.setText("Pasta Tickets:");

        jLCodebar.setText("Imprimir código de barras?");

        jLServidorRemoto.setText("Servidor Remoto?");

        jLabel1.setText("Host MySQL");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jTFUser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFUserFocusGained(evt);
            }
        });

        jLabel2.setText("Banco de dados");

        jLabel3.setText("User");

        jLabel4.setText("Senha");

        jPFSenha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPFSenhaFocusGained(evt);
            }
        });

        jTFHost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFHostFocusGained(evt);
            }
        });

        jBSalvar.setText("Salvar");
        jBSalvar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalvarActionPerformed(evt);
            }
        });

        jTFBD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFBDFocusGained(evt);
            }
        });

        jLOBS.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLOBS.setText("Substituir contrabarras (\\) por barras normais (/)");

        bGCodebar.add(jRBSimCodebar);
        jRBSimCodebar.setText("Sim");

        bGCodebar.add(jRBNaoCodebar);
        jRBNaoCodebar.setText("Não");

        bGServidor.add(jRBSimServidor);
        jRBSimServidor.setText("Sim");
        jRBSimServidor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSimServidorActionPerformed(evt);
            }
        });

        bGServidor.add(jRBNaoServidor);
        jRBNaoServidor.setText("Não");
        jRBNaoServidor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBNaoServidorActionPerformed(evt);
            }
        });

        jLCodebar1.setText("Imprimir título do evento?");

        bGTitulo.add(jRBNaoTitulo);
        jRBNaoTitulo.setText("Não");

        bGTitulo.add(jRBSimTitulo);
        jRBSimTitulo.setText("Sim");

        jLPDV.setText("PDV:");
        jLPDV.setToolTipText("Ponto de Venda");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Impressão"));

        jLabel5.setText("Abertura:");

        jLabel6.setText("Fechamento:");

        jLabel7.setText("Suprimento:");

        jLabel8.setText("Sangria:");

        jCBAbertura.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5" }));

        jCBSangria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5" }));

        jCBFechamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5" }));

        jCBSuprimento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBSangria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBFechamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBSuprimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jCBAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBFechamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jCBSangria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBSuprimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLTicket)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPFSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFHost, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFBD, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFUser, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFCaminho, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLOBS)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jRBSimServidor)
                                        .addGap(18, 18, 18)
                                        .addComponent(jRBNaoServidor))))))
                    .addComponent(jLServidorRemoto)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLSoftware)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBSoftware))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLCodebar1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jRBSimTitulo)
                            .addGap(18, 18, 18)
                            .addComponent(jRBNaoTitulo))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLCodebar)
                            .addGap(18, 18, 18)
                            .addComponent(jRBSimCodebar)
                            .addGap(18, 18, 18)
                            .addComponent(jRBNaoCodebar))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLPDV)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTFPDV, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLSoftware)
                    .addComponent(jBSoftware))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLCodebar, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRBSimCodebar)
                    .addComponent(jRBNaoCodebar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRBSimTitulo)
                        .addComponent(jRBNaoTitulo))
                    .addComponent(jLCodebar1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLPDV)
                    .addComponent(jTFPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLServidorRemoto, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRBSimServidor)
                    .addComponent(jRBNaoServidor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPFSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTicket)
                    .addComponent(jTFCaminho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLOBS)
                .addGap(18, 18, 18)
                .addComponent(jBSalvar)
                .addGap(20, 20, 20))
        );

        jPanel1.getAccessibleContext().setAccessibleName("Impressão");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSoftwareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSoftwareActionPerformed
        // TODO add your handling code here:
        JFileChooser arquivo = new JFileChooser();
        arquivo.setFileFilter(new FileNameExtensionFilter("Arquivos de imagem", "bmp", "png", "jpg"));
        arquivo.setAcceptAllFileFilterUsed(false);
        arquivo.showOpenDialog(arquivo);
        File origem = arquivo.getSelectedFile();
        File destino;
        try {
            destino = new File("arq/logo.png");
            copyFile(origem, destino);
            TelaPrincipal.Configurou=true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro na criação da imagem. Não há o diretório FrenteDeCaixa/arq", "LC - Configurações", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jBSoftwareActionPerformed

    private void jBSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalvarActionPerformed
        // TODO add your handling code here:
        String host=jTFHost.getText();
        String BD=jTFBD.getText();
        String user=jTFUser.getText();
        String senha= new String(jPFSenha.getPassword());
        if ((host.equals("") || BD.equals("") || user.equals("") || senha.equals("")) && jRBSimServidor.isSelected()){
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
            ChecaVazio cv = new ChecaVazio();
            cv.ChecaVazio(jPFSenha, senha);
            cv.ChecaVazio(jTFBD, BD);
            cv.ChecaVazio(jTFUser, user);
            cv.ChecaVazio(jTFHost, host);
        } else {
            if (jRBSimCodebar.isSelected()){
                try {
                    con.Insert("UPDATE fc_config SET imprime_codebar=1 LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (jRBNaoCodebar.isSelected()){
                try {
                    con.Insert("UPDATE fc_config SET imprime_codebar=0 LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (jRBSimTitulo.isSelected()){
                try {
                    con.Insert("UPDATE fc_config SET imprime_titulo=1 LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (jRBNaoTitulo.isSelected()){
                try {
                    con.Insert("UPDATE fc_config SET imprime_titulo=0 LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (jRBNaoServidor.isSelected()){
                try {
                    con.Insert("UPDATE fc_config SET banco_unico=1, host2='', banco2='', login2='', senha2='' LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (jRBSimServidor.isSelected()){
                try {
                    Cript cripta = new Cript();
                    user=cripta.crip(user);
                    senha=cripta.crip(senha);
                    con.Insert("UPDATE fc_config SET banco_unico=0, host2='"+host+"', banco2='"+BD+"', login2='"+user+"', senha2='"+senha+"' LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na criptografia dos dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (!jTFCaminho.equals("")){
                if (!jTFCaminho.getText().equals(CaminhoQuandoEntrou)){
                    try {
                        con.Insert("UPDATE fc_config SET pasta_arquivos='"+jTFCaminho.getText()+"' LIMIT 1");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                int pdv = Integer.parseInt(jTFPDV.getText());
                if (pdv>0){
                    con.Insert("UPDATE fc_config SET pdv='"+jTFPDV.getText()+"' LIMIT 1");
                    con.Insert("UPDATE fc_caixa SET pdv_caixa='"+jTFPDV.getText()+"' WHERE id_fc_caixa="+TelaPrincipal.Id_CaixaAberto);
                    TelaPrincipal.pdvAtivo = Integer.parseInt((String)jTFPDV.getText());
                    TelaPrincipal.AparecerTela=true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico positivo", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                    jTFPDV.setText("");
                    jTFPDV.requestFocus();
                    jTFPDV.setBackground(Color.yellow);
                }
                con.Insert("UPDATE fc_config SET abertura='"+jCBAbertura.getSelectedIndex()+"', fechamento='"+jCBFechamento.getSelectedIndex()+"', sangria='"+jCBSangria.getSelectedIndex()+"', suprimento='"+jCBSuprimento.getSelectedIndex()+"'");
            } catch (NumberFormatException n){
                JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                jTFPDV.setText("");
                jTFPDV.requestFocus();
                jTFPDV.setBackground(Color.yellow);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jBSalvarActionPerformed

    private void jTFHostFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFHostFocusGained
        // TODO add your handling code here:
        EnterProBD();
    }//GEN-LAST:event_jTFHostFocusGained

    private void jTFBDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFBDFocusGained
        // TODO add your handling code here:
        EnterProUser();
    }//GEN-LAST:event_jTFBDFocusGained

    private void jTFUserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFUserFocusGained
        // TODO add your handling code here:
        EnterPraSenha();
    }//GEN-LAST:event_jTFUserFocusGained

    private void jPFSenhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPFSenhaFocusGained
        // TODO add your handling code here:
        EnterPraSalvar();
    }//GEN-LAST:event_jPFSenhaFocusGained

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        // TODO add your handling code here:
        toFront();
        requestFocus();
    }//GEN-LAST:event_formWindowLostFocus

    private void jRBSimServidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSimServidorActionPerformed
        // TODO add your handling code here:
        HabilitaTFs();
    }//GEN-LAST:event_jRBSimServidorActionPerformed

    private void jRBNaoServidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBNaoServidorActionPerformed
        // TODO add your handling code here:
        DesabilitaTFs();
    }//GEN-LAST:event_jRBNaoServidorActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bGCodebar;
    private javax.swing.ButtonGroup bGServidor;
    private javax.swing.ButtonGroup bGTitulo;
    private javax.swing.JButton jBSalvar;
    private javax.swing.JButton jBSoftware;
    private javax.swing.JComboBox<String> jCBAbertura;
    private javax.swing.JComboBox<String> jCBFechamento;
    private javax.swing.JComboBox<String> jCBSangria;
    private javax.swing.JComboBox<String> jCBSuprimento;
    private javax.swing.JLabel jLCodebar;
    private javax.swing.JLabel jLCodebar1;
    private javax.swing.JLabel jLOBS;
    private javax.swing.JLabel jLPDV;
    private javax.swing.JLabel jLServidorRemoto;
    private javax.swing.JLabel jLSoftware;
    private javax.swing.JLabel jLTicket;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPasswordField jPFSenha;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRBNaoCodebar;
    private javax.swing.JRadioButton jRBNaoServidor;
    private javax.swing.JRadioButton jRBNaoTitulo;
    private javax.swing.JRadioButton jRBSimCodebar;
    private javax.swing.JRadioButton jRBSimServidor;
    private javax.swing.JRadioButton jRBSimTitulo;
    private javax.swing.JTextField jTFBD;
    private javax.swing.JTextField jTFCaminho;
    private javax.swing.JTextField jTFHost;
    private javax.swing.JTextField jTFPDV;
    private javax.swing.JTextField jTFUser;
    // End of variables declaration//GEN-END:variables
}
