const controlForm = document.querySelector('#control-form');
var errorCount = 0;

function validateForm() {
    const selectedRBoxes = document.querySelectorAll('#r-values .ui-chkbox-box.ui-state-active');
    const xValue = document.querySelector('#x-value_input').value;
    const yValue = document.querySelector('#y-value_input').value;

    let summary = "Ошибка валидации";

    if (!selectedRBoxes.length) {
        showError(summary, "Выбери хоть один радиус");
        return false;
    }

    let xNumber = parseFloat(xValue);
    if (isNaN(xNumber) || !(-4 <= xNumber && xNumber <= 4)) {
        showError(summary, "Проверь X!");
        return false;
    }

    let yNumber = parseFloat(yValue);
    if (isNaN(yNumber) || !(-5 < yNumber && yNumber < 3)) {
        showError(summary, "Проверь Y!");
        return false;
    }

    return true;
}

function showError(summary, text) {
    const errorDiv = document.createElement("div");
    errorDiv.setAttribute("id", "error-div-" + ++errorCount);
    errorDiv.setAttribute("class", "inline-error-container");
    errorDiv.innerHTML = `
        <span style="font-size: 40px;">⚠️</span>
        <div>
            <h2 style="text-align: center; color: #780000">${summary}</h2>
            <p id="error-info-p">${text}</p>
        </div>
        <button class="close-button" id="close-error-button-${errorCount}">×</button>
    `;

    controlForm.after(errorDiv);
    document.querySelector("#close-error-button-" + errorCount).addEventListener("click", () => {
        const thisDiv = document.querySelector("#error-div-" + errorCount);
        thisDiv.remove();
        errorCount--;
    })

    timedRemoveElement("#error-div-" + errorCount)

}

async function timedRemoveElement(nodeSelector) {
    setTimeout(() => {
        const thisDiv = document.querySelector(nodeSelector);
        if (thisDiv) {
            thisDiv.remove();
            errorCount--;
        }
    }, 3000);
}
