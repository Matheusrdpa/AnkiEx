# AnkiEx - YouTube to Anki Automator 🇯🇵☕

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Anki](https://img.shields.io/badge/Anki-Connect-blue?style=for-the-badge)

<img width="1031" height="751" alt="image" src="https://github.com/user-attachments/assets/5615fb07-eecc-42f5-b62c-cc7bbeceba3e" />

**AnkiEx** é uma ferramenta desktop desenvolvida em Java que automatiza o processo de "mineração" de vocabulário japonês (Sentence Mining) diretamente de vídeos do YouTube para o Anki.

Diferente de extensões de navegador, o AnkiEx monitora sua área de transferência, utiliza Google Vertex AI (Gemini 2.0 Flash) para orquestrar e corrigir legendas fragmentadas, e cria cards completos com furigana e tradução automática.

## ⚠️ Nota sobre o Desenvolvimento

Este projeto é um showcase de **Backend Engineering**.

* **Backend (Java/Spring):** Desenvolvido por mim para orquestrar a lógica de mineração, processamento de legendas e integração com o Anki.
* **GUI (Electron/Tailwind):** A interface gráfica foi gerada totalmente com o uso de **Inteligência Artificial** (LLMs). O objetivo foi criar um cliente visual moderno apenas para consumir a API Java, permitindo que meu foco permanecesse na arquitetura, performance e regras de negócio do servidor mas que continuasse agradável para o usuário.

## Funcionalidades

- **Monitoramento de Clipboard:** Detecta automaticamente links do YouTube copiados (`Ctrl+C`).
- **Sincronização de Legendas:** As legendas do video vão ser baixadas e a frase vai ser extraida do timestamp em que estiver no link.
- **IA como Orquestrador:** Corrige frases cortadas do YouTube, gera traduções para o Inglês e extrai morfemas com significados contextualizados.
- **Integração com Anki:** Cria cards automaticamente no seu deck preferido via AnkiConnect.
- **Interface Gráfica (Swing):** Pop-up flutuante para seleção de palavras e configuração de Deck.
- **Furigana Automático:** Gera leitura de Kanjis no formato padrão do Anki (漢字[かんじ]).

## Tecnologias Utilizadas

- **Core:** Java 17, Spring Boot 3
- **Artificial Intelligence:** IA com Vertex AI: Utiliza o modelo gemini-2.0-flash para corrigir legendas, gerar traduções e extrair morfemas contextualizados
- **External Tools:** yt-dlp (Download de legendas), AnkiConnect (Integração)
- **Build:** Maven
- **GUI (Interface Auxiliar):**
* Electron & TailwindCSS *(Gerados via IA para visualização)*

## Pré-requisitos

Para rodar o projeto, você precisará configurar o ambiente:

1. **Java JDK 17** ou superior instalado.
2.  **Google Cloud Platform (Vertex AI):**
   -  Um projeto ativo no GCP com a API Vertex AI habilitada.
   -  Uma Service Account com as permissões necessárias e seu arquivo de credenciais JSON.
3. **Anki** instalado com o add-on **AnkiConnect** (Código: `2055492159`).
   - *Nota:* Certifique-se de que o AnkiConnect está configurado para aceitar conexões locais (padrão porta 8765).
   - *Nota:* _No momento_ é **NECESSÁRIO** que você tenha um tipo de nota chamado "Mining" (case sensitive) com os seguintes campos:
   - <img width="569" height="470" alt="image" src="https://github.com/user-attachments/assets/212a3ba5-4309-4e1d-8383-739715751e11" />
   
5. **Dependências na pasta raiz:**
   O projeto espera a seguinte estrutura de arquivos para funcionar:

   ```text
   AnkiEx/
   ├── tools/
   │   ├── yt-dlp.exe       # Executável para baixar legendas
   └── src/...
   
## Como Rodar

1. Configure as Variáveis de Ambiente:
O projeto utiliza injeção de dependência para ler as credenciais do GCP. Defina as seguintes variáveis no seu sistema:

   GCP_PROJECT_ID: O ID do seu projeto no Google Cloud.

   GCP_CREDENTIALS_PATH: O caminho completo para o arquivo JSON da sua Service Account (ex: C:/keys/projeto-anki.json).

2. Clone o repositório
```
git clone https://github.com/matheusrdpa/ankiex.git
cd ankiex 
```
3. Configure as ferramentas.
   - *Nota:* Verifique se o yt-dlp.exe está dentro da pasta tools/.

4. Abra o Anki: O Anki precisa estar aberto para receber os cards (o plugin AnkiConnect deve estar ativo).

5. Instale as dependencias e execute a aplicação:
```
cd frontend
npm install
```
- e depois:
```
npm start
``` 

## Como Usar
1. Com o AnkiEx rodando e o Anki aberto:

2. Vá para o YouTube e assista a um vídeo em japonês (que tenha legendas disponíveis).

3. Quando ouvir uma frase que quer aprender, copie a URL do vídeo com o tempo atual (Clique com o botão direito no vídeo → "Copiar URL no tempo atual" ou Ctrl+C na barra de endereço se já tiver o parâmetro &t=).

4. Cole o link copiado no campo na parte superior:
  <img width="1031" height="751" alt="image" src="https://github.com/user-attachments/assets/5615fb07-eecc-42f5-b62c-cc7bbeceba3e" />

6. Digite o nome exato do Deck (ex: Minna no Nihongo) (Case sensitive), clique na palavra que deseja aprender e depois em "Add to anki".

Pronto! O card foi criado no Anki com Frase, Definição e Leitura automaticamente dessa forma: 
<img width="669" height="601" alt="image" src="https://github.com/user-attachments/assets/88ce4914-b8d5-4885-baf6-712ed514d20b" />

- **ATENÇÃO** Para o card ser criado exatamente com a formatação acima, você precisa configurar seu card da mesma forma.
- Abra o anki, no menu superior, selecione Ferramentas → Gerenciar tipos de notas → Selecione o tipo Mining (Se não possuir ainda, crie um) → Cartões. E deixe assim:
-  ## Frente:  <img width="1116" height="678" alt="image" src="https://github.com/user-attachments/assets/02c42a62-a306-45d0-95db-6f5447c7a99e" />
- ## Verso: <img width="1116" height="678" alt="image" src="https://github.com/user-attachments/assets/6680837e-6a68-4065-bcfa-aee09608fc72" />





## Estrutura do projeto
```YtDlpService.java``` Gerencia processos externos (ProcessBuilder) para download e sincronização de legendas.

```AiService.java``` O cérebro do projeto. Envia a legenda bruta para a IA e recebe um JSON estruturado com tudo o que o card precisa.

```AnkiService.java``` Cliente HTTP para comunicar com a API local do Anki (JSON-RPC).

```JsonConverterService.java```Filtra os arquivos .json3 do YouTube usando busca por proximidade temporal.

