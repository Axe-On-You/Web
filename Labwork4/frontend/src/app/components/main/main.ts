import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { PointService, Point } from '../../services/point.service';

// PrimeNG Modules
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { CardModule } from 'primeng/card';

@Component({
    selector: 'app-main',
    standalone: true,
    imports: [CommonModule, FormsModule, ButtonModule, TableModule, SelectModule, InputTextModule, CardModule],
    templateUrl: './main.html'
})
export class MainComponent implements OnInit, AfterViewInit {
    @ViewChild('canvasGraph', { static: false }) canvasRef!: ElementRef<HTMLCanvasElement>;

    xOptions = [-5, -4, -3, -2, -1, 0, 1, 2, 3];
    rOptions = [-5, -4, -3, -2, -1, 0, 1, 2, 3]; // В задании написано MultiSelect, но для графика логичнее выбирать один R

    x: number = 0;
    y: string = "0"; // String, чтобы валидировать ввод руками
    r: number = 1;

    points: Point[] = [];

    constructor(private pointService: PointService, private auth: AuthService) {}

    ngOnInit() {
        this.loadPoints();
    }

    ngAfterViewInit() {
        this.drawGraph();
    }

    loadPoints() {
        this.pointService.getPoints().subscribe(data => {
            this.points = data;
            this.drawGraph();
        });
    }

    // --- Логика графика ---
    drawGraph() {
        if (!this.canvasRef) return;
        const canvas = this.canvasRef.nativeElement;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const w = canvas.width;
        const h = canvas.height;
        const margin = 20; // отступ от краев
        const scale = (w - 2 * margin) / 10; // масштаб: 10 единиц (от -5 до 5) занимают почти всю ширину

        // Очистка
        ctx.clearRect(0, 0, w, h);

        // Рисуем область (синим цветом)
        if (this.r > 0) {
            const rVal = this.r * scale;
            ctx.fillStyle = 'rgba(63, 145, 255, 0.7)'; // Тот самый голубой со скрина
            ctx.beginPath();

            // 1. Четверть круга (1 четверть): x>0, y>0, R/2
            ctx.moveTo(w / 2, h / 2);
            ctx.arc(w / 2, h / 2, rVal / 2, -Math.PI / 2, 0, false);
            ctx.fill();

            // 2. Прямоугольник (2 четверть): x от -R до 0, y от 0 до R/2
            ctx.fillRect(w / 2 - rVal, h / 2 - rVal / 2, rVal, rVal / 2);

            // 3. Треугольник (3 четверть): y >= -x/2 - r/2
            ctx.beginPath();
            ctx.moveTo(w / 2, h / 2); // Центр
            ctx.lineTo(w / 2 - rVal, h / 2); // Точка -R на оси X
            ctx.lineTo(w / 2, h / 2 + rVal / 2); // Точка -R/2 на оси Y
            ctx.closePath();
            ctx.fill();
        }

        // Рисуем оси
        ctx.strokeStyle = '#000';
        ctx.lineWidth = 1.5;
        ctx.beginPath();
        // X
        ctx.moveTo(0, h / 2); ctx.lineTo(w, h / 2);
        ctx.lineTo(w - 10, h / 2 - 5); ctx.moveTo(w, h / 2); ctx.lineTo(w - 10, h / 2 + 5);
        // Y
        ctx.moveTo(w / 2, h); ctx.lineTo(w / 2, 0);
        ctx.lineTo(w / 2 - 5, 10); ctx.moveTo(w / 2, 0); ctx.lineTo(w / 2 + 5, 10);
        ctx.stroke();

        // Подписи делений (R, R/2)
        if (this.r > 0) {
            const rVal = this.r * scale;
            ctx.fillStyle = '#000';
            ctx.font = '12px Arial';
            // На оси X
            ctx.fillText('R', w / 2 + rVal - 5, h / 2 + 15);
            ctx.fillText('R/2', w / 2 + rVal / 2 - 10, h / 2 + 15);
            ctx.fillText('-R', w / 2 - rVal - 10, h / 2 + 15);
            // На оси Y
            ctx.fillText('R', w / 2 + 5, h / 2 - rVal + 5);
            ctx.fillText('R/2', w / 2 + 5, h / 2 - rVal / 2 + 5);
            ctx.fillText('-R/2', w / 2 + 5, h / 2 + rVal / 2 + 5);
        }

        // Рисуем точки
        this.points.forEach(p => {
            // Важно: точки рисуем относительно текущего R на графике!
            // Если точка была поставлена с R=3, а сейчас R=1, она может визуально "вылететь" из области
            const px = w / 2 + p.x * scale;
            const py = h / 2 - p.y * scale;

            ctx.beginPath();
            ctx.arc(px, py, 4, 0, 2 * Math.PI);
            ctx.fillStyle = p.hit ? '#4CAF50' : '#F44336';
            ctx.fill();
            ctx.strokeStyle = '#fff';
            ctx.stroke();
        });
    }

    onCanvasClick(event: MouseEvent) {
        if (this.r <= 0) {
            alert("Выберите положительный радиус!");
            return;
        }
        const canvas = this.canvasRef.nativeElement;
        const rect = canvas.getBoundingClientRect();
        const clickX = event.clientX - rect.left;
        const clickY = event.clientY - rect.top;

        const w = canvas.width;
        const h = canvas.height;
        const margin = 20;
        const scale = (w - 2 * margin) / 10;

        const graphX = (clickX - w / 2) / scale;
        const graphY = (h / 2 - clickY) / scale;

        this.submitPoint(graphX, graphY, this.r);
    }

    submitForm() {
        const yVal = parseFloat(this.y);
        if (isNaN(yVal) || yVal < -3 || yVal > 3) {
            alert("Y должен быть числом от -3 до 3");
            return;
        }
        this.submitPoint(this.x, yVal, this.r);
    }

    submitPoint(x: number, y: number, r: number) {
        this.pointService.addPoint({ x, y, r }).subscribe(newPoint => {
            this.points.push(newPoint); // Добавляем в таблицу
            this.drawGraph(); // Перерисовываем
        });
    }

    logout() {
        this.auth.logout();
    }
}