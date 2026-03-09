package biblioteca;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Exemplo independente, apenas para estudo.
 *
 * Sistema simples de BIBLIOTECA com livros físicos e ebooks.
 * A ideia é treinar:
 * - uso de menu em console
 * - leitura e escrita em arquivo texto (CSV)
 * - uso de classe base abstrata + subclasses
 * - buscas em vetor
 */
public class Biblioteca {

    /** Quantidade máxima de novos livros aceitos por execução */
    static final int MAX_NOVOS_LIVROS = 10;

    /** Nome do arquivo de dados com os livros */
    static String nomeArquivoDados;

    /** Scanner para leitura do teclado */
    static Scanner teclado;

    /** Vetor de livros cadastrados (terá espaço extra para novos cadastros) */
    static Livro[] livrosCadastrados;

    /** Quantidade de livros efetivamente cadastrados no vetor */
    static int quantosLivros;

    /** Pausa de console: espera ENTER para continuar */
    static void pausa() {
        System.out.println("Digite ENTER para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho padrão da aplicação */
    static void cabecalho() {
        System.out.println("SISTEMA DE BIBLIOTECA");
        System.out.println("=====================");
    }

    /**
     * Mostra o menu principal e lê a opção do usuário.
     *
     * @return opção escolhida como inteiro
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os livros");
        System.out.println("2 - Procurar livro pelo título");
        System.out.println("3 - Cadastrar novo livro");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os dados de livros de um arquivo texto.
     *
     * Formato do arquivo:
     * N (quantidade de livros)
     * tipo;título;autor;ano;[tamanhoMB ou dataDeDevolucao]
     *
     * tipo 1 = livro físico (possui data prevista de devolução)
     * tipo 2 = ebook (possui tamanho em MB)
     */
    static Livro[] lerLivros(String nomeArquivoDados) {
        Scanner arquivo = null;
        int i, numLivros;
        String linha;
        Livro livro;
        Livro[] livrosTemporarios = new Livro[MAX_NOVOS_LIVROS];

        try {
            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

            // primeira linha: quantidade de livros gravados
            numLivros = Integer.parseInt(arquivo.nextLine());

            // lê até numLivros linhas (ou até atingir o limite do vetor temporário)
            for (i = 0; (i < numLivros && i < MAX_NOVOS_LIVROS); i++) {
                linha = arquivo.nextLine();
                livro = Livro.criarDoTexto(linha);
                livrosTemporarios[i] = livro;
            }
            quantosLivros = i;

        } catch (IOException e) {
            livrosTemporarios = null;
            quantosLivros = 0;
        } finally {
            if (arquivo != null) {
                arquivo.close();
            }
        }

        // monta vetor final com espaço para novos livros
        Livro[] vetorLivros;
        if (livrosTemporarios == null || quantosLivros == 0) {
            vetorLivros = new Livro[0];
        } else {
            vetorLivros = new Livro[quantosLivros + MAX_NOVOS_LIVROS];
            for (i = 0; i < quantosLivros; i++) {
                vetorLivros[i] = livrosTemporarios[i];
            }
        }
        return vetorLivros;
    }

    /** Lista todos os livros cadastrados, numerados, um por linha */
    static void listarTodosOsLivros() {
        cabecalho();
        System.out.println("\nLIVROS CADASTRADOS:");
        for (int i = 0; i < livrosCadastrados.length; i++) {
            if (livrosCadastrados[i] != null) {
                System.out.println(String.format("%02d - %s", (i + 1), livrosCadastrados[i].toString()));
            }
        }
    }

    /**
     * Procura um livro pelo título (ignora maiúsculas/minúsculas)
     * e mostra seus dados.
     */
    static void localizarLivro() {
        cabecalho();
        System.out.println("Informe o título do livro desejado:");
        String titulo = teclado.nextLine();

        // usa um "livro de busca" apenas com o título, para reaproveitar equals
        LivroFisico livroBusca = new LivroFisico(titulo, "AutorQualquer", 2000, java.time.LocalDate.now().plusDays(1));

        Livro encontrado = null;
        boolean localizado = false;

        for (int i = 0; (i < quantosLivros && !localizado); i++) {
            if (livrosCadastrados[i].equals(livroBusca)) {
                localizado = true;
                encontrado = livrosCadastrados[i];
            }
        }

        if (!localizado) {
            System.out.println("Livro não localizado!");
        } else {
            System.out.println(encontrado.toString());
        }
    }

    /**
     * Cadastra um novo livro (físico ou ebook) a partir de dados digitados.
     */
    static void cadastrarLivro() {
        cabecalho();
        System.out.println("Cadastro de novo livro");
        System.out.println("1 - Livro físico");
        System.out.println("2 - Ebook");
        System.out.print("Digite o tipo de livro: ");
        int tipo = Integer.parseInt(teclado.nextLine());

        System.out.print("Título: ");
        String titulo = teclado.nextLine();

        System.out.print("Autor: ");
        String autor = teclado.nextLine();

        System.out.print("Ano de publicação: ");
        int ano = Integer.parseInt(teclado.nextLine());

        Livro novoLivro = null;

        if (tipo == 1) {
            System.out.print("Data prevista de devolução (dd/MM/yyyy): ");
            String dataStr = teclado.nextLine();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.LocalDate dataDevolucao = java.time.LocalDate.parse(dataStr, formatter);
            novoLivro = new LivroFisico(titulo, autor, ano, dataDevolucao);
        } else if (tipo == 2) {
            System.out.print("Tamanho do arquivo (em MB): ");
            double tamanhoMB = Double.parseDouble(teclado.nextLine().replace(",", "."));
            novoLivro = new Ebook(titulo, autor, ano, tamanhoMB);
        } else {
            System.out.println("Tipo de livro inválido. Cadastro cancelado.");
            return;
        }

        if (quantosLivros < livrosCadastrados.length) {
            livrosCadastrados[quantosLivros] = novoLivro;
            quantosLivros++;
            System.out.println("Livro cadastrado com sucesso!");
        } else {
            System.out.println("Não há espaço para novos livros.");
        }
    }

    /**
     * Grava os livros cadastrados em um arquivo CSV.
     */
    static void salvarLivros(String nomeArquivo) {
        FileWriter escritor = null;
        try {
            escritor = new FileWriter(nomeArquivo);

            // primeira linha: quantidade de livros
            escritor.write(Integer.toString(quantosLivros));
            escritor.write(System.lineSeparator());

            // linhas seguintes: dados de cada livro
            for (int i = 0; i < quantosLivros; i++) {
                if (livrosCadastrados[i] != null) {
                    escritor.write(livrosCadastrados[i].gerarDadosTexto());
                    escritor.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar livros: " + e.getMessage());
        } finally {
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
        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "dadosLivros.csv";

        livrosCadastrados = lerLivros(nomeArquivoDados);

        int opcao;
        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsLivros();
                case 2 -> localizarLivro();
                case 3 -> cadastrarLivro();
            }
            if (opcao != 0) {
                pausa();
            }
        } while (opcao != 0);

        salvarLivros(nomeArquivoDados);
        teclado.close();
    }
}

