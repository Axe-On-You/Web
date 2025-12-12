const canvas = document.querySelector('#map-canvas');
const coordP = document.querySelector('#current_coord');
const ctx = canvas.getContext("2d");
const width = canvas.width;
const height = canvas.height;
const centerX = width / 2;
const centerY = height / 2;

const one = 30;

function handleRChange(RSelectedRaw) {
    const selectedRJson = JSON.parse(RSelectedRaw);
    drawFigureOnCanvas(selectedRJson);
}

function getRadians(degrees) {
    return (Math.PI / 180) * degrees;
}

function drawCoordinateSystem() {
    const canvas = document.getElementById('map-canvas');
    const ctx = canvas.getContext('2d');

    const width = canvas.width;
    const height = canvas.height;
    const centerX = width / 2;
    const centerY = height / 2;
    const axisColor = '#333333';
    const arrowSize = 10;

    ctx.clearRect(0, 0, width, height);

    // оси координат
    ctx.strokeStyle = axisColor;
    ctx.lineWidth = 2;

    // ось х
    ctx.beginPath();
    ctx.moveTo(0, centerY);
    ctx.lineTo(width, centerY);
    ctx.stroke();

    // ось у
    ctx.beginPath();
    ctx.moveTo(centerX, 0);
    ctx.lineTo(centerX, height);
    ctx.stroke();

    // стрелка х
    ctx.beginPath();
    ctx.moveTo(width - arrowSize, centerY - arrowSize/2);
    ctx.lineTo(width, centerY);
    ctx.lineTo(width - arrowSize, centerY + arrowSize/2);
    ctx.stroke();

    // стрелка у
    ctx.beginPath();
    ctx.moveTo(centerX - arrowSize/2, arrowSize);
    ctx.lineTo(centerX, 0);
    ctx.lineTo(centerX + arrowSize/2, arrowSize);
    ctx.stroke();

    ctx.fillStyle = axisColor;
    ctx.font = '14px Courier New';
    ctx.fillText('x', width - 15, centerY - 10);
    ctx.fillText('y', centerX + 10, 15);
    ctx.fillText('0', centerX + 5, centerY - 5);
}

// рисование точки по абсолютный координатам
function drawDot(x, y, success) {
    const color = success ? "green" : "red";
    const strokeColor = "black";
    const radius = 5;

    ctx.beginPath();
    ctx.arc(x, y, radius, 0, 2 * Math.PI);
    ctx.fillStyle = color;
    ctx.fill();
    ctx.strokeStyle = strokeColor;
    ctx.stroke();
}

// абсолютные координаты в системные
function absToSystemCoord(x, y) {
    const logicalX = (x - centerX) / one;
    const logicalY = (centerY - y) / one;

    return {x: logicalX.toFixed(2), y: logicalY.toFixed(3)};
}

// системные координаты в абсолютные
function systemToAbsCoord(x, y) {
    const absX = x * one + centerX;
    const absY = centerY - y * one;

    return {x: absX, y: absY};
}

function drawAllDots(selectedRValues) {
    const pointsJson = document.querySelector("#all-points-json").value;
    if (!pointsJson || !selectedRValues) return;

    const allPoints = JSON.parse(pointsJson);
    const numericRValues = selectedRValues.map(r => parseFloat(r));
    const filteredPoints = allPoints.filter(p => numericRValues.includes(parseFloat(p.r)));

    filteredPoints.forEach(dot => {
        let absCoords = systemToAbsCoord(dot.x, dot.y);
        drawDot(absCoords.x, absCoords.y, dot.hit);
    });
}


function drawFigureOnCanvas(selectedRJson) {
    const width = canvas.width;
    const height = canvas.height;
    const centerX = width / 2;
    const centerY = height / 2;
    const areaColor = "rgba(0, 48, 73, 0.4)"

    drawCoordinateSystem();

    selectedRJson.forEach(val => {
        let rValue = parseFloat(val);

        ctx.fillStyle = areaColor;

        ctx.fillRect(centerX, centerY, rValue * one / 2, rValue * one)

        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.lineTo(centerX - rValue * one, centerY);
        ctx.lineTo(centerX, centerY - rValue * one / 2);
        ctx.closePath();
        ctx.fill();

        ctx.beginPath();
        ctx.arc(centerX, centerY, rValue * one / 2, getRadians(270), getRadians(0));
        ctx.lineTo(centerX, centerY);
        ctx.closePath();
        ctx.fill();
    });

    drawAllDots(selectedRJson);

}


function refreshCanvas() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    const selectedRCheckboxes = document.querySelectorAll('#r-values input[type=checkbox]:checked');
    const selectedRValues = Array.from(selectedRCheckboxes).map(cb => cb.value);

    drawCoordinateSystem();
    drawFigureOnCanvas(selectedRValues);
}


function showClientSideMessage(summary, detail, severity) {
    addFacesMessage(
        [
            {name: 'summary', value: summary},
            {name: 'detail', value: detail},
            {name: 'severity', value: severity}
        ]
    );
}


canvas.addEventListener("mousemove", event => {
    const rect = canvas.getBoundingClientRect();

    let currentX = event.clientX - rect.left;
    let currentY = event.clientY - rect.top;

    let trueCoords = absToSystemCoord(currentX, currentY);
    let trueX = trueCoords.x;
    let trueY = trueCoords.y;

    coordP.textContent = `x: ${trueX}, y: ${trueY}`
})
canvas.addEventListener("mouseleave", event => {
    coordP.textContent = "---";
})

canvas.addEventListener("click", event => {
    let localCanvas = document.querySelector("#map-canvas");

    const selectedRCheckboxes = document.querySelectorAll('#r-values input[type=checkbox]:checked');
    const selectedRValues = Array.from(selectedRCheckboxes).map(cb => cb.value);

    console.log(selectedRValues);

    if (selectedRValues.length === 0) {
        showClientSideMessage("Неа", "Точка не выберется без радиуса", "info");
        return;
    }

    const rect = localCanvas.getBoundingClientRect();
    const clickX = event.clientX - rect.left;
    const clickY = event.clientY - rect.top;

    const systemCoords = absToSystemCoord(clickX, clickY);

    if (-5 >= systemCoords.y || 3 <= systemCoords.y) {
        showClientSideMessage("Неа", "Y должен быть от -5 до 3 не включительно", "warn");
        return;
    }
    if (-4 > systemCoords.x || 4 < systemCoords.x) {
        showClientSideMessage("Неа", "X должен быть от -4 до 4 включительно", "warn");
        return;
    }

    addPointViaCanvas(
        [
            {name: "x", value: systemCoords.x},
            {name: "y", value: systemCoords.y},
            {name: "rList", value: JSON.stringify(selectedRValues)},
        ]
    );

});


refreshCanvas();