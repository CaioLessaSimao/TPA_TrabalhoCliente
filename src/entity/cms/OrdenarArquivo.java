package entity.cms;

import java.io.*;
import java.util.*;

public class OrdenarArquivo {

    private static void salvarBloco(List<Cliente> clientes, String arquivoNome) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoNome))) {
            for (Cliente cliente : clientes) {
                oos.writeObject(cliente);
            }
        }
    }

    private static Iterator<Cliente> carregarBloco(String arquivoNome) throws IOException {
        return new Iterator<>() {
            private ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoNome));
            private Cliente proximo = carregarProximo();

            private Cliente carregarProximo() {
                try {
                    return (Cliente) ois.readObject();
                } catch (EOFException e) {
                    return null;
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean hasNext() {
                return proximo != null;
            }

            @Override
            public Cliente next() {
                Cliente atual = proximo;
                proximo = carregarProximo();
                return atual;
            }
        };
    }

    public static void execute(String arquivo) throws IOException {
        int tamanhoMemoria = 3000;
        List<String> arquivosTemp = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            List<Cliente> bloco = new ArrayList<>();
            while (true) {
                try {
                    Cliente cliente = (Cliente) ois.readObject();
                    bloco.add(cliente);
                    if (bloco.size() == tamanhoMemoria) {
                        bloco.sort(Comparator.naturalOrder());
                        String arquivoTemp = "temp_" + arquivosTemp.size() + ".bin";
                        salvarBloco(bloco, arquivoTemp);
                        arquivosTemp.add(arquivoTemp);
                        bloco.clear();
                    }
                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!bloco.isEmpty()) {
                bloco.sort(Comparator.naturalOrder());
                String arquivoTemp = "temp_" + arquivosTemp.size() + ".bin";
                salvarBloco(bloco, arquivoTemp);
                arquivosTemp.add(arquivoTemp);
            }
        }

        PriorityQueue<Item> heap = new PriorityQueue<>();
        List<Iterator<Cliente>> iteradores = new ArrayList<>();

        for (String arquivoTemp : arquivosTemp) {
            Iterator<Cliente> iterador = carregarBloco(arquivoTemp);
            iteradores.add(iterador);
            if (iterador.hasNext()) {
                heap.add(new Item(iterador.next(), iteradores.size() - 1));
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            while (!heap.isEmpty()) {
                Item menor = heap.poll();
                oos.writeObject(menor.cliente);

                Iterator<Cliente> iterador = iteradores.get(menor.index);
                if (iterador.hasNext()) {
                    heap.add(new Item(iterador.next(), menor.index));
                }
            }
        }

        for (String arquivoTemp : arquivosTemp) {
            new File(arquivoTemp).delete();
        }
    }

    private static class Item implements Comparable<Item> {
        Cliente cliente;
        int index;

        public Item(Cliente cliente, int index) {
            this.cliente = cliente;
            this.index = index;
        }

        @Override
        public int compareTo(Item outro) {
            return this.cliente.getNome().compareTo(outro.cliente.getNome());
        }
    }
}
