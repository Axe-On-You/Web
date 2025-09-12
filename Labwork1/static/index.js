document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('shot-form');
    const yInput = document.getElementById('y-input');
    const errorMessage = document.getElementById('error-message');
    const resultsTableBody = document.querySelector('#results-table tbody');

    async function loadInitialHistory() {
        try {
            // Делаем запрос БЕЗ параметров
            const response = await fetch('/cgi-bin/labwork1.jar');
            if (response.ok) {
                const data = await response.json();
                if (data && data.length > 0) {
                    updateResultsTable(data);
                }
            } else {
                console.error('Ошибка при загрузке истории:', response.statusText);
            }
        } catch (error) {
            console.error('Не удалось связаться с сервером для загрузки истории:', error);
        }
    }

    loadInitialHistory();

    // Валидация значения Y при вводе
    yInput.addEventListener('input', () => {
        const yValue = yInput.value.trim().replace(',', '.');
        if (yValue === '' || yValue === '-') {
            return; // Разрешаем пустое поле или знак минуса для начала ввода
        }

        const yNum = Number(yValue);

        if (isNaN(yNum) || yNum < -5 || yNum > 5) {
            yInput.classList.add('invalid');
            errorMessage.textContent = 'Y должен быть числом в диапазоне [-5; 5].';
        } else {
            yInput.classList.remove('invalid');
            errorMessage.textContent = '';
        }
    });

    // Обработка отправки формы
    form.addEventListener('submit', async (event) => {
        event.preventDefault(); // Предотвращаем стандартную отправку формы
        errorMessage.textContent = '';

        // --- Валидация перед отправкой ---
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
        if (isNaN(yNum) || yNum < -5 || yNum > 5) {
            errorMessage.textContent = 'Y должен быть числом в диапазоне [-5; 5].';
            yInput.classList.add('invalid');
            return;
        } else {
            yInput.classList.remove('invalid');
        }

        // --- Отправка AJAX-запроса ---
        const queryParams = new URLSearchParams({
            x: xValue,
            y: yNum,
            r: rValue
        });

        const url = `/cgi-bin/labwork1.jar?${queryParams.toString()}`;

        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            });

            const data = await response.json();

            if (!response.ok) {
                // Отображаем ошибку от сервера (например, ошибку валидации)
                errorMessage.textContent = data.error || `Ошибка ${response.status}`;
            } else {
                // Обновляем таблицу с результатами
                updateResultsTable(data);
            }

        } catch (error) {
            console.error('Ошибка при отправке запроса:', error);
            errorMessage.textContent = 'Не удалось связаться с сервером. Проверьте консоль.';
        }
    });

    function parseInstantToDate(isoString) {
        if (!isoString) return null;
        // Сначала пробуем стандартный парсер
        const direct = new Date(isoString);
        if (!isNaN(direct)) return direct;

        // Если не получилось (из-за >3 знаков после точки) — обрежем до миллисекунд
        const m = /^(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2})(\.\d+)?Z$/.exec(isoString);
        if (m) {
            const base = m[1];
            const frac = (m[2] || "").slice(0, 4); // возьмем до ".xyz" (3 мс + точка)
            // Преобразуем дробные наносекунды до миллисекунд
            let ms = "";
            if (frac) {
                // frac вида ".095412479" -> ".095"
                ms = "." + frac.replace(".", "").padEnd(3, "0").slice(0, 3);
            }
            const fixed = `${base}${ms}Z`;
            const d = new Date(fixed);
            if (!isNaN(d)) return d;
        }
        return null;
    }

    function updateResultsTable(history) {
        resultsTableBody.innerHTML = '';

        const timeFormatter = new Intl.DateTimeFormat(undefined, {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });

        // Новые записи сверху
        history.slice().reverse().forEach(entry => {
            const row = resultsTableBody.insertRow();

            row.insertCell(0).textContent = entry.x;
            row.insertCell(1).textContent = entry.y;
            row.insertCell(2).textContent = entry.r;
            row.insertCell(3).textContent = entry.result ? 'Попал' : 'Промах';

            // Время: переводим Instant в локальное отображение
            const d = parseInstantToDate(entry.currentTime);
            row.insertCell(4).textContent = d ? timeFormatter.format(d) : String(entry.currentTime ?? '');

            // Время работы (нс): гарантированно отображаем число
            const exec = entry.executionTime;
            row.insertCell(5).textContent =
                typeof exec === 'number' ? exec.toLocaleString() : String(exec ?? '');

            // Подсветка строки
            row.style.backgroundColor = entry.result ? '#dff0d8' : '#f2dede';
        });
    }
});