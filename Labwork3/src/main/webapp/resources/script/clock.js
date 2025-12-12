document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('clock-canvas');
    if (! canvas) return;

    const ctx = canvas.getContext("2d");
    const canvasSize = 300;
    canvas.width = canvasSize;
    canvas.height = canvasSize;

    let radius = canvas.height / 2;

    ctx.translate(radius, radius);
    radius *= 0.9;

    function drawClock() {
        drawFace(ctx, radius);
        drawTicks(ctx, radius);
        drawTime(ctx, radius);
    }

    function drawFace(ctx, radius) {
        ctx.beginPath();
        ctx.arc(0, 0, radius, 0, 2 * Math.PI);
        ctx.fillStyle = 'white';
        ctx.fill();

        ctx.strokeStyle = '#780000';
        ctx.lineWidth = radius * 0.1;
        ctx.stroke();

        ctx.beginPath();
        ctx.arc(0, 0, radius * 0.05, 0, 2 * Math.PI);
        ctx.fillStyle = '#333';
        ctx.fill();
    }

    function drawTicks(ctx, radius) {
        ctx.strokeStyle = '#780000';
        for (let i = 0; i < 60; i++) {
            const angle = (i / 60) * 2 * Math.PI;
            ctx.save();
            ctx.rotate(angle);

            ctx.beginPath();
            if (i % 5 === 0) {
                ctx.lineWidth = radius * 0.04;
                ctx.moveTo(0, -radius * 0.9);
            } else {
                ctx.lineWidth = radius * 0.02;
                ctx.moveTo(0, -radius * 0.95);
            }

            ctx.lineTo(0, -radius);
            ctx.stroke();

            ctx.restore();
        }
    }

    function drawTime(ctx, radius) {
        const now = new Date();
        let hour = now.getHours();
        let minute = now.getMinutes();
        let second = now.getSeconds();
        hour %= 12

        let hourAngle = (hour * Math.PI / 6) +
            (minute * Math.PI / (6 * 60)) +
            (second * Math.PI / (360 * 60));

        let minuteAngle = (minute * Math.PI / 30) + (second * Math.PI / (30 * 60));

        let secondAngle = (second * Math.PI / 30);

        drawHand(ctx, hourAngle, radius * 0.5, radius * 0.07, 'black');
        drawHand(ctx, minuteAngle, radius * 0.8, radius * 0.07, '#003049');
        drawHand(ctx, secondAngle, radius * 0.9, radius * 0.02, '#780000');

    }

    function drawHand(ctx, pos, length, width, color) {
        ctx.beginPath();
        ctx.lineWidth = width;
        ctx.lineCap = "round";
        ctx.strokeStyle = color;

        ctx.moveTo(0, 0);

        ctx.rotate(pos);
        ctx.lineTo(0, -length);
        ctx.stroke();

        ctx.rotate(-pos);
    }

    drawClock();
    setInterval(drawClock, 7000);
});