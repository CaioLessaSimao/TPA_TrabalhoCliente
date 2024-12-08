package entity.cms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class novoCliente {
    JTextField campoNome, campoSobrenome, campoEndereco, campoTelefone, campoCreditScore;
    String arquivo;

    public novoCliente (String arquivo, ClienteGUI2 c) {

        this.arquivo = arquivo;

        // Cria o frame (janela)
        JFrame janela = new JFrame("Formulário de Cadastro");

        // Define o layout
        janela.setLayout(new GridLayout(6, 2, 10, 10)); // 6 linhas, 2 colunas, espaçamento de 10px

        // Cria os rótulos e campos de texto
        JLabel labelNome = new JLabel("Nome:");
        campoNome = new JTextField();

        JLabel labelSobrenome = new JLabel("Sobrenome:");
        campoSobrenome = new JTextField();

        JLabel labelEndereco = new JLabel("Endereço:");
        campoEndereco = new JTextField();

        JLabel labelTelefone = new JLabel("Telefone:");
        campoTelefone = new JTextField();

        JLabel labelCreditScore = new JLabel("Credit Score:");
        campoCreditScore = new JTextField();

        // Botão de envio
        JButton botaoEnviar = new JButton("Enviar");

        // Adiciona os componentes na janela
        janela.add(labelNome);
        janela.add(campoNome);

        janela.add(labelSobrenome);
        janela.add(campoSobrenome);

        janela.add(labelEndereco);
        janela.add(campoEndereco);

        janela.add(labelTelefone);
        janela.add(campoTelefone);

        janela.add(labelCreditScore);
        janela.add(campoCreditScore);

        janela.add(new JLabel()); // Espaço vazio para alinhamento
        janela.add(botaoEnviar);

        // Define a ação do botão
        botaoEnviar.addActionListener(e -> {
            try {
                adicionarCliente(janela, c);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Configurações da janela
        janela.setSize(400, 350); // Define o tamanho
        janela.setLocationRelativeTo(null); // Centraliza na tela
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Encerra o programa ao fechar
        janela.setVisible(true); // Torna a janela visível
    }

    public void adicionarCliente(JFrame janela, ClienteGUI2 c) throws IOException {
        String nome = campoNome.getText();
        String sobrenome = campoSobrenome.getText();
        String endereco = campoEndereco.getText();
        String telefone = campoTelefone.getText();
        int creditScore = Integer.parseInt(campoCreditScore.getText());

        Cliente cliente = new Cliente(nome, sobrenome, endereco, telefone, creditScore);
        //ArquivoCliente arquivoCliente = new ArquivoCliente();
        //arquivoCliente.abrirArquivo(arquivo, "escrita", Cliente.class);

        try {
            // Verifica se o arquivo já existe
            boolean arquivoExiste = new File(arquivo).exists();

            try (FileOutputStream fos = new FileOutputStream(arquivo, true);
                 ObjectOutputStream oos = arquivoExiste
                         ? new AppendableObjectOutputStream(fos)
                         : new ObjectOutputStream(fos)) {

                oos.writeObject(cliente);
                System.out.println("Cliente adicionado com sucesso!");
            }
        } catch (IOException e) {
            System.err.println("Erro ao gravar o objeto: " + e.getMessage());
        }
        c.recarregarArquivo();
        janela.dispose();
    }

    static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset(); // Evita reescrever o cabeçalho
        }
    }
}
