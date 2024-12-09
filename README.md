# Sistema de Gerenciamento de Clientes  

Este projeto é um sistema que permite o gerenciamento de clientes por meio de funcionalidades como cadastro, pesquisa, listagem e remoção. A aplicação utiliza uma interface gráfica para interações simples e diretas, sendo ideal para manipular grandes volumes de dados.  

## Requisitos  
- **Java JDK** (versão 8 ou superior).  
- Um ambiente de desenvolvimento ou terminal para execução de programas Java.  

## Como Executar  

1. **Baixe e Extraia o Projeto:**  
   - Certifique-se de ter o código-fonte em seu computador, no formato de arquivos `.java`.  

2. **Compile os Arquivos:**  
   - Navegue até o diretório do projeto no terminal e compile todos os arquivos Java:
     ```bash
     javac -d bin src/entity/cms/*.java
     ```
     (Certifique-se de que o diretório `bin` existe ou substitua por outro destino para os `.class`.)  

3. **Execute a Interface Gráfica:**  
   - A interface principal do sistema é a classe `ClienteGUI2`. Execute-a com o comando:
     ```bash
     java -cp bin entity.cms.ClienteGUI2
     ```

4. **Inicie as Operações:**  
   - Após carregar a aplicação, você pode:  
     - **Listar Clientes:** Clique em *"Listar Clientes"* e selecione o arquivo de dados (`Clientes.dat`).  
     - **Inserir Cliente:** Clique em *"Inserir Cliente"* e preencha os campos do formulário.  
     - **Pesquisar Cliente:** Digite o nome no campo de busca e clique em *"Pesquisar"*.  
     - **Remover Cliente:** Digite o nome do cliente e clique em *"Remover Cliente"*.  

5. **Atualização do Arquivo:**  
   - Qualquer alteração gera um novo arquivo atualizado (`arqClientesAtualizado.dat`) no diretório principal.  

## Breve Descrição Técnica  

- **Listagem de Clientes:** Ordenação externa via *K-Way Merge Sort*, permitindo o carregamento e exibição de grandes volumes de dados em memória limitada.  
- **Inserção:** Adiciona novos registros ao arquivo principal, preservando a integridade e a organização dos dados.  
- **Pesquisa:** Localiza um cliente específico por nome utilizando técnicas de leitura eficiente de arquivos.  
- **Remoção:** Exclui clientes do arquivo original e gera um novo arquivo atualizado.  

## Principais Classes  

- **`ClienteGUI2`**: Interface gráfica principal para gerenciar todas as operações.  
- **`novoCliente`**: Gerencia a inserção de novos clientes por meio de um formulário gráfico.  
- **`OrdenarArquivo`**: Implementa o algoritmo de ordenação externa para grandes volumes de dados.  
- **`RemoverCliente`**: Lê e regrava arquivos para excluir registros específicos.  

## Observação  

Certifique-se de que o arquivo de dados inicial (`Clientes.dat`) esteja disponível. Caso contrário, gere-o utilizando uma classe auxiliar, como `GeradorDeArquivosDeClientes`.  

## Autor  
- **Caio Lessa Simão**  
- **Marcos Vinícius Souza dos Santos**  
