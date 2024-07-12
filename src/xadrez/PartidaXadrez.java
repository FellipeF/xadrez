package xadrez;

import jogo.Peca;
import jogo.Posicao;
import jogo.Tabuleiro;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

//Definição das regras da partida

public class PartidaXadrez {
    private Tabuleiro tabuleiro;

    public PartidaXadrez()
    {
        tabuleiro = new Tabuleiro(8,8);
        setup();
    }
    
    public PecaXadrez[][] getPecas()
    {
        PecaXadrez[][] matriz = new PecaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
        
        for (int i = 0; i < tabuleiro.getLinhas(); i++)
        {
            for (int j = 0; j < tabuleiro.getColunas(); j++)
            {
                matriz[i][j] = (PecaXadrez) tabuleiro.getPeca(i, j);
            }
        }
        
        return matriz;
    }
    
    public PecaXadrez fazerMovimentoXadrez(PosicaoXadrez origem, PosicaoXadrez destino)
    {
        Posicao orig = origem.toPosicao();
        Posicao dest = destino.toPosicao();
        
        validarOrigem(orig);
        Peca pecaCapturada = movimentar(orig, dest);
        
        return (PecaXadrez) pecaCapturada;
    }
    
    private Peca movimentar(Posicao origem, Posicao destino)
    {
        Peca p = tabuleiro.removerPeca(origem);
        Peca pecaCapturada = tabuleiro.removerPeca(destino);
        
        tabuleiro.colocarPeca(p, destino);
        
        return pecaCapturada;
    }
    
    private void validarOrigem(Posicao posicao)
    {
        if (!tabuleiro.existePeca(posicao))
        {
            throw new ExcecaoXadrez("Nao existe peca na posicao de origem");
        }
    }
    
    //Passando nas coordenadas de xadrez ao invés de na posição da matriz
    private void colocarNovaPeca(char coluna, int linha, PecaXadrez peca)
    {
        tabuleiro.colocarPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
    }
    
    //Inicialização da partida, colocando as peças no tabuleiro
    private void setup()
    {
        colocarNovaPeca('a', 1, new Torre(Cor.BRANCA, tabuleiro));
        colocarNovaPeca('h', 1, new Torre(Cor.BRANCA, tabuleiro));
        colocarNovaPeca('e', 1, new Rei(Cor.BRANCA, tabuleiro));
        
        colocarNovaPeca('a', 8, new Torre(Cor.PRETA, tabuleiro));
        colocarNovaPeca('h', 8, new Torre(Cor.PRETA, tabuleiro));
        colocarNovaPeca('e', 8, new Rei(Cor.PRETA, tabuleiro)); 
    }
}