const { ipcRenderer } = require('electron');


let morphemeSelecionado = null; 
let dadosAtuais = null;         


async function processarLink() {
    const linkInput = document.getElementById('link-input');
    const link = linkInput.value;

    if (!link) return;

    document.getElementById('sentence-display').innerText = "Carregando...";
    document.getElementById('translation-display').innerText = "Processando vídeo...";
    
    try {
        const response = await fetch('http://localhost:9091/api/process', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ videoUrl: link })
        });

        if (!response.ok) throw new Error('Erro na API Java');

        const data = await response.json();
        dadosAtuais = data;
        renderizarDados(data);

    } catch (error) {
        console.error(error);
        document.getElementById('sentence-display').innerText = "Erro ao conectar";
        document.getElementById('translation-display').innerText = "Verifique se o backend Java está rodando na porta 9091.";
    }
}


function renderizarDados(data) {
    document.getElementById('sentence-display').innerText = data.sentence;
    document.getElementById('translation-display').innerText = data.translation;

    const container = document.getElementById('container-palavras');
    container.innerHTML = ''; 
    
    morphemeSelecionado = null;

    data.morphemes.forEach(morpheme => {
        const btn = document.createElement('button');
        btn.innerText = morpheme.surface; 
        
        btn.className = "word-chip px-5 py-3 bg-[#1e1e1e] border border-gray-700 rounded-xl text-xl hover:border-blue-500 hover:bg-[#252525] transition-all text-gray-300 cursor-pointer";
        
        btn.onclick = () => {
            morphemeSelecionado = morpheme;
            console.log("Selecionado:", morpheme);

            Array.from(container.children).forEach(c => {
                c.classList.remove('border-blue-500', 'text-blue-400', 'ring-2', 'ring-blue-500/20');
                c.classList.add('border-gray-700', 'text-gray-300');
            });
            btn.classList.remove('border-gray-700', 'text-gray-300');
            btn.classList.add('border-blue-500', 'text-blue-400', 'ring-2', 'ring-blue-500/20');
        };

        container.appendChild(btn);
    });
}


async function salvarCardNoAnki() {
    const deckNameInput = document.getElementById('deck-name').value;

    if (!morphemeSelecionado) {
        alert("Selecione uma palavra clicando nela primeiro!");
        return;
    }
    if (!dadosAtuais) {
        alert("Nenhuma frase processada ainda.");
        return;
    }
    if (!deckNameInput) {
        alert("Preencha o nome do Deck do Anki!");
        return;
    }

    const payload = {
        deckName: deckNameInput,
        surface: morphemeSelecionado.surface,
        reading: morphemeSelecionado.reading,
        meaning: morphemeSelecionado.meaning || "", 
        pos: morphemeSelecionado.pos || "", 
        sentence: dadosAtuais.sentence,
        translation: dadosAtuais.translation,
        furigana: dadosAtuais.furigana
    };

    try {
        const response = await fetch('http://localhost:9091/anki/create-card', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert("Card criado com sucesso no Anki! ✅");
        } else {
            console.error("Status:", response.status);
            alert("Erro ao criar card. Verifique o console do Java.");
        }
    } catch (error) {
        console.error(error);
        alert("Erro de conexão com o Java.");
    }
}