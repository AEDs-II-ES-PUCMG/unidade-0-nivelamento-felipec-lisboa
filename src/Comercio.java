import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Versão alternativa da aplicação de comércio,
 * com a mesma lógica básica de App, mas em uma classe separada.
 * Útil para comparar abordagens e estudar a estrutura do código.
 */
public class Comercio {
    /** Para inclusão de novos produtos no vetor em cada execução */
    static final int MAX_NOVOS_PRODUTOS = 10;

    /** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados. Sempre terá espaço para 10 novos produtos a cada execução */
    static Produto[] produtosCadastrados;

    /** Quantidade produtos cadastrados atualmente no vetor */
    static int quantosProdutos;

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa(){
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho(){
        System.out.println("AEDII COMÉRCIO DE COISINHAS");
        System.out.println("===========================");
    }

    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * Perceba que poderia haver uma melhor modularização com a criação de uma classe Menu.
     * @return Um inteiro com a opção do usuário.
    */
    static int menu(){
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar e listar um produto");
        System.out.println("3 - Cadastrar novo produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os dados de um arquivo texto e retorna um vetor de produtos. Arquivo no formato
     * N  (quantiade de produtos) <br/>
     * tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
        // Scanner responsável por ler o arquivo texto
        Scanner arquivo = null;
        int i, numProdutos;
        String linha;
        Produto produto;
        // Vetor temporário para armazenar apenas os produtos lidos
        Produto[] produtoCadastrados = new Produto[MAX_NOVOS_PRODUTOS];

        try{
            // Abre o arquivo de dados usando UTF-8
            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

            // Primeira linha contém a quantidade de produtos registrados
            numProdutos = Integer.parseInt(arquivo.nextLine());

            // Lê até numProdutos linhas ou até atingir o limite de MAX_NOVOS_PRODUTOS
            for(i=0;(i<numProdutos && i<MAX_NOVOS_PRODUTOS); i++){
                linha = arquivo.nextLine();
                produto = Produto.criarDoTexto(linha); // Converte a linha CSV em um objeto Produto
                produtoCadastrados[i] = produto;
            }
            // Atualiza o contador de produtos carregados
            quantosProdutos = i;

        }catch (IOException excecaoArquivo){
            // Em caso de erro na leitura, considera que nenhum produto foi carregado
            produtoCadastrados = null;
            quantosProdutos = 0;
        }finally{
            // Fecha o arquivo se ele tiver sido aberto
            if (arquivo != null) {
                arquivo.close();
            }
        }

        // Monta o vetor final de produtos, com espaço extra para novos cadastros
        Produto[] vetorProdutos;
        if (produtoCadastrados == null || quantosProdutos == 0) {
            vetorProdutos = new Produto[0];
        } else {
            vetorProdutos = new Produto[quantosProdutos + MAX_NOVOS_PRODUTOS];
            for (i = 0; i < quantosProdutos; i++) {
                vetorProdutos[i] = produtoCadastrados[i];
            }
        }
        return vetorProdutos;
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos(){
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < produtosCadastrados.length; i++) {
            if(produtosCadastrados[i]!=null)
                System.out.println(String.format("%02d - %s", (i+1),produtosCadastrados[i].toString()));
        }
    }

    /** Localiza um produto no vetor de cadastrados, a partir do nome, e imprime seus dados. 
     *  A busca não é sensível ao caso.  Em caso de não encontrar o produto, imprime mensagem padrão */
    static void localizarProdutos(){
        String descricao;
        ProdutoNaoPerecivel produtoALocalizar;
        Produto produto = null;
        boolean localizado = false;

        cabecalho();
        System.out.println("Informe a descrição do produto desejado");

        // Lê a descrição que o usuário deseja procurar
        descricao = teclado.nextLine();

        // Cria um objeto temporário com a descrição para reaproveitar o equals de Produto
        produtoALocalizar = new ProdutoNaoPerecivel(descricao, 0.01);

        // Percorre o vetor até encontrar um produto com a mesma descrição
        for(int i = 0;(i<quantosProdutos && !localizado); i++){
            if(produtosCadastrados[i].equals(produtoALocalizar)){
                localizado = true;
                produto = produtosCadastrados[i];
            }
        }
        if(!localizado){
            System.out.println("Produto não localizado!");
        }else{
            System.out.println(produto.toString());
        }
    }

    /**
     * Rotina de cadastro de um novo produto: pergunta ao usuário o tipo do produto, lê os dados correspondentes,
     * cria o objeto adequado de acordo com o tipo, inclui no vetor. Este método pode ser feito com um nível muito 
     * melhor de modularização. As diversas fases da lógica poderiam ser encapsuladas em outros métodos. 
     * Uma sugestão de melhoria mais significativa poderia ser o uso de padrão Factory Method para criação dos objetos.
     */
    static void cadastrarProduto(){
        cabecalho();
        System.out.println("Cadastro de novo produto");
        System.out.println("1 - Produto não perecível");
        System.out.println("2 - Produto perecível");
        System.out.print("Digite o tipo de produto: ");

        // Tipo escolhido pelo usuário (1 ou 2)
        int tipo = Integer.parseInt(teclado.nextLine());

        // Dados básicos comuns a qualquer produto
        System.out.print("Descrição: ");
        String descricao = teclado.nextLine();

        System.out.print("Preço de custo: ");
        double precoCusto = Double.parseDouble(teclado.nextLine().replace(",", "."));

        System.out.print("Margem de lucro (ex: 0.2 para 20%): ");
        double margemLucro = Double.parseDouble(teclado.nextLine().replace(",", "."));

        Produto novoProduto = null;

        // Escolhe a subclasse correta com base no tipo
        if (tipo == 1) {
            novoProduto = new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
        } else if (tipo == 2) {
            System.out.print("Data de validade (dd/MM/yyyy): ");
            String dataStr = teclado.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate validade = LocalDate.parse(dataStr, formatter);
            novoProduto = new ProdutoPerecivel(descricao, precoCusto, margemLucro, validade);
        } else {
            System.out.println("Tipo de produto inválido. Cadastro cancelado.");
            return;
        }

        // Inclui o novo produto no vetor se ainda houver espaço
        if (quantosProdutos < produtosCadastrados.length) {
            produtosCadastrados[quantosProdutos] = novoProduto;
            quantosProdutos++;
            System.out.println("Produto cadastrado com sucesso!");
        } else {
            System.out.println("Não há espaço para novos produtos.");
        }
    }

    /**
     * Salva os dados dos produtos cadastrados no arquivo csv informado. Sobrescreve todo o conteúdo do arquivo.
     * @param nomeArquivo Nome do arquivo a ser gravado.
     */
    public static void salvarProdutos(String nomeArquivo){
        FileWriter escritor = null;
        try{
            // Abre o arquivo de saída, sobrescrevendo o conteúdo anterior
            escritor = new FileWriter(nomeArquivo);

            // Grava primeiro a quantidade de produtos cadastrados
            escritor.write(Integer.toString(quantosProdutos));
            escritor.write(System.lineSeparator());

            // Grava uma linha por produto, usando o formato definido em gerarDadosTexto()
            for (int i = 0; i < quantosProdutos; i++) {
                if (produtosCadastrados[i] != null) {
                    escritor.write(produtosCadastrados[i].gerarDadosTexto());
                    escritor.write(System.lineSeparator());
                }
            }
        }catch(IOException e){
            System.out.println("Erro ao salvar produtos: " + e.getMessage());
        }finally{
            // Fecha o arquivo de escrita se ele tiver sido aberto
            if (escritor != null) {
                try {
                    escritor.close();
                } catch (IOException e) {
                    // erro ao fechar pode ser ignorado aqui
                }
            }
        }  
    }

    public static void main(String[] args) throws Exception {
        // Inicializa o scanner para leitura do teclado com o charset adequado
        teclado = new Scanner(System.in, Charset.forName("ISO-8859-2"));

        // Define o nome do arquivo de dados
        nomeArquivoDados = "dadosProdutos.csv";

        // Carrega os produtos existentes do arquivo
        produtosCadastrados = lerProdutos(nomeArquivoDados);

        int opcao = -1;
        // Loop principal da aplicação de console
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> localizarProdutos();
                case 3 -> cadastrarProduto();
            }
            pausa();
        }while(opcao !=0);       

        // Ao final, grava novamente o conteúdo no arquivo CSV
        salvarProdutos(nomeArquivoDados);
        teclado.close();    
    }
}
