# Trabalho de Tratamento de Exceção - Robôs Famintos 🤖
Jogo desenvolvido em Java (Swing) no qual robôs se movem em um tabuleiro cartesiano 6x6 até encontrar um alimento, tratando movimentos inválidos por meio de uma exceção customizada (`MovimentoInvalidoException`).

## Índice
- [Visão Geral](#visão-geral)
- [Instalação e Execução](#instalação-e-execução)
- [Funcionalidades](#funcionalidades)
- [Como Usar](#como-usar)
- [Estrutura](#estrutura)

## Visão Geral
- Menu Inicial animado (um robô pula rochas e bombas até chegar à maçã)
- Modo Você no comando (movimento manual)
- Modo Corrida maluca (dois robôs aleatórios)
- Modo Normal x Inteligente (`Robo` contra `RoboInteligente`)
- Modo Campo minado (bombas e rochas no caminho dos robôs)
- Tratamento de movimentos inválidos com `MovimentoInvalidoException`

## Instalação e Execução
**Requisitos**: JDK ≥ 17 | Windows / Linux / macOS

```text
cd TrabalhoExcecao

# Linux / macOS
javac -encoding UTF-8 -d bin Main.java $(find src -name "*.java")
java -cp bin Main
```

```text
# Windows (PowerShell)
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } > sources.txt
javac -encoding UTF-8 -d bin Main.java "@sources.txt"
java -cp bin Main
```

> Em IDEs (Eclipse / IntelliJ): marque `src/` e a raiz do projeto como *source root* e defina `Main` como classe principal.

## Funcionalidades
- Robô manual: mover com setas (`up`, `down`, `right`, `left`) até o alimento
- Corrida aleatória: robôs azul e vermelho se movem por sorteio até um encontrar o alimento
- Normal x Inteligente: o `RoboInteligente` não repete a direção que acabou de falhar
- Bombas e rochas: a `Bomba` desativa o robô e some do tabuleiro; a `Rocha` devolve o robô à posição anterior
- Histórico de jogadas, com movimentos inválidos destacados em vermelho (`[!]`)
- Tabuleiro com eixos, legenda e robôs animados em tempo real (`javax.swing.Timer`)

## Como usar
### Você no comando
> **Posição do Alimento**: escolhida pelas setas ao lado do tabuleiro<br>
> **Controle**: botões de seta para mover o robô azul a partir de (0, 0)<br>
> **Movimento fora do tabuleiro**: exceção tratada e registrada no histórico<br>

### Campo minado
> **Posição do Alimento**: (4, 3)<br>
> **Obstáculos**: + Bomba em (2, 2), + Rocha em (1, 3)<br>
> **Iniciar**: robô azul (normal) e robô verde (inteligente) saem de (0, 0)<br>
> **Fim de jogo**: um robô encontra o alimento ou os dois explodem<br>

## Estrutura

```text
TrabalhoExcecao/
├── Main.java
├── src/
│   ├── exceptions/
│   │   └── MovimentoInvalidoException.java
│   ├── model/
│   │   ├── Bomba.java
│   │   ├── Obstaculo.java
│   │   ├── Robo.java
│   │   ├── RoboInteligente.java
│   │   ├── Rocha.java
│   │   └── Tabuleiro.java
│   └── view/
│       ├── JanelaPrincipal.java
│       ├── components/
│       │   ├── Banner.java
│       │   ├── Botao.java
│       │   ├── Componentes.java
│       │   ├── Icones.java
│       │   ├── Legenda.java
│       │   ├── PainelLog.java
│       │   ├── PainelTabuleiro.java
│       │   └── SeletorPosicao.java
│       ├── screens/
│       │   ├── PainelBase.java
│       │   ├── PainelCorrida.java
│       │   ├── PainelInteligente.java
│       │   ├── PainelManual.java
│       │   ├── PainelObstaculos.java
│       │   └── TelaMenu.java
│       └── theme/
│           └── Tema.java
├── .gitignore
└── README.md
```

## Licença
UECE © 2026 *(preencher com os nomes da equipe)*
