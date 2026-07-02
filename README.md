[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/AR7CADm8)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=23400245)
# Plataforma de Memorização para Estudantes

<p align="center">
  <img src="https://github.com/user-attachments/assets/6a53b89d-6007-4833-9118-ade2e4388a7e" width="100%"/>
</p>
Acesse as telas do projeto no Figma: https://www.figma.com/design/Cfivm9YhSHqVLeQbkaYKIg/MemoBrain?node-id=5-2&t=dk5BeQFGYttWYBGj-1

![Status do Projeto](https://img.shields.io/badge/Status-Em_Desenvolvimento-blue)
![Curso](https://img.shields.io/badge/Desenvolvimento-Mobile-orange)

## 📝 Descrição
Aplicação mobile inspirada em ferramentas de flashcards, mas com múltiplos formatos de exercícios para otimizar a revisão de conteúdos. O projeto foca em ir além do modelo tradicional de "frente e verso", oferecendo uma experiência de estudo ativo completa.

---

## 👥 Equipe
| Nome | Matrícula | Curso |
| :--- | :---: | :--- |
| **Guilherme Oliveira De Lima** | 558311 | Redes de Computadores |
| **Allyson Augusto Freire Novaes** | 553353 | Redes de Computadores |
| **Thiago Euclides Melo** | 556598 | Redes de Computadores |
| **Wiliene do Nascimento Silva** | 552362 | Redes de Computadores |
| **Paulo Henrique H. Silveira** | 553591 | Redes de Computadores |

---

## 🎯 Objetivos e Impacto

### **Objetivo Geral**
Desenvolver uma plataforma dinâmica, para auxiliar estudantes no processo de memorização e revisão de conteúdos para provas, vestibulares e concursos públicos.

### **Público-Alvo**
* **Concurseiros:**
* **Estudantes Universitários:**
* **Estudantes de Ensino Médio e ENEM:** 

### **Impacto Esperado**
Através da diversificação de exercícios, espera-se uma melhora significativa na retenção de conteúdo e no desempenho acadêmico dos usuários.

---

## 🚀 Funcionalidades da Aplicação

### **Tipos de Exercícios e Atividades**
A plataforma oferece uma vasta gama de ferramentas para diferentes estilos de aprendizagem:

* **Flashcards e Questões:** Pergunta e resposta, múltipla escolha e respostas abertas (digitadas).
* **Exercícios Interativos:** Completar texto, arrastar e soltar, e ordenação de conceitos.
* **Estudo Visual:** Oclusão de imagem (esconder partes de diagramas, mapas ou anatomia).
* **Organização Avançada:** * Associação por categorias e fases.
    * Linha do tempo (ordenação cronológica).
    * Preenchimento de quadros comparativos (escolha por linha ou célula).
    * Seleção de itens corretos.

### **Gestão de Estudo**
* Organização de conteúdos em **cadernos de estudo**.
* Acompanhamento detalhado de **progresso e desempenho**.

## Organização de Arquivos e Telas

```text
MemoBrain/
├── app/
│   └── src/main/java/com/memobrain/memonow/
│
│       ├── MainActivity.kt
│       │   └── Ponto de entrada do aplicativo. Configura o tema e inicia a navegação.
│
│       ├── navegacao/
│       │   ├── AppNavegacao.kt
│       │   │   └── Controla o fluxo entre as telas do aplicativo.
│       │   └── rotas_telas.kt
│       │       └── Define as rotas usadas pela navegação.
│
│       ├── data/
│       │   ├── local/
│       │   │   └── datastore/
│       │   │       ├── ArmazenamentoSessao.kt
│       │   │       │   └── Salva localmente UID e e-mail da sessão atual.
│       │   │       └── PreferenciasUsuario.kt
│       │   │           └── Reservado para preferências do usuário, como tema e notificações.
│       │   │
│       │   ├── remote/
│       │   │   ├── autenticacao/
│       │   │   │   ├── GerenciadorAutenticacao.kt
│       │   │   │   │   └── Centraliza funções gerais de autenticação.
│       │   │   │   ├── ServicoCadastroFirebase.kt
│       │   │   │   │   └── Cria usuários no Firebase Authentication e salva o perfil no Firestore.
│       │   │   │   └── ServicoLoginFirebase.kt
│       │   │   │       └── Realiza login, logout e consulta o usuário autenticado.
│       │   │   │
│       │   │   └── firestore/
│       │   │       ├── FonteDadosFirestoreCaderno.kt
│       │   │       │   └── Comunicação direta com a coleção de cadernos.
│       │   │       ├── FonteDadosFirestoreArquivo.kt
│       │   │       │   └── Comunicação direta com os arquivos de cada caderno.
│       │   │       ├── FonteDadosFirestoreConteudo.kt
│       │   │       │   └── Gerencia flashcards e questões de múltipla escolha.
│       │   │       └── FonteDadosFirestoreHistorico.kt
│       │   │           └── Salva e consulta o histórico de revisões.
│       │   │
│       │   └── repository/
│       │       └── repositorio/
│       │           ├── RepositorioCaderno.kt
│       │           ├── RepositorioArquivo.kt
│       │           ├── RepositorioConteudo.kt
│       │           └── RepositorioHistorico.kt
│       │
│       │           └── Faz a ponte entre as telas/ViewModels e o Firestore.
│
│       ├── features/
│       │   ├── login/
│       │   │   ├── TelaInicial.kt
│       │   │   │   └── Tela de boas-vindas com opções de entrar ou cadastrar.
│       │   │   └── LoginTela.kt
│       │   │       └── Tela usada para autenticação de usuários cadastrados.
│       │   │
│       │   ├── registrar/
│       │   │   └── RegistrarTela.kt
│       │   │       └── Tela de criação de uma nova conta.
│       │   │
│       │   ├── perfil/
│       │   │   └── ConfigTela.kt
│       │   │       └── Exibe dados do perfil, plano atual e opções de sair ou excluir conta.
│       │   │
│       │   └── cadernos/
│       │       ├── DashboardCadernosTela.kt
│       │       │   └── Tela inicial do app: métodos de estudo, cadernos em andamento e atividades recentes.
│       │       │
│       │       ├── HomeViewModel.kt
│       │       │   └── Controla os dados exibidos na tela inicial.
│       │       │
│       │       ├── ListaCadernosTela.kt
│       │       │   └── Lista todos os cadernos do usuário autenticado.
│       │       │
│       │       ├── CadernosViewModel.kt
│       │       │   └── Controla o carregamento e atualização da lista de cadernos.
│       │       │
│       │       ├── CriarCadernoScreen.kt
│       │       ├── CriarCadernoViewModel.kt
│       │       │   └── Criam novos cadernos.
│       │       │
│       │       ├── EditNotebookScreen.kt
│       │       ├── EditNotebookViewModel.kt
│       │       │   └── Editam ou excluem um caderno existente.
│       │       │
│       │       ├── DetalheCadernoScreen.kt
│       │       ├── DetalheCadernoViewModel.kt
│       │       │   └── Exibem os arquivos pertencentes a um caderno.
│       │       │
│       │       ├── CriarArquivoScreen.kt
│       │       ├── CriarArquivoViewModel.kt
│       │       │   └── Criam arquivos de estudo dentro de um caderno.
│       │       │
│       │       ├── EditArquivoScreen.kt
│       │       ├── EditArquivoViewModel.kt
│       │       │   └── Editam ou excluem arquivos já criados.
│       │       │
│       │       ├── CreateFlashcardScreen.kt
│       │       ├── CreateFlashcardViewModel.kt
│       │       │   └── Criam perguntas abertas no formato de flashcard.
│       │       │
│       │       ├── CreateMultipleChoiceScreen.kt
│       │       ├── CreateMultipleChoiceViewModel.kt
│       │       │   └── Criam questões de múltipla escolha.
│       │       │
│       │       ├── RevisarArquivoScreen.kt
│       │       ├── RevisarArquivoViewModel.kt
│       │       │   └── Controlam a revisão de perguntas e registram o progresso.
│       │       │
│       │       ├── FlashcardSummaryScreen.kt
│       │       ├── FlashcardSummaryViewModel.kt
│       │       │   └── Exibem o resultado da revisão, com acertos e tempo.
│       │       │
│       │       └── GraficosTelas.kt
│       │           └── Exibe gráficos de desempenho e evolução do usuário.
│       │
│       └── ui/
│           ├── componentes/
│           │   ├── Botao.kt
│           │   └── SocialLoginBotao.kt
│           │
│           │   └── Componentes reutilizáveis usados em diferentes telas.
│           │
│           └── tema/
│               ├── Color.kt
│               ├── Theme.kt
│               └── Type.kt
│
│               └── Define cores, tipografia e tema visual do aplicativo.
```


---
> Projeto desenvolvido para fins acadêmicos - Universidade Federal do Ceará (UFC) - Campus Quixadá.
