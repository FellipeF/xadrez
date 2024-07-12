package jogo;

public class Tabuleiro {

    private int linhas;
    private int colunas;
    private Peca[][] pecas;

    public Tabuleiro(int linhas, int colunas) {
        if (linhas < 1 || colunas < 1) {
            throw new ExcecaoTabuleiro("Erro criando o tabuleiro: Necessario que haja ao menos 1 linha e 1 coluna");
        }
        this.linhas = linhas;
        this.colunas = colunas;
        pecas = new Peca[linhas][colunas];
    }

    public int getLinhas() {
        return linhas;
    }

    public int getColunas() {
        return colunas;
    }

    public Peca getPeca(int linha, int coluna) {
        if (!existePosicao(linha, coluna)) {
            throw new ExcecaoTabuleiro("Posicao nao esta no tabuleiro");
        }
        return pecas[linha][coluna];
    }

    public Peca getPeca(Posicao posicao) {
        if (!existePosicao(posicao)) {
            throw new ExcecaoTabuleiro("Posicao nao esta no tabuleiro");
        }
        return pecas[posicao.getLinha()][posicao.getColuna()];
    }

    public void colocarPeca(Peca peca, Posicao posicao) {
        if (existePeca(posicao)) {
            throw new ExcecaoTabuleiro("Ja existe uma peca na posicao " + posicao);
        }
        pecas[posicao.getLinha()][posicao.getColuna()] = peca;
        peca.posicao = posicao;
    }

    private boolean existePosicao(int linha, int coluna) {
        return linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas;
    }

    public boolean existePosicao(Posicao posicao) {
        return existePosicao(posicao.getLinha(), posicao.getColuna());
    }

    public boolean existePeca(Posicao posicao) {
        if (!existePosicao(posicao)) {
            throw new ExcecaoTabuleiro("Posicao nao esta no tabuleiro");
        }
        return getPeca(posicao) != null;
    }
}
