import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe base abstrata para livros da biblioteca.
 * Segue o mesmo padrão de estrutura de {@link Produto}.
 */
public abstract class Livro {

    /** Título do livro */
    protected String titulo;

    /** Autor do livro */
    protected String autor;

    /** Ano de publicação do livro */
    protected int anoPublicacao;

    /**
     * Construtor básico de livro.
     *
     * @param titulo         título do livro
     * @param autor          autor do livro
     * @param anoPublicacao  ano em que o livro foi publicado
     */
    public Livro(String titulo, String autor, int anoPublicacao) {
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
    }

    /**
     * Dois livros são considerados iguais se tiverem o mesmo título
     * (ignorando maiúsculas/minúsculas), semelhante ao equals de Produto.
     */
    @Override
    public boolean equals(Object obj) {
        Livro outro = (Livro) obj;
        return this.titulo.toLowerCase().equals(outro.titulo.toLowerCase());
    }

    /** Representação em texto padrão de um livro */
    @Override
    public String toString() {
        return String.format("Título: %s\nAutor: %s\nAno: %d", titulo, autor, anoPublicacao);
    }

    /**
     * Gera uma linha de texto com os dados do livro no formato:
     * tipo;título;autor;ano;[campoExtra]
     */
    public abstract String gerarDadosTexto();

    /**
     * Cria um Livro (fisico ou ebook) a partir de uma linha CSV no formato:
     * tipo;título;autor;ano;[campoExtra]
     */
    public static Livro criarDoTexto(String linha) {
        String[] partes = linha.split(";");
        int tipo = Integer.parseInt(partes[0]);  // 1 físico, 2 ebook
        String titulo = partes[1];
        String autor = partes[2];
        int ano = Integer.parseInt(partes[3]);

        if (tipo == 1) {
            // livro físico: campo 4 é data de devolução
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataDevolucao = LocalDate.parse(partes[4], formatter);
            return new LivroFisico(titulo, autor, ano, dataDevolucao);
        } else {
            // ebook: campo 4 é tamanho em MB
            double tamanhoMB = Double.parseDouble(partes[4]);
            return new Ebook(titulo, autor, ano, tamanhoMB);
        }
    }
}

