package entity.cms;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RemoverCliente {

    // Lê clientes do arquivo em lotes, respeitando o tamanho máximo do buffer
    public static List<Cliente> lerClientesDeArquivo(String nomeArquivo, int tamanhoBuffer) throws IOException, ClassNotFoundException {
        List<Cliente> buffer = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            while (true) {
                try {
                    Cliente cliente = (Cliente) inputStream.readObject();
                    buffer.add(cliente);

                    // Se o buffer atingir o tamanho máximo, retorna os clientes carregados
                    if (buffer.size() == tamanhoBuffer) {
                        return buffer;
                    }
                } catch (EOFException e) {
                    // Fim do arquivo alcançado
                    break;
                }
            }
        }
        return buffer;
    }

    // Escreve os clientes restantes (sem o removido) no arquivo de saída
    public static void escreverClientesNoArquivo(String nomeArquivo, List<Cliente> clientes) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            for (Cliente cliente : clientes) {
                outputStream.writeObject(cliente);
            }
        }
    }

    // Executa a remoção do cliente e gravação do arquivo
    public static void execute(String arquivoEntrada, String arquivoSaida, String clienteRemover, int tamanhoBuffer) {
        List<Cliente> buffer = new ArrayList<>();
        try {
            boolean clienteEncontrado = false;

            // Abrir os arquivos de entrada e saída
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(arquivoEntrada));
                 ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(arquivoSaida))) {

                while (true) {
                    try {
                        // Ler clientes em lotes
                        Cliente cliente = (Cliente) inputStream.readObject();
                        if (!cliente.getNome().equals(clienteRemover)) {
                            buffer.add(cliente);
                        } else {
                            clienteEncontrado = true;
                        }

                        // Escrever o buffer no arquivo se atingir o tamanho máximo
                        if (buffer.size() == tamanhoBuffer) {
                            for (Cliente c : buffer) {
                                outputStream.writeObject(c);
                            }
                            buffer.clear();
                        }
                    } catch (EOFException e) {
                        // Fim do arquivo alcançado
                        break;
                    }
                }

                // Gravar os clientes restantes no buffer
                for (Cliente c : buffer) {
                    outputStream.writeObject(c);
                }
            }

            // Informar se o cliente foi encontrado ou não
            if (clienteEncontrado) {
                System.out.println("Cliente removido com sucesso.");
            } else {
                System.out.println("Cliente não encontrado.");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}
