document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('point-form');
    const yInput = document.getElementById('y-input');
    const errorMessage = document.getElementById('error-message');

    form.addEventListener('submit', (event) => {
        if (!validateForm()) {
            event.preventDefault();
        }
    });

    // Очистка ошибок при сбросе формы
    form.addEventListener('reset', () => {
        errorMessage.textContent = '';
    });

    function validateForm() {
        errorMessage.textContent = '';
        let errorParts = [];

        // Валидация X
        const xValue = form.querySelector('input[name="x"]:checked');
        if (!xValue) {
            errorParts.push('Необходимо выбрать значение X.');
        }

        // Валидация Y
        const yValue = yInput.value.trim();
        const yValueForCheck = yValue.replace(',', '.');

        if (yValue === '') {
            errorParts.push('Необходимо ввести значение Y.');
        } else if (yValue.length > 17) {
            errorParts.push('Длина значения Y не должна превышать 17 символов.');
        } else if (isNaN(yValueForCheck) || !isFinite(yValueForCheck)) {
            errorParts.push('Значение Y должно быть числом.');
        } else {
            const yNum = parseFloat(yValueForCheck);
            if (yNum <= -3 || yNum >= 5) {
                errorParts.push('Значение Y должно быть в интервале (-3 ... 5).');
            }
        }

        // Валидация R
        const rValue = form.querySelector('input[name="r"]:checked');
        if (!rValue) {
            errorParts.push('Необходимо выбрать значение R.');
        }

        if (errorParts.length > 0) {
            errorMessage.innerHTML = errorParts.join('<br>');
            return false;
        }

        return true;
    }
});