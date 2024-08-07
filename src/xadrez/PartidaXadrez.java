package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jogo.Peca;
import jogo.Posicao;
import jogo.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

//Definição das regras da partida
public class PartidaXadrez {

    private int turno;
    private Cor jogadorAtual;
    private Tabuleiro tabuleiro;
    private boolean check;
    private boolean checkmate;
    private PecaXadrez vulneravelEnPassant;
    private PecaXadrez promovida;

    private List<Peca> pecasTabuleiro = new ArrayList<>();
    private List<Peca> pecasCapturadas = new ArrayList<>();

    public PartidaXadrez() {
        tabuleiro = new Tabuleiro(8, 8);
        turno = 1;
        jogadorAtual = Cor.BRANCA;
        setup();
    }

    public int getTurno() {
        return turno;
    }

    public Cor getJogadorAtual() {
        return jogadorAtual;
    }

    public boolean getCheck() {
        return check;
    }

    public boolean getCheckmate() {
        return checkmate;
    }

    public PecaXadrez getVulneravelEnPassant() {
        return vulneravelEnPassant;
    }

    public PecaXadrez getPromovida() {
        return promovida;
    }

    public PecaXadrez[][] getPecas() {
        PecaXadrez[][] matriz = new PecaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];

        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                matriz[i][j] = (PecaXadrez) tabuleiro.getPeca(i, j);
            }
        }

        return matriz;
    }

    //Método para permitir a impressão na aplicação dos possíveis destinos da peça
    public boolean[][] movimentosPossiveis(PosicaoXadrez origem) {
        Posicao p = origem.toPosicao();
        validarOrigem(p);
        return tabuleiro.getPeca(p).movimentosPossiveis();
    }

    public PecaXadrez fazerMovimentoXadrez(PosicaoXadrez origem, PosicaoXadrez destino) {
        Posicao orig = origem.toPosicao();
        Posicao dest = destino.toPosicao();

        validarOrigem(orig);
        validarDestino(orig, dest);
        Peca pecaCapturada = movimentar(orig, dest);

        if (testarCheck(jogadorAtual)) {
            desfazerMovimento(orig, dest, pecaCapturada);
            throw new ExcecaoXadrez("O movimento efetuada causa check no Rei");
        }

        PecaXadrez pecaMovimentada = (PecaXadrez) tabuleiro.getPeca(dest);

        //Promoção
        promovida = null;
        if(pecaMovimentada instanceof Peao)
        {
            if((pecaMovimentada.getCor() == Cor.BRANCA && dest.getLinha() == 0) || (pecaMovimentada.getCor() == Cor.PRETA && dest.getLinha() == 7))
            {
                promovida = (PecaXadrez)tabuleiro.getPeca(dest);
                promovida = trocarPecaPromovida("Q");
            }
        }
        
        check = (testarCheck(verificaOponente(jogadorAtual))) ? true : false;

        if (testarCheckmate(verificaOponente(jogadorAtual))) {
            checkmate = true;
        } else {
            proximoTurno();
        }

        //En Passant
        if (pecaMovimentada instanceof Peao && (dest.getLinha() == orig.getLinha() - 2) || dest.getLinha() == orig.getLinha() + 2) {
            vulneravelEnPassant = pecaMovimentada;
        } else {
            vulneravelEnPassant = null;
        }

        return (PecaXadrez) pecaCapturada;
    }
    
    public PecaXadrez trocarPecaPromovida(String tipo)
    {
        if (promovida == null)
        {
            throw new IllegalStateException("Nao ha peca para ser promovida");
        }
        if (!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("Q"))
        {
            return promovida;
        }
        Posicao posicao = promovida.getPosicaoXadrez().toPosicao();
        Peca peca = tabuleiro.removerPeca(posicao);
        pecasTabuleiro.remove(peca);
        
        PecaXadrez novaPeca = novaPeca(tipo, promovida.getCor());
        tabuleiro.colocarPeca(novaPeca, posicao);
        pecasTabuleiro.add(novaPeca);
        
        return novaPeca;
    }
    
    private PecaXadrez novaPeca(String tipo, Cor cor)
    {
        if(tipo.equals("Q")) return new Rainha(cor, tabuleiro);
        if(tipo.equals("C")) return new Cavalo(cor, tabuleiro);
        if(tipo.equals("T")) return new Torre(cor, tabuleiro);
        return new Bispo(cor, tabuleiro);
    }

    private Peca movimentar(Posicao origem, Posicao destino) {
        PecaXadrez p = (PecaXadrez) tabuleiro.removerPeca(origem);
        p.incrementarQtdMovimentos();
        Peca pecaCapturada = tabuleiro.removerPeca(destino);

        tabuleiro.colocarPeca(p, destino);

        if (pecaCapturada != null) {
            pecasTabuleiro.remove(pecaCapturada);
            pecasCapturadas.add(pecaCapturada);
        }

        //Roque do lado do Rei
        if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
            Posicao origemTorre = new Posicao(origem.getLinha(), origem.getColuna() + 3);
            Posicao destinoTorre = new Posicao(origem.getLinha(), origem.getColuna() + 1);
            PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(origemTorre);
            tabuleiro.colocarPeca(torre, destinoTorre);
            torre.incrementarQtdMovimentos();
        }

        //Roque do lado da Rainha
        if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
            Posicao origemTorre = new Posicao(origem.getLinha(), origem.getColuna() - 4);
            Posicao destinoTorre = new Posicao(origem.getLinha(), origem.getColuna() - 1);
            PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(origemTorre);
            tabuleiro.colocarPeca(torre, destinoTorre);
            torre.incrementarQtdMovimentos();
        }

        //En Passant
        if (p instanceof Peao) {
            if (origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
                Posicao posicaoPeao;
                if (p.getCor() == Cor.BRANCA)
                {
                    posicaoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
                }
                else
                {
                    posicaoPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
                }
                pecaCapturada = tabuleiro.removerPeca(posicaoPeao);
                pecasCapturadas.add(pecaCapturada);
                pecasTabuleiro.remove(pecaCapturada);
            }
        }

        return pecaCapturada;
    }

    //Previne o xeque causado pelo próprio jogador, desfazendo a jogada
    private void desfazerMovimento(Posicao origem, Posicao destino, Peca capturada) {
        PecaXadrez p = (PecaXadrez) tabuleiro.removerPeca(destino);
        p.decrementarQtdMovimentos();
        tabuleiro.colocarPeca(p, origem);

        if (capturada != null) {
            tabuleiro.colocarPeca(capturada, destino);
            pecasCapturadas.remove(capturada);
            pecasTabuleiro.add(capturada);
        }

        //Roque do lado do Rei
        if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
            Posicao origemTorre = new Posicao(origem.getLinha(), origem.getColuna() + 3);
            Posicao destinoTorre = new Posicao(origem.getLinha(), origem.getColuna() + 1);
            PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(destinoTorre);
            tabuleiro.colocarPeca(torre, origemTorre);
            torre.decrementarQtdMovimentos();
        }

        //Roque do lado da Rainha
        if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
            Posicao origemTorre = new Posicao(origem.getLinha(), origem.getColuna() - 4);
            Posicao destinoTorre = new Posicao(origem.getLinha(), origem.getColuna() - 1);
            PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(destinoTorre);
            tabuleiro.colocarPeca(torre, origemTorre);
            torre.decrementarQtdMovimentos();
        }
        
        //En Passant
        if (p instanceof Peao) {
            if (origem.getColuna() != destino.getColuna() && capturada == vulneravelEnPassant) {
                PecaXadrez peao = (PecaXadrez)tabuleiro.removerPeca(destino);
                Posicao posicaoPeao;
                if (p.getCor() == Cor.BRANCA)
                {
                    posicaoPeao = new Posicao(3, destino.getColuna());
                }
                else
                {
                    posicaoPeao = new Posicao(4, destino.getColuna());
                }
                tabuleiro.colocarPeca(peao, posicaoPeao);
            }
        }
    }

    private void validarOrigem(Posicao posicao) {
        if (!tabuleiro.existePeca(posicao)) {
            throw new ExcecaoXadrez("Nao existe peca na posicao de origem");
        }
        if (jogadorAtual != ((PecaXadrez) tabuleiro.getPeca(posicao)).getCor()) {
            throw new ExcecaoXadrez("A peca escolhida nao e do jogador atual");
        }
        if (!tabuleiro.getPeca(posicao).isAlgumMovimentoPossivel()) {
            throw new ExcecaoXadrez("Nao existem movimentos possiveis para a peca escolhida");
        }
    }

    private void validarDestino(Posicao origem, Posicao destino) {
        if (!tabuleiro.getPeca(origem).movimentoPossivel(destino)) {
            throw new ExcecaoXadrez("A peca escolhida nao pode ser movida para a posicao de destino");
        }
    }

    private void proximoTurno() {
        turno++;
        jogadorAtual = (jogadorAtual == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
    }

    private Cor verificaOponente(Cor cor) {
        return (cor == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
    }

    private PecaXadrez localizaRei(Cor cor) {
        List<Peca> lista = pecasTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor).collect(Collectors.toList());
        for (Peca p : lista) {
            if (p instanceof Rei) {
                return (PecaXadrez) p;
            }
        }
        throw new IllegalStateException("Nao existe rei da cor " + cor + " no tabuleiro");
    }

    private boolean testarCheck(Cor cor) {
        Posicao posicaoRei = localizaRei(cor).getPosicaoXadrez().toPosicao();
        List<Peca> pecasOponente = pecasTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == verificaOponente(cor)).collect(Collectors.toList());

        for (Peca p : pecasOponente) {
            boolean[][] matriz = p.movimentosPossiveis();
            if (matriz[posicaoRei.getLinha()][posicaoRei.getColuna()]) //O rei está na casa aonde estão os movimentos possíveis do adversário?
            {
                return true;
            }
        }

        return false;
    }

    private boolean testarCheckmate(Cor cor) {
        if (!testarCheck(cor)) {
            return false;
        }
        List<Peca> lista = pecasTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor).collect(Collectors.toList());

        for (Peca p : lista) {
            boolean[][] matriz = p.movimentosPossiveis();
            for (int i = 0; i < tabuleiro.getLinhas(); i++) {
                for (int j = 0; j < tabuleiro.getColunas(); j++) {
                    if (matriz[i][j]) {
                        Posicao origem = ((PecaXadrez) p).getPosicaoXadrez().toPosicao();
                        Posicao destino = new Posicao(i, j);
                        Peca capturada = movimentar(origem, destino);

                        //Moveu a peça da origem ao destino. Ainda está em cheque?
                        boolean testarCheck = testarCheck(cor);
                        desfazerMovimento(origem, destino, capturada);
                        if (!testarCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    //Passando nas coordenadas de xadrez ao invés de na posição da matriz
    private void colocarNovaPeca(char coluna, int linha, PecaXadrez peca) {
        tabuleiro.colocarPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
        pecasTabuleiro.add(peca);
    }

    //Inicialização da partida, colocando as peças no tabuleiro
    private void setup() {

        //BRANCAS
        colocarNovaPeca('a', 2, new Peao(Cor.BRANCA, tabuleiro, this));
        colocarNovaPeca('b', 2, new Peao(Cor.BRANCA, tabuleiro, this));
        colocarNovaPeca('c', 2, new Peao(Cor.BRANCA, tabuleiro, this));
        colocarNovaPeca('d', 2, new Peao(Cor.BRANCA, tabuleiro, this));
        colocarNovaPeca('e', 2, new Peao(Cor.BRANCA, tabuleiro, this));
        colocarNovaPeca('f', 2, new Peao(Cor.BRANCA, tabuleiro, this));
        colocarNovaPeca('g', 2, new Peao(Cor.BRANCA, tabuleiro, this));
        colocarNovaPeca('h', 2, new Peao(Cor.BRANCA, tabuleiro, this));

        colocarNovaPeca('a', 1, new Torre(Cor.BRANCA, tabuleiro));
        colocarNovaPeca('h', 1, new Torre(Cor.BRANCA, tabuleiro));

        colocarNovaPeca('b', 1, new Cavalo(Cor.BRANCA, tabuleiro));
        colocarNovaPeca('g', 1, new Cavalo(Cor.BRANCA, tabuleiro));

        colocarNovaPeca('c', 1, new Bispo(Cor.BRANCA, tabuleiro));
        colocarNovaPeca('f', 1, new Bispo(Cor.BRANCA, tabuleiro));

        colocarNovaPeca('d', 1, new Rainha(Cor.BRANCA, tabuleiro));

        colocarNovaPeca('e', 1, new Rei(Cor.BRANCA, tabuleiro, this));

        //PRETAS
        colocarNovaPeca('a', 7, new Peao(Cor.PRETA, tabuleiro, this));
        colocarNovaPeca('b', 7, new Peao(Cor.PRETA, tabuleiro, this));
        colocarNovaPeca('c', 7, new Peao(Cor.PRETA, tabuleiro, this));
        colocarNovaPeca('d', 7, new Peao(Cor.PRETA, tabuleiro, this));
        colocarNovaPeca('e', 7, new Peao(Cor.PRETA, tabuleiro, this));
        colocarNovaPeca('f', 7, new Peao(Cor.PRETA, tabuleiro, this));
        colocarNovaPeca('g', 7, new Peao(Cor.PRETA, tabuleiro, this));
        colocarNovaPeca('h', 7, new Peao(Cor.PRETA, tabuleiro, this));

        colocarNovaPeca('a', 8, new Torre(Cor.PRETA, tabuleiro));
        colocarNovaPeca('h', 8, new Torre(Cor.PRETA, tabuleiro));

        colocarNovaPeca('b', 8, new Cavalo(Cor.PRETA, tabuleiro));
        colocarNovaPeca('g', 8, new Cavalo(Cor.PRETA, tabuleiro));

        colocarNovaPeca('c', 8, new Bispo(Cor.PRETA, tabuleiro));
        colocarNovaPeca('f', 8, new Bispo(Cor.PRETA, tabuleiro));

        colocarNovaPeca('d', 8, new Rainha(Cor.PRETA, tabuleiro));

        colocarNovaPeca('e', 8, new Rei(Cor.PRETA, tabuleiro, this));
    }
}
