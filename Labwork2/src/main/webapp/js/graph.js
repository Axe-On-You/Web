document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('graph');
    const ctx = canvas.getContext('2d');
    const errorMessage = document.getElementById('error-message');
    const width = canvas.width;
    const height = canvas.height;
    const centerX = width / 2;
    const centerY = height / 2;
    const scale = 34;
    let R_val = null;

    function drawGraph(R) {
        // Очистка
        ctx.clearRect(0, 0, width, height);
        ctx.fillStyle = "#fff";
        ctx.fillRect(0, 0, width, height);

        const r_px = R ? R * scale : 2.5 * scale;

        // --- Рисуем области в стиле SVG ---
        ctx.fillStyle = '#5c9aff';
        ctx.strokeStyle = '#0056b3';
        ctx.lineWidth = 1.5;

        // 1-я четверть: прямоугольный треугольник
        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.lineTo(centerX + r_px, centerY);
        ctx.lineTo(centerX, centerY - r_px / 2);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        // 2-я четверть: четверть круга
        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.arc(centerX, centerY, r_px / 2, Math.PI, 1.5 * Math.PI);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        // 3-я четверть: прямоугольник
        ctx.beginPath();
        ctx.rect(centerX - r_px, centerY, r_px, r_px / 2);
        ctx.fill();
        ctx.stroke();

        // --- Рисуем сетку и оси ---
        ctx.strokeStyle = '#ccc';
        ctx.lineWidth = 1;
        ctx.setLineDash([2, 2]);
        for (let i = scale; i < centerX; i += scale) {
            ctx.beginPath();
            ctx.moveTo(centerX + i, 0); ctx.lineTo(centerX + i, height);
            ctx.moveTo(centerX - i, 0); ctx.lineTo(centerX - i, height);
            ctx.moveTo(0, centerY + i); ctx.lineTo(width, centerY + i);
            ctx.moveTo(0, centerY - i); ctx.lineTo(width, centerY - i);
            ctx.stroke();
        }
        ctx.setLineDash([]);

        // Оси
        ctx.strokeStyle = '#333';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.moveTo(0, centerY); ctx.lineTo(width, centerY);
        ctx.moveTo(centerX, 0); ctx.lineTo(centerX, height);
        ctx.stroke();

        // Стрелки
        ctx.fillStyle = '#333';
        ctx.beginPath();
        ctx.moveTo(width - 10, centerY - 5); ctx.lineTo(width, centerY); ctx.lineTo(width - 10, centerY + 5); ctx.closePath();
        ctx.fill();
        ctx.beginPath();
        ctx.moveTo(centerX - 5, 10); ctx.lineTo(centerX, 0); ctx.lineTo(centerX + 5, 10); ctx.closePath();
        ctx.fill();

        // Метки
        ctx.fillStyle = '#333';
        ctx.font = '14px sans-serif';
        const labels = ['-R', '-R/2', 'R/2', 'R'];
        const positions = [-r_px, -r_px / 2, r_px / 2, r_px];
        positions.forEach((pos, i) => {
            ctx.fillText(labels[i], centerX + pos - 10, centerY + 20);
            ctx.fillText(labels[i], centerX + 5, centerY - pos + 5);
        });
        ctx.fillText('X', width - 20, centerY - 10);
        ctx.fillText('Y', centerX + 10, 20);
    }

    function drawPoints() {
        const rVal = getSelectedR();
        if (!rVal) return;

        const r_px = rVal * scale;
        const rows = document.querySelectorAll('#results-table tbody tr');
        rows.forEach(row => {
            const cells = row.cells;
            const x = parseFloat(cells[0].textContent);
            const y = parseFloat(cells[1].textContent);
            const r_point = parseFloat(cells[2].textContent);
            const hit = cells[3].textContent.trim() === 'Попадание';

            if (r_point === rVal) {
                const canvasX = centerX + (x / r_point) * r_px;
                const canvasY = centerY - (y / r_point) * r_px;

                ctx.fillStyle = hit ? 'green' : 'red';
                ctx.strokeStyle = 'black';
                ctx.lineWidth = 1;
                ctx.beginPath();
                ctx.arc(canvasX, canvasY, 4, 0, 2 * Math.PI);
                ctx.fill();
                ctx.stroke();
            }
        });
    }

    function getSelectedR() {
        const rRadio = document.querySelector('input[name="r"]:checked');
        return rRadio ? parseFloat(rRadio.value) : null;
    }

    function updateGraphAndPoints() {
        errorMessage.textContent = '';
        R_val = getSelectedR();
        drawGraph(R_val);
        drawPoints();
    }

    document.querySelectorAll('input[name="r"]').forEach(radio => {
        radio.addEventListener('change', updateGraphAndPoints);
    });

    canvas.addEventListener('click', (event) => {
        errorMessage.textContent = ''; // Очищаем старые ошибки
        R_val = getSelectedR();
        if (!R_val) {
            errorMessage.textContent = 'Пожалуйста, выберите значение R.';
            return;
        }

        const r_px = R_val * scale;
        const rect = canvas.getBoundingClientRect();
        const canvasX = event.clientX - rect.left;
        const canvasY = event.clientY - rect.top;

        const x = (canvasX - centerX) / r_px * R_val;
        const y = (centerY - canvasY) / r_px * R_val;

        if (y <= -3 || y >= 5) {
            errorMessage.textContent = `Значение Y, полученное с графика (${y.toFixed(2)}), выходит за допустимый диапазон (-3 ... 5). Запрос не будет отправлен.`;
            return;
        }

        const params = new URLSearchParams();
        params.append('x', x.toFixed(3));
        params.append('y', y.toFixed(3));
        params.append('r', R_val);

        window.location.href = `controller?${params.toString()}`;
    });

    // Первоначальная отрисовка
    updateGraphAndPoints();
});