document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('shot-form');
    const yInput = document.getElementById('y-input');
    const errorMessage = document.getElementById('error-message');
    const resultsTableBody = document.querySelector('#results-table tbody');
    const clearHistoryBtn = document.getElementById('clear-history-btn');

    // Определяем уникальный ключ для хранения истории в localStorage.
    const HISTORY_KEY = 'shot-history';

    /**
     * Читает историю из localStorage.
     * @returns {Array} Массив с объектами истории или пустой массив.
     */
    function getHistoryFromStorage() {
        const historyJson = localStorage.getItem(HISTORY_KEY);
        return historyJson ? JSON.parse(historyJson) : [];
    }

    /**
     * Сохраняет массив истории в localStorage.
     * @param {Array} history Массив с объектами истории.
     */
    function saveHistoryToStorage(history) {
        const historyJson = JSON.stringify(history);
        localStorage.setItem(HISTORY_KEY, historyJson);
    }

    /**
     * Загружает и отображает историю при первоначальной загрузке страницы.
     */
    function loadInitialHistory() {
        const history = getHistoryFromStorage();
        updateResultsTable(history); // Отображаем то, что нашли в localStorage
    }



    // Валидация значения Y при вводе
    yInput.addEventListener('input', () => {
        const yValue = yInput.value.trim().replace(',', '.');
        if (yValue === '' || yValue === '-') return;
        const yNum = Number(yValue);
        if (isNaN(yNum) || yNum <= -5 || yNum >= 5) {
            yInput.classList.add('invalid');
            errorMessage.textContent = 'Y должен быть числом в интервале (-5; 5).';
        } else {
            yInput.classList.remove('invalid');
            errorMessage.textContent = '';
        }
    });

    // Обработка отправки формы
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        errorMessage.textContent = '';

        const xValue = form.elements['x'].value;
        const yValueRaw = yInput.value.trim().replace(',', '.');
        const rValue = form.elements['r'].value;

        if (!xValue || !rValue) {
            errorMessage.textContent = 'Необходимо выбрать значения для X и R.';
            return;
        }
        if (yValueRaw === '') {
            errorMessage.textContent = 'Необходимо ввести значение для Y.';
            return;
        }
        const yNum = Number(yValueRaw);
        if (isNaN(yNum) || yNum <= -5 || yNum >= 5) {
            errorMessage.textContent = 'Y должен быть числом в интервале (-5; 5).';
            yInput.classList.add('invalid');
            return;
        } else {
            yInput.classList.remove('invalid');
        }

        const queryParams = new URLSearchParams({ x: xValue, y: yNum, r: rValue });
        const url = `/cgi-bin/labwork1.jar?${queryParams.toString()}`;

        try {
            const response = await fetch(url);
            const data = await response.json();

            if (!response.ok) {
                errorMessage.textContent = data.error || `Ошибка ${response.status}`;
            } else {
                const history = getHistoryFromStorage();
                history.push(data);
                saveHistoryToStorage(history);
                updateResultsTable(history);
            }
        } catch (error) {
            console.error('Ошибка при отправке запроса:', error);
            errorMessage.textContent = 'Не удалось связаться с сервером. Проверьте консоль.';
        }
    });

    /**
     * Новая функция для очистки истории.
     */
    clearHistoryBtn.addEventListener('click', () => {
        localStorage.removeItem(HISTORY_KEY);
        updateResultsTable([]);
        errorMessage.textContent = 'История очищена.';
    });


    function updateResultsTable(history) {
        resultsTableBody.innerHTML = '';
        if (!history) return;

        const timeFormatter = new Intl.DateTimeFormat('ru', {
            hour: '2-digit', minute: '2-digit', second: '2-digit'
        });

        // Новые записи сверху, поэтому реверсируем копию массива
        history.slice().reverse().forEach(entry => {
            const row = resultsTableBody.insertRow();
            row.insertCell(0).textContent = entry.x;
            row.insertCell(1).textContent = entry.y;
            row.insertCell(2).textContent = entry.r;
            row.insertCell(3).textContent = entry.result ? 'Попал' : 'Промах';
            const d = new Date(entry.currentTime);
            row.insertCell(4).textContent = d ? timeFormatter.format(d) : '–';
            row.insertCell(5).textContent = entry.executionTime.toLocaleString('ru');
            row.style.backgroundColor = entry.result ? '#dff0d8' : '#f2dede';
        });
    }

    loadInitialHistory();
});