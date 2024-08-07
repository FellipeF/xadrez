package xadrez;

import jogo.Peca;
import jogo.Posicao;
import jogo.Tabuleiro;

//A implementação do movimento é realizada nas peças específicas

public abstract class PecaXadrez extends Peca{
    
    private Cor cor;
    private int qtdMovimentos;

    public PecaXadrez(Tabuleiro tabuleiro) {
        super(tabuleiro);
    }

    public PecaXadrez(Cor cor, Tabuleiro tabuleiro) {
        super(tabuleiro);
        this.cor = cor;
    }

    public Cor getCor() {
        return cor;
    }
    
    public int getQtdMovimentos()
    {
        return qtdMovimentos;
    }
    
    protected void incrementarQtdMovimentos()
    {
        qtdMovimentos++;
    }
    
    protected void decrementarQtdMovimentos()
    {
        qtdMovimentos--;
    }
    
    public PosicaoXadrez getPosicaoXadrez()
    {
        return PosicaoXadrez.fromPosicao(posicao);
    }
    
    //Há uma peça do oponente no local aonde está se movendo?
    protected boolean isPecaOponenteLocal(Posicao posicao)
    {
        PecaXadrez p = (PecaXadrez)getTabuleiro().getPeca(posicao);
        return p != null && p.getCor() != cor;
    }
}
