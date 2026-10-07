const API_URL = 'http://localhost:8080/api/v1/validacao/upload';

const dropZone = document.getElementById('dropZone');
const fileInput = document.getElementById('fileInput');
const fileInfo = document.getElementById('fileInfo');
const fileName = document.getElementById('fileName');
const btnRemoveFile = document.getElementById('btnRemoveFile');
const btnProcessar = document.getElementById('btnProcessar');
const resultCard = document.getElementById('resultCard');

let selectedFile = null;

dropZone.addEventListener('click', () => fileInput.click());

dropZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropZone.style.backgroundColor = 'rgba(255, 107, 0, 0.1)';
});

dropZone.addEventListener('dragleave', () => {
    dropZone.style.backgroundColor = 'transparent';
});

dropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropZone.style.backgroundColor = 'transparent';
    if (e.dataTransfer.files.length > 0) {
        handleFileSelect(e.dataTransfer.files[0]);
    }
});

fileInput.addEventListener('change', (e) => {
    if (e.target.files.length > 0) {
        handleFileSelect(e.target.files[0]);
    }
});

function handleFileSelect(file) {
    selectedFile = file;
    fileName.textContent = `${file.name} (${(file.size / 1024).toFixed(1)} KB)`;
    fileInfo.classList.remove('hidden');
    btnProcessar.disabled = false;
}

btnRemoveFile.addEventListener('click', (e) => {
    e.stopPropagation();
    selectedFile = null;
    fileInput.value = '';
    fileInfo.classList.add('hidden');
    btnProcessar.disabled = true;
});

btnProcessar.addEventListener('click', async () => {
    if (!selectedFile) return;

    btnProcessar.disabled = true;
    btnProcessar.textContent = 'Processando OCR, IA e ViaCEP...';

    const formData = new FormData();
    formData.append('arquivo', selectedFile);

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) throw new Error('Falha no processamento.');

        const data = await response.json();
        renderizarResultado(data);
    } catch (err) {
        alert('Erro ao conectar com a API do Backend. Certifique-se de que o Spring Boot está rodando em http://localhost:8080');
    } finally {
        btnProcessar.disabled = false;
        btnProcessar.textContent = 'Validar Documento';
    }
});

function renderizarResultado(data) {
    resultCard.classList.remove('hidden');

    document.getElementById('protocoloId').textContent = `#${data.protocolo}`;
    const badge = document.getElementById('statusBadge');
    badge.textContent = data.status;
    badge.className = `badge ${data.status.toLowerCase()}`;

    document.getElementById('jsonBox').textContent = JSON.stringify(data.dadosExtraidos, null, 2);

    const rulesList = document.getElementById('rulesList');
    rulesList.innerHTML = '';

    data.regrasAtendidas.forEach(r => {
        const li = document.createElement('li');
        li.style.color = '#28C76F';
        li.textContent = `✓ ${r}`;
        rulesList.appendChild(li);
    });

    data.inconformidades.forEach(i => {
        const li = document.createElement('li');
        li.style.color = '#EA5455';
        li.textContent = `✗ ${i}`;
        rulesList.appendChild(li);
    });

    const pipelineGrid = document.getElementById('pipelineGrid');
    pipelineGrid.innerHTML = '';

    data.pipelineEtapas.forEach(e => {
        const card = document.createElement('div');
        card.className = 'pipeline-card';
        card.innerHTML = `
            <strong>Passo ${e.passo}: ${e.modulo}</strong>
            <p style="margin:4px 0; font-size:0.85rem; color:#A0A5B5;">${e.descricao}</p>
            <small style="color:#FF6B00;">${e.resultado}</small>
        `;
        pipelineGrid.appendChild(card);
    });
}
