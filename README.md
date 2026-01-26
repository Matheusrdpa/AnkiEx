# AnkiEx - YouTube to Anki Automator 🇯🇵☕

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Anki](https://img.shields.io/badge/Anki-Connect-blue?style=for-the-badge)

**AnkiEx** é uma ferramenta desktop desenvolvida em Java que automatiza o processo de "mineração" de vocabulário japonês (Sentence Mining) diretamente de vídeos do YouTube para o Anki.

Diferente de extensões de navegador, o AnkiEx monitora sua área de transferência e orquestra downloads, processamento de linguagem natural (NLP) e criação de cards em background.

## 🚀 Funcionalidades

- **Monitoramento de Clipboard:** Detecta automaticamente links do YouTube copiados (`Ctrl+C`).
- **Sincronização de Legendas:** As legendas do video vão ser baixadas e a frase vai ser extraida do timestamp em que estiver no link.
- **Análise Morfológica (NLP):** Utiliza **Sudachi** para quebrar frases japonesas em palavras e identificar classes gramaticais.
- **Dicionário Automático:** Busca definições, leituras (Furigana) e traduções via API do Jisho.org.
- **Integração com Anki:** Cria cards automaticamente no seu deck preferido via AnkiConnect.
- **Interface Gráfica (Swing):** Pop-up flutuante para seleção de palavras e configuração de Deck.

## 🛠️ Tecnologias Utilizadas

- **Core:** Java 17, Spring Boot 3
- **GUI:** Java Swing
- **NLP:** Sudachi (Java)
- **External Tools:** yt-dlp (Download de legendas), AnkiConnect (Integração)
- **Build:** Maven

## ⚙️ Pré-requisitos

Para rodar o projeto, você precisará configurar o ambiente:

1. **Java JDK 17** ou superior instalado.
2. **Anki** instalado com o add-on **AnkiConnect** (Código: `2055492159`).
   - *Nota:* Certifique-se de que o AnkiConnect está configurado para aceitar conexões locais (padrão porta 8765).
3. **Dependências na pasta raiz:**
   O projeto espera a seguinte estrutura de arquivos para funcionar:

   ```text
   AnkiEx/
   ├── tools/
   │   ├── yt-dlp.exe       # Executável para baixar legendas
   │   └── whitelist.txt    # Lista de exceções para o tokenizador
   ├── system_core.dic      # Dicionário do Sudachi (Necessário baixar)
   └── src/...
**Importante:** Você precisa baixar o system_core.dic que pode ser encontrado no [github](https://github.com/WorksApplications/SudachiDict?tab=readme-ov-file) (dicionário do Sudachi) e colocá-lo na raiz do projeto.

## 🏃‍♂️ Como Rodar

1. Clone o repositório
```
git clone https://github.com/matheusrdpa/ankiex.git
cd ankiex 
```
2. Configure as ferramentas.
   - *Nota:* Verifique se o yt-dlp.exe está dentro da pasta tools/.
   - *Nota:* Coloque o system_core.dic na raiz do projeto.

3. Abra o Anki: O Anki precisa estar aberto para receber os cards (o plugin AnkiConnect deve estar ativo).

4. Execute a aplicação:
```
./mvnw spring-boot:run
```

## Como Usar
1. Com o AnkiEx rodando e o Anki aberto:

2. Vá para o YouTube e assista a um vídeo em japonês (que tenha legendas disponíveis).

3. Quando ouvir uma frase que quer aprender, copie a URL do vídeo com o tempo atual (Clique com o botão direito no vídeo → "Copiar URL no tempo atual" ou Ctrl+C na barra de endereço se já tiver o parâmetro &t=).

4. O AnkiEx detectará o link e abrirá um popup flutuante com a frase detectada e a seguinte formatação:.
   <img width="383" height="422" alt="image" src="https://github.com/user-attachments/assets/328e8d9e-389f-4dd8-9235-01092bf2634a" />

6. Digite o nome exato do Deck (ex: Mining) e o Número da palavra que deseja aprender.

Pronto! O card foi criado no Anki com Frase, Definição e Leitura automaticamente dessa forma: 
<img width="669" height="602" alt="image" src="https://github.com/user-attachments/assets/820816ca-afba-483b-b823-205179041ab3" />


## Estrutura do projeto
```ClipBoardWatcher.java``` Thread Daemon que monitora a área de transferência do SO em tempo real.

```YtDlpService.java``` Gerencia processos externos (ProcessBuilder) para download e sincronização de legendas.

```MorphAnalyzerService.java``` Integração com o Sudachi NLP para análise morfológica e tokenização.

```AnkiService.java``` Cliente HTTP para comunicar com a API local do Anki (JSON-RPC).
