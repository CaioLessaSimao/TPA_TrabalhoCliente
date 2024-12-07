package entity.cms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;

public class ClienteGUI2 extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private BufferDeClientes bufferDeClientes;
    private final int TAMANHO_BUFFER = 10000;
    private int registrosCarregados = 0; // Contador de registros já carregados
    private String arquivoSelecionado;
    private boolean arquivoCarregado = false; // Para verificar se o arquivo foi carregado

    public ClienteGUI2() {
        setTitle("Gerenciamento de Clientes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        bufferDeClientes = new BufferDeClientes();
        criarInterface();
    }


    private void carregarArquivo(String modo) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        int retorno = fileChooser.showOpenDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            arquivoSelecionado = fileChooser.getSelectedFile().getAbsolutePath();
            OrdenarArquivo.execute(arquivoSelecionado);
            bufferDeClientes.associaBuffer(new ArquivoCliente()); // Substitua por sua implementação
            bufferDeClientes.inicializaBuffer(modo,  arquivoSelecionado); // Passa o nome do arquivo aqui
            registrosCarregados = 0; // Reseta o contador
            tableModel.setRowCount(0); // Limpa a tabela
            carregarMaisClientes(); // Carrega os primeiros clientes
            arquivoCarregado = true; // Marca que o arquivo foi carregado
        }
    }

    private void criarInterface() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCarregar = new JButton("Listar Clientes");
        JButton btnRemover = new JButton("Remover Cliente");
        tableModel = new DefaultTableModel(new String[]{"#", "Nome", "Sobrenome", "Telefone", "Endereço", "Credit Score"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JTextField buscarField = new JTextField();
        JButton btnFiltrar = new JButton("Pesquisar");
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(buscarField, BorderLayout.CENTER);
        textPanel.add(btnFiltrar, BorderLayout.EAST);

        JTextField excluirField = new JTextField();
        JButton btnInserir = new JButton("Inserir Cliente");
        JPanel inserirPanel = new JPanel(new BorderLayout());



        // Adiciona um listener ao JScrollPane para carregar mais clientes ao rolar
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (!scrollPane.getVerticalScrollBar().getValueIsAdjusting()) {
                    if (arquivoCarregado &&
                            tableModel.getRowCount() >= TAMANHO_BUFFER &&
                            scrollPane.getVerticalScrollBar().getValue() +
                                    scrollPane.getVerticalScrollBar().getVisibleAmount() >=
                                    scrollPane.getVerticalScrollBar().getMaximum()) {
                        carregarMaisClientes();
                    }
                }
            }
        });

        btnCarregar.addActionListener(e -> {
            try {
                carregarArquivo("leitura");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnFiltrar.addActionListener(e -> buscarCliente(buscarField));
        btnRemover.addActionListener(e -> {
            try {
                removerCliente(buscarField);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnInserir.addActionListener(e -> inserirCliente());

        JPanel northPanel = new JPanel(new FlowLayout());
        northPanel.add(btnCarregar);
        northPanel.add(btnRemover);
        northPanel.add(btnInserir);

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(inserirPanel, BorderLayout.SOUTH);
        panel.add(textPanel, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }

    private void removerCliente(JTextField buscarField) throws IOException {
        if (arquivoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum arquivo carregado.");
            return;
        }

        String nomeParaRemover = buscarField.getText().trim().toLowerCase();
        if (nomeParaRemover.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o nome do cliente para remover.");
            return;
        }

        carregarArquivo("escrita");
        bufferDeClientes.inicializaBuffer("escrita", arquivoSelecionado);

        try {
            bufferDeClientes.removeCliente(nomeParaRemover); //chamar função
            JOptionPane.showMessageDialog(this, "Cliente removido com sucesso.");
            tableModel.setRowCount(0); // Limpa a tabela
            carregarMaisClientes(); // Atualiza os dados na tabela
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao remover cliente: " + e.getMessage());
        } finally {
            bufferDeClientes.fechaBuffer();
        }
    }

    private void carregarMaisClientes() {
        // Carrega apenas 10.000 registros de cada vez
        Cliente[] clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER); // Chama o método com o tamanho do buffer
        if (clientes != null && clientes.length > 0) {
            for (Cliente cliente : clientes) {
                if (cliente != null) { // Verifica se o cliente não é nulo
                    tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, cliente.getNome(), cliente.getSobrenome(), cliente.getTelefone(), cliente.getEndereco(), cliente.getCreditScore()});
                }
            }
            registrosCarregados += clientes.length; // Atualiza o contador
        }
    }

    public void buscarCliente(JTextField buscarField) {
        if (arquivoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum arquivo selecionado");
            return;
        }

        String busca = buscarField.getText().trim().toLowerCase();
        if (busca.isEmpty()) {  return;  }

        bufferDeClientes.inicializaBuffer("leitura", arquivoSelecionado);
        Cliente[] clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER);
        boolean clienteEncontrado = false;

        while (clientes != null && clientes.length > 0 && !clienteEncontrado) {
            for (Cliente cliente : clientes) {
                if (cliente != null && busca.equals(cliente.getNome().toLowerCase())) {
                    tableModel.setRowCount(0);
                    tableModel.addRow(new Object[]{
                            tableModel.getRowCount() + 1,
                            cliente.getNome(),
                            cliente.getSobrenome(),
                            cliente.getTelefone(),
                            cliente.getEndereco(),
                            cliente.getCreditScore()
                    });
                    clienteEncontrado = true;
                    break;
                }
            }
            if (!clienteEncontrado) {
                clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER);
            }
        }

        if (!clienteEncontrado) {
            JOptionPane.showMessageDialog(this, "Cliente não encontrado.");
        }
    }

    public void inserirCliente(){
        if (arquivoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum arquivo selecionado");
            return;
        }

        novoCliente nc = new novoCliente(arquivoSelecionado);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteGUI2 gui = new ClienteGUI2();
            gui.setVisible(true);
        });
    }
}
