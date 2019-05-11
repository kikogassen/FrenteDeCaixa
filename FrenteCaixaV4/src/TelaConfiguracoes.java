
import Classes.ChecaVazio;
import Classes.Conexao;
import Classes.Cript;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TelaConfiguracoes extends javax.swing.JFrame {
    Conexao con = new Conexao();
    ResultSet rs;
    String CaminhoQuandoEntrou = "";
    private JColorChooser colorChooser;
    String cor = "";
    Color color;
    public TelaConfiguracoes() {
        initComponents();
        URL url = this.getClass().getResource("/imagens/icone.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(iconeTitulo);
        ConfiguraCloseOperation();
        ConfiguraESC();
        Iniciador();
    }
    private void Iniciador() {
        jTFPDV.setText("");
        getRootPane().setDefaultButton(jBSalvar);
        this.colorChooser = new JColorChooser();
        try {
            rs = con.Select("SELECT * FROM fc_config LIMIT 1");
            rs.first();
            if (rs.getInt("banco_unico") == 0) {
                jRBSimServidor.setSelected(true);
                HabilitaTFs();
                jTFHost.setText(rs.getString("host2"));
                jTFBD.setText(rs.getString("banco2"));
                jTFUser.setText(Cript.descrip(rs.getString("login2")));
                jPFSenha.setText(Cript.descrip(rs.getString("senha2")));
            } else {
                jRBNaoServidor.setSelected(true);
                DesabilitaTFs();
            }
            if (rs.getInt("imprime_codebar") == 1) {
                jRBSimCodebar.setSelected(true);
            } else {
                jRBNaoCodebar.setSelected(true);
            }
            if (rs.getInt("vendedor") == 1) {
                jRBSimVendedor.setSelected(true);
            } else {
                jRBNaoVendedor.setSelected(true);
            }
            jTFPDV.setText(rs.getString("pdv"));
            jCBAbertura.setSelectedIndex(rs.getInt("abertura"));
            jCBFechamento.setSelectedIndex(rs.getInt("fechamento"));
            jCBSangria.setSelectedIndex(rs.getInt("sangria"));
            jCBSuprimento.setSelectedIndex(rs.getInt("suprimento"));
            jSincroniza.setText(rs.getString("sincroniza"));
            cor = rs.getString("fonte_cor");
            color = new Color(Integer.parseInt(cor));
            jCorFonte.setBackground(color);
            jTamanhoFonte.setText(rs.getString("fonte_tamanho"));
            jEspaco.setText(rs.getString("fonte_espaco"));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            JOptionPane.showMessageDialog(null, "Erro na criptografia dos dados", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ConfiguraCloseOperation() {
        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                try {
                    int pdv = Integer.parseInt(jTFPDV.getText());
                    if (pdv > 0) {
                        TelaPrincipal.pdvAtivo = Integer.parseInt((String) jTFPDV.getText());
                        TelaPrincipal.AparecerTela = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico positivo", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                        jTFPDV.setText("");
                        jTFPDV.requestFocus();
                        jTFPDV.setBackground(Color.yellow);
                    }
                } catch (NumberFormatException n) {
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                    jTFPDV.setText("");
                    jTFPDV.requestFocus();
                    jTFPDV.setBackground(Color.yellow);
                }
            }
        }
        );
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

    private void EnterProBD() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFBD.requestFocus();
                EnterProUser();
            }
        });
    }

    private void EnterProUser() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFUser.requestFocus();
                EnterPraSenha();
            }
        });
    }

    private void EnterPraSenha() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jPFSenha.requestFocus();
                EnterPraSalvar();
            }
        });
    }

    private void EnterPraSalvar() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBSalvarActionPerformed(arg0);
            }
        });
    }

    private void HabilitaTFs() {
        jLabel1.setEnabled(true);
        jLabel2.setEnabled(true);
        jLabel3.setEnabled(true);
        jLabel4.setEnabled(true);
        jTFBD.setEnabled(true);
        jTFHost.setEnabled(true);
        jTFUser.setEnabled(true);
        jPFSenha.setEnabled(true);
        jLabel9.setEnabled(true);
        jSincroniza.setEnabled(true);
        TelaPrincipal.ServidorRemoto = 1;
        jTFHost.requestFocus();
    }

    private void DesabilitaTFs() {
        jLabel1.setEnabled(false);
        jLabel2.setEnabled(false);
        jLabel3.setEnabled(false);
        jLabel4.setEnabled(false);
        jTFBD.setEnabled(false);
        jTFHost.setEnabled(false);
        jTFUser.setEnabled(false);
        jPFSenha.setEnabled(false);
        jLabel9.setEnabled(false);
        jSincroniza.setEnabled(false);
        TelaPrincipal.ServidorRemoto = 0;
        jTFBD.setText("");
        jTFHost.setText("");
        jTFUser.setText("");
        jPFSenha.setText("");
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bGCodebar = new javax.swing.ButtonGroup();
        bGServidor = new javax.swing.ButtonGroup();
        bGTitulo = new javax.swing.ButtonGroup();
        bGVendedor = new javax.swing.ButtonGroup();
        jBSalvar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jCBAbertura = new javax.swing.JComboBox<>();
        jCBSangria = new javax.swing.JComboBox<>();
        jCBFechamento = new javax.swing.JComboBox<>();
        jCBSuprimento = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTamanhoFonte = new javax.swing.JTextField();
        jEspaco = new javax.swing.JTextField();
        jCorFonte = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jTFHost = new javax.swing.JTextField();
        jLServidorRemoto = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jRBSimServidor = new javax.swing.JRadioButton();
        jRBNaoServidor = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        jSincroniza = new javax.swing.JTextField();
        jTFUser = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPFSenha = new javax.swing.JPasswordField();
        jTFBD = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLPDV = new javax.swing.JLabel();
        jTFPDV = new javax.swing.JTextField();
        jLCodebar = new javax.swing.JLabel();
        jRBSimCodebar = new javax.swing.JRadioButton();
        jRBNaoCodebar = new javax.swing.JRadioButton();
        jBSoftware = new javax.swing.JButton();
        jLSoftware = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jRBSimVendedor = new javax.swing.JRadioButton();
        jRBNaoVendedor = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("LIFES CREATIVE - Configurações");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jBSalvar.setText("Salvar");
        jBSalvar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBSalvar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalvarActionPerformed(evt);
            }
        });

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBFechamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBSangria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBSuprimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jCBAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBSangria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBFechamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBSuprimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Listagem de produtos"));

        jLabel11.setText("Espaço:");

        jLabel10.setText("Tamanho:");

        jTamanhoFonte.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTamanhoFonteKeyTyped(evt);
            }
        });

        jEspaco.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jEspacoKeyTyped(evt);
            }
        });

        jCorFonte.setBorder(null);
        jCorFonte.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jCorFonte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCorFonteActionPerformed(evt);
            }
        });

        jLabel12.setText("Cor:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTamanhoFonte)
                .addGap(48, 48, 48)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jEspaco)
                .addGap(50, 50, 50)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jCorFonte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(31, 31, 31))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jCorFonte, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jTamanhoFonte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jEspaco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Servidor MySQL"));

        jTFHost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFHostFocusGained(evt);
            }
        });

        jLServidorRemoto.setText("Servidor Remoto?");

        jLabel1.setText("Host MySQL");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

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

        jLabel9.setText("Tempo de sincronização com o servidor (segundos):");

        jSincroniza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSincronizaActionPerformed(evt);
            }
        });

        jTFUser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFUserFocusGained(evt);
            }
        });

        jLabel3.setText("User");

        jPFSenha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPFSenhaFocusGained(evt);
            }
        });

        jTFBD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFBDFocusGained(evt);
            }
        });

        jLabel2.setText("Banco de dados");

        jLabel4.setText("Senha");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFUser, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFHost, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPFSenha)
                            .addComponent(jTFBD, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLServidorRemoto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRBSimServidor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRBNaoServidor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSincroniza, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLServidorRemoto, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRBSimServidor)
                    .addComponent(jRBNaoServidor)
                    .addComponent(jLabel9)
                    .addComponent(jSincroniza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTFBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jPFSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTFHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 13, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Configurações gerais"));

        jLPDV.setText("PDV:");
        jLPDV.setToolTipText("Ponto de Venda");

        jLCodebar.setText("Imprimir código de barras?");

        bGCodebar.add(jRBSimCodebar);
        jRBSimCodebar.setText("Sim");

        bGCodebar.add(jRBNaoCodebar);
        jRBNaoCodebar.setText("Não");

        jBSoftware.setText("Escolher");
        jBSoftware.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBSoftware.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSoftwareActionPerformed(evt);
            }
        });

        jLSoftware.setText("Logomarca:");

        jLabel13.setText("Identificar vendedor?");

        bGVendedor.add(jRBSimVendedor);
        jRBSimVendedor.setText("Sim");

        bGVendedor.add(jRBNaoVendedor);
        jRBNaoVendedor.setText("Não");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLSoftware)
                    .addComponent(jLPDV))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTFPDV)
                    .addComponent(jBSoftware, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLCodebar))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jRBSimCodebar)
                        .addGap(18, 18, 18)
                        .addComponent(jRBNaoCodebar))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jRBSimVendedor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRBNaoVendedor)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLCodebar, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jRBSimCodebar)
                        .addComponent(jRBNaoCodebar))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLPDV)
                        .addComponent(jTFPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLSoftware)
                    .addComponent(jBSoftware)
                    .addComponent(jLabel13)
                    .addComponent(jRBSimVendedor)
                    .addComponent(jRBNaoVendedor))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(265, 265, 265)
                .addComponent(jBSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(265, 265, 265))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.getAccessibleContext().setAccessibleName("Lista dos itens");

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
            TelaPrincipal.Configurou = true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro na criação da imagem. Não há o diretório FrenteDeCaixa/arq", "LIFES CREATIVE - Configurações", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jBSoftwareActionPerformed

    private void jBSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalvarActionPerformed
        // TODO add your handling code here:
        String host = jTFHost.getText();
        String BD = jTFBD.getText();
        String user = jTFUser.getText();
        String senha = new String(jPFSenha.getPassword());
        if ((host.equals("") || BD.equals("") || user.equals("") || senha.equals("") || jSincroniza.getText().equals("")) && jRBSimServidor.isSelected()) {
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
            ChecaVazio cv = new ChecaVazio();
            cv.ChecaVazio(jPFSenha, senha);
            cv.ChecaVazio(jTFBD, BD);
            cv.ChecaVazio(jTFUser, user);
            cv.ChecaVazio(jTFHost, host);
            cv.ChecaVazio(jSincroniza, jSincroniza.getText());

        } else {
            if (jTamanhoFonte.getText().equals("") || jEspaco.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                ChecaVazio cv = new ChecaVazio();
                cv.ChecaVazio(jTamanhoFonte, jTamanhoFonte.getText());
                cv.ChecaVazio(jEspaco, jEspaco.getText());
            } else {
                try {
                    con.Insert("UPDATE fc_config SET fonte_tamanho=" + jTamanhoFonte.getText() + ", fonte_espaco=" + jEspaco.getText() + ",fonte_cor=" + cor + " LIMIT 1");
                } catch (SQLException ex) {
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (jRBSimCodebar.isSelected()) {
                try {
                    con.Insert("UPDATE fc_config SET imprime_codebar=1 LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (jRBNaoCodebar.isSelected()) {
                try {
                    con.Insert("UPDATE fc_config SET imprime_codebar=0 LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                if (jRBSimVendedor.isSelected()) {
                    con.Insert("UPDATE fc_config SET vendedor = 1 LIMIT 1");
                } else if(jRBNaoVendedor.isSelected()){
                    con.Insert("UPDATE fc_config SET vendedor = 0 LIMIT 1");
                }
            } catch (SQLException ex) {
                Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*
            if (jRBSimTitulo.isSelected()) {
                try {
                    con.Insert("UPDATE fc_config SET imprime_titulo=1 LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (jRBNaoTitulo.isSelected()) {
                try {
                    con.Insert("UPDATE fc_config SET imprime_titulo=0 LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }*/
            if (jRBNaoServidor.isSelected()) {
                try {
                    con.Insert("UPDATE fc_config SET banco_unico=1, host2='', banco2='', login2='', senha2='' LIMIT 1");

                    TelaPrincipal.AtualizaServer = 0;
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (jRBSimServidor.isSelected()) {
                try {
                    user = Cript.crip(user);
                    senha = Cript.crip(senha);
                    TelaPrincipal.AtualizaServer = 1;
                    con.Insert("UPDATE fc_config SET banco_unico=0, host2='" + host + "', banco2='" + BD + "', login2='" + user + "', senha2='" + senha + "', sincroniza=" + Integer.parseInt(jSincroniza.getText()) + ", fonte_tamanho=" + Integer.parseInt(jTamanhoFonte.getText()) + ", fonte_cor='" + cor + "',fonte_espaco=" + Integer.parseInt(jEspaco.getText()) + " LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na criptografia dos dados", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                }
            }

            try {
                int pdv = Integer.parseInt(jTFPDV.getText());
                if (pdv > 0) {
                    con.Insert("UPDATE fc_config SET pdv='" + jTFPDV.getText() + "' LIMIT 1");
                    con.Insert("UPDATE fc_caixa SET pdv_caixa='" + jTFPDV.getText() + "' WHERE id_fc_caixa=" + TelaPrincipal.Id_CaixaAberto);
                    TelaPrincipal.pdvAtivo = Integer.parseInt((String) jTFPDV.getText());
                    TelaPrincipal.AparecerTela = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico positivo", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                    jTFPDV.setText("");
                    jTFPDV.requestFocus();
                    jTFPDV.setBackground(Color.yellow);
                }
                con.Insert("UPDATE fc_config SET abertura='" + jCBAbertura.getSelectedIndex() + "', fechamento='" + jCBFechamento.getSelectedIndex() + "', sangria='" + jCBSangria.getSelectedIndex() + "', suprimento='" + jCBSuprimento.getSelectedIndex() + "'");
            } catch (NumberFormatException n) {
                JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                jTFPDV.setText("");
                jTFPDV.requestFocus();
                jTFPDV.setBackground(Color.yellow);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Configuração", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jBSalvarActionPerformed

    private void jTFHostFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFHostFocusGained
        EnterProBD();
    }//GEN-LAST:event_jTFHostFocusGained

    private void jTFBDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFBDFocusGained
        EnterProUser();
    }//GEN-LAST:event_jTFBDFocusGained

    private void jTFUserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFUserFocusGained
        EnterPraSenha();
    }//GEN-LAST:event_jTFUserFocusGained

    private void jPFSenhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPFSenhaFocusGained
        EnterPraSalvar();
    }//GEN-LAST:event_jPFSenhaFocusGained

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        toFront();
        requestFocus();
    }//GEN-LAST:event_formWindowLostFocus

    private void jRBSimServidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSimServidorActionPerformed
        HabilitaTFs();
    }//GEN-LAST:event_jRBSimServidorActionPerformed

    private void jRBNaoServidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBNaoServidorActionPerformed
        DesabilitaTFs();
    }//GEN-LAST:event_jRBNaoServidorActionPerformed

    private void jSincronizaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSincronizaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jSincronizaActionPerformed

    private void jCorFonteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCorFonteActionPerformed
        color = JColorChooser.showDialog(null, "Selecione",
                this.getContentPane().getBackground());

        cor = String.valueOf(color.getRGB());
        //        java.awt.Color[r=0,g=0,b=0]
        jCorFonte.setBackground(color);
    }//GEN-LAST:event_jCorFonteActionPerformed

    private void jEspacoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jEspacoKeyTyped
        String caracteres = "0987654321";
        if (!caracteres.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }
    }//GEN-LAST:event_jEspacoKeyTyped

    private void jTamanhoFonteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTamanhoFonteKeyTyped
        String caracteres = "0987654321";
        if (!caracteres.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }
    }//GEN-LAST:event_jTamanhoFonteKeyTyped

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bGCodebar;
    private javax.swing.ButtonGroup bGServidor;
    private javax.swing.ButtonGroup bGTitulo;
    private javax.swing.ButtonGroup bGVendedor;
    private javax.swing.JButton jBSalvar;
    private javax.swing.JButton jBSoftware;
    private javax.swing.JComboBox<String> jCBAbertura;
    private javax.swing.JComboBox<String> jCBFechamento;
    private javax.swing.JComboBox<String> jCBSangria;
    private javax.swing.JComboBox<String> jCBSuprimento;
    private javax.swing.JButton jCorFonte;
    private javax.swing.JTextField jEspaco;
    private javax.swing.JLabel jLCodebar;
    private javax.swing.JLabel jLPDV;
    private javax.swing.JLabel jLServidorRemoto;
    private javax.swing.JLabel jLSoftware;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPasswordField jPFSenha;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRBNaoCodebar;
    private javax.swing.JRadioButton jRBNaoServidor;
    private javax.swing.JRadioButton jRBNaoVendedor;
    private javax.swing.JRadioButton jRBSimCodebar;
    private javax.swing.JRadioButton jRBSimServidor;
    private javax.swing.JRadioButton jRBSimVendedor;
    private javax.swing.JTextField jSincroniza;
    private javax.swing.JTextField jTFBD;
    private javax.swing.JTextField jTFHost;
    private javax.swing.JTextField jTFPDV;
    private javax.swing.JTextField jTFUser;
    private javax.swing.JTextField jTamanhoFonte;
    // End of variables declaration//GEN-END:variables
}
