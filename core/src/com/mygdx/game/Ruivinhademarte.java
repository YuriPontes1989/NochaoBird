package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class Ruivinhademarte extends ApplicationAdapter {
	// armazena a textura
	private  SpriteBatch batch;
	private  Texture[] passaros;
	private  Texture fundo;
	private  Texture  canoBaixo;
	private  Texture canoTopo;
	private  Texture gameOver;
	private Texture[] coin;
	// colisão dos objetos
	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private  Rectangle retanguloCanoBaixo;
	private Circle circuloCoin;
	// configuração da tela do dispositivo, posição dos canos   e pontuações
	private float posicaoCoinHorizontal;
	private float posicaoCoinVertical;
	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade =2;
	private float posicaoInicialVerticalPassaro=0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVerical;
	private float espacoEntreCanos;
	private boolean coletouCoin = false;
	private boolean colidiuCoin = false;
	private int coinType;
	private Random random;
	private  int pontos=0;
	private int pontuacaoMaxima=0;
	private boolean passouCano=false;
	private  int estadoJogo=0;
	private float posicaoHorizontalPassaro;
	// utilização de texto da interface
	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;
	// armazena os sons
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somCoin;
	// armazena a pontuação na memoria do celular
	Preferences preferencias;
	// uma camera e largura e altura
	private OrthographicCamera camera;
	private Viewport viewport;
	private  final  float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGHT = 1280;

	// inicializa todos os objetos  e texturas
	@Override
	public void create () {
		inicializarTexturas();
		inicializarObjetos();

	}

	// roda o frama
	@Override
	public void  render() {
		//  quando os assets saiem do alcance da camera ele automaticamente limpa
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		// desenha os objetos  e interface do jogo
		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();

	}
	// pega referencia do inicilizar texturas e inicia a animaçãp do passaro
	private void  inicializarTexturas(){
		passaros = new Texture[3];
		passaros[0] = new Texture("AngryBird1.png");
		passaros[1] = new Texture("AngryBird2.png");
		passaros[2] = new Texture("AngryBird3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");

		coin = new Texture[2];
		coin[0] = new Texture("SilverCoin.png");
		coin[1] = new Texture("GoldCoin.png");
	}
	 // instacia objetos e  atribui valores a ele além de iniciar as variaveis
	private void inicializarObjetos(){
	// classes de utilidades
		batch = new SpriteBatch();
		random = new Random();
	// irá definar o valor do tamanho da tela do dispositivo, além de dar valor inicial das posições do objeto
		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
		posicaoCanoHorizontal = larguraDispositivo;
		posicaoCoinVertical = alturaDispositivo / 2;
		posicaoCoinHorizontal = larguraDispositivo * 1.5f + canoBaixo.getWidth();
		espacoEntreCanos = 350;
	// cria o texto de pontuação atribuindo a ele um  cor e um tamanho
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);
	// cria o texto de recomeçar define a cor verde a ele e um tamanho
		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);
	//cria o texto de melhor pontuação atribui a cor vermelho a ele e seta o tamanho
		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);
		textoMelhorPontuacao.getData().setScale(2);
	// inicializa as colisoes dos objetos
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoCima = new Rectangle();
		circuloCoin = new Circle();
	// pega as referencias do som
		somVoando = Gdx.audio.newSound( Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound( Gdx.files.internal("som_pontos.wav"));
		somCoin = Gdx.audio.newSound(Gdx.files.internal("coin.wav"));
	// pega as referencias guardadas na memoria do celular junto com a pontuação
		preferencias = Gdx.app.getPreferences("FlappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);
	// inicializa a camera de acordo com o tamanho da tela
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2,0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}
	// roda o jogo depenendo do estado que o jogo se encontra
	private void verificarEstadoJogo(){
		// detecta o toque na tela
		boolean toqueTela = Gdx.input.justTouched();
		// se o usuário tocar na tela o jogo se inicia
		if ( estadoJogo == 0){
			if ( toqueTela){
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}
			// roda  o jogo
		}else if ( estadoJogo == 1){
			if (toqueTela){
				gravidade = -15;
				somVoando.play();
			}
			// move os elementos do jogo
			posicaoCanoHorizontal -=Gdx.graphics.getDeltaTime() * 200;
			posicaoCoinHorizontal -= Gdx.graphics.getDeltaTime() * 500;

			// quando o cano sai de visão da camera ele reseta a posição horizontal e  seta uma posiçap randomica além de resetar o valor da variavel
			if ( posicaoCanoHorizontal < -canoTopo.getWidth() ){
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCanoVerical = random.nextInt(400) - 200;
				passouCano = false;
			}
			// Se a moeda sair da tela, volta ela para a direita (como se instanciasse outra), define uma altura random e seta a váriavel "coletouCoin" para falso.
			if (posicaoCoinHorizontal < -coin[0].getWidth() * 3){
				posicaoCoinHorizontal = larguraDispositivo;
				posicaoCoinVertical = random.nextInt((int)alturaDispositivo - 300) + 150;
				coletouCoin = false;
				coinType = random.nextInt(2);
			}
			// quando o usario toca o passaro pula e faz a gravidade funcionar
			if ( posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
			gravidade++;
			// tela de game over
		}else if ( estadoJogo == 2){
			if ( pontos > pontuacaoMaxima){
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
				preferencias.flush();
			}
			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime()*500;
			// reseta o jogo
			if ( toqueTela ){
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo /2;
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCoinHorizontal = larguraDispositivo * 1.5f + canoBaixo.getWidth();
				posicaoCoinVertical = alturaDispositivo / 2;
			}
		}
	}
	// gere os colisores e detecta as colisoes
	private void detectarColisoes(){
		// cria a colisão do passaro
		circuloPassaro.set(
				50 + posicaoCanoHorizontal + passaros[0].getWidth() / 2,
				posicaoInicialVerticalPassaro + passaros [0].getHeight() /2,
				passaros[0].getWidth() /2
		);
		// cria e posiciona o colisor dos canos de baixo
		retanguloCanoBaixo.set(
				posicaoCanoHorizontal,
				alturaDispositivo / 2 -canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVerical,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);
		// cria e posiciona o colisdor dos canos de cima
		retanguloCanoCima.set(
				posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVerical,
				canoTopo.getWidth(), canoTopo.getHeight()
		);
		circuloCoin.set(posicaoCoinHorizontal, posicaoCoinVertical, coin[0].getWidth());
		//detecta as colisões
		boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
		colidiuCoin = Intersector.overlaps(circuloPassaro, circuloCoin);
		// se colidiu  da a tela de game over
		if (colidiuCanoCima || colidiuCanoBaixo){
			if (estadoJogo == 1){
				somColisao.play();
				estadoJogo = 2;
			}
		}


	}
	//desenha as texturas do jogo
	private void  desenharTexturas(){
		// traz as coordenadas do mundo para as coordenadas da tela do dispositivo
		batch.setProjectionMatrix(camera.combined);
		// permite que desenhe objetos na tela
		batch.begin();
		batch.draw(fundo,0,0,larguraDispositivo,alturaDispositivo);
		batch.draw(passaros[ (int) variacao],
				50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);
		// desenha cano de baixo
		batch.draw(canoBaixo, posicaoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos/2 + posicaoCanoVerical);
		// desenha o cano de cima
		batch.draw(canoTopo,posicaoCanoHorizontal,
				alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVerical);
		// Desenha a moeda na posição X e Y.
		batch.draw(coin[coinType], posicaoCoinHorizontal, posicaoCoinVertical);
		textoPontuacao.draw(batch, String.valueOf(pontos),larguraDispositivo/2,
				alturaDispositivo - 110);


		if (estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo/ 2 - gameOver.getWidth()/2,
					alturaDispositivo / 2);
			textoReiniciar.draw(batch,
					"toque para reiniciar!", larguraDispositivo/2 -140,
					alturaDispositivo/2 - gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch,
					"seu recorde é: " + pontuacaoMaxima + "pontos",
					larguraDispositivo/2 - 140, alturaDispositivo/2 - gameOver.getHeight());
		}
		batch.end();
	}
	// mostra ao usario a pontuação caso ele tenha passado por canos
	public  void  validarPontos(){

		if (posicaoCanoHorizontal < 50-passaros[0].getWidth()){
			if (!passouCano){
				pontos++;
				passouCano = true;
				somPontuacao.play();
			}
		}
		// Se colidiu com a moeda e nao coletou ela:
		if(colidiuCoin && !coletouCoin){
			// Dependendo do tipo da moeda, adiciona uma quantidade de pontos diferentes, toca um som e reseta as variáveis da moeda para a direita.
			coletouCoin = true;
			colidiuCoin = false;
			somCoin.play();
			switch (coinType){
				case 0:
					pontos += 5;
					break;
				case 1:
					pontos += 10;
					break;
			}
			posicaoCoinHorizontal = posicaoCoinHorizontal * 1.5f + larguraDispositivo;
			posicaoCoinVertical = random.nextInt((int)alturaDispositivo - 300) + 150;
			coletouCoin = false;
			coinType = random.nextInt(2);
		}

		variacao += Gdx.graphics.getDeltaTime()  * 10;

		if (variacao > 3)
			variacao = 0;
	}
	// dá um resize  no tamnho da tela do jogo
	@Override
	public  void resize(int width, int height){
		viewport.update(width, height);
	}

	@Override
	public  void dispose (){

	}
}
