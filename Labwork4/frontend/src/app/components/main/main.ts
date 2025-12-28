import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { PointService, Point } from '../../services/point.service';

// PrimeNG Modules
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { MultiSelectModule } from 'primeng/multiselect';
import { InputTextModule } from 'primeng/inputtext';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
    selector: 'app-main',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ButtonModule,
        TableModule,
        MultiSelectModule,
        InputTextModule,
        CardModule,
        ToastModule
    ],
    providers: [MessageService],
    templateUrl: './main.html'
})
export class MainComponent implements OnInit, AfterViewInit {
    @ViewChild('canvasGraph', { static: false }) canvasRef!: ElementRef<HTMLCanvasElement>;

    xOptions = [-5, -4, -3, -2, -1, 0, 1, 2, 3];
    rOptions = [1, 2, 3];

    selectedX: number[] = [];
    selectedR: number[] = [];
    y: string = "0";

    points: Point[] = [];

    constructor(
        private pointService: PointService,
        private auth: AuthService,
        private msg: MessageService
    ) {}

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

    validateY(event: any) {
        let val = event.target.value.replace(/[^0-9.-]/g, '');
        if ((val.match(/\./g) || []).length > 1) val = val.substr(0, val.lastIndexOf("."));
        this.y = val;
    }

    drawGraph() {
        if (!this.canvasRef) return;
        const canvas = this.canvasRef.nativeElement;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const w = canvas.width;
        const h = canvas.height;

        const maxRForScale = this.selectedR.length > 0 ? Math.max(...this.selectedR) : 3;
        const margin = 35;
        const scale = (w / 2 - margin) / maxRForScale;

        ctx.clearRect(0, 0, w, h);

        this.selectedR.forEach(r => {
            if (r > 0) {
                const rVal = r * scale;
                ctx.fillStyle = 'rgba(63, 145, 255, 0.3)';

                ctx.beginPath();
                ctx.moveTo(w / 2, h / 2);
                ctx.arc(w / 2, h / 2, rVal / 2, -Math.PI / 2, 0, false);
                ctx.fill();

                ctx.fillRect(w / 2 - rVal, h / 2 - rVal / 2, rVal, rVal / 2);

                ctx.beginPath();
                ctx.moveTo(w / 2, h / 2);
                ctx.lineTo(w / 2 - rVal, h / 2);
                ctx.lineTo(w / 2, h / 2 + rVal / 2);
                ctx.closePath();
                ctx.fill();
            }
        });

        this.drawAxes(ctx, w, h, scale, maxRForScale);

        this.points.forEach(p => {
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

    drawAxes(ctx: CanvasRenderingContext2D, w: number, h: number, scale: number, r: number) {
        ctx.strokeStyle = '#333';
        ctx.lineWidth = 1.5;
        ctx.fillStyle = '#333';
        ctx.font = '12px Arial';

        // X Axis
        ctx.beginPath();
        ctx.moveTo(0, h / 2); ctx.lineTo(w, h / 2);
        ctx.stroke();

        // Y Axis
        ctx.beginPath();
        ctx.moveTo(w / 2, 0); ctx.lineTo(w / 2, h);
        ctx.stroke();

        const rVal = r * scale;
        const rHalf = rVal / 2;

        const marksX = [
            {pos: w/2 - rVal, label: '-' + r},
            {pos: w/2 - rHalf, label: '-' + r/2},
            {pos: w/2 + rHalf, label: '' + r/2},
            {pos: w/2 + rVal, label: '' + r}
        ];

        marksX.forEach(m => {
            ctx.beginPath();
            ctx.moveTo(m.pos, h/2 - 5); ctx.lineTo(m.pos, h/2 + 5);
            ctx.stroke();
            ctx.fillText(m.label, m.pos - 10, h/2 + 20);
        });

        const marksY = [
            {pos: h/2 - rVal, label: '' + r},
            {pos: h/2 - rHalf, label: '' + r/2},
            {pos: h/2 + rHalf, label: '-' + r/2},
            {pos: h/2 + rVal, label: '-' + r}
        ];

        marksY.forEach(m => {
            ctx.beginPath();
            ctx.moveTo(w/2 - 5, m.pos); ctx.lineTo(w/2 + 5, m.pos);
            ctx.stroke();
            ctx.fillText(m.label, w/2 + 10, m.pos + 5);
        });
    }

    onCanvasClick(event: MouseEvent) {
        if (this.selectedR.length !== 1) {
            this.msg.add({severity:'warn', summary:'Внимание', detail:'Для клика по графику выберите ровно один R'});
            return;
        }

        const r = this.selectedR[0];
        const canvas = this.canvasRef.nativeElement;
        const rect = canvas.getBoundingClientRect();
        const clickX = event.clientX - rect.left;
        const clickY = event.clientY - rect.top;

        const w = canvas.width;
        const h = canvas.height;
        const maxR = Math.max(...this.selectedR);
        const scale = (w / 2 - 35) / maxR;

        const graphX = (clickX - w / 2) / scale;
        const graphY = (h / 2 - clickY) / scale;

        this.submitPoint(graphX, graphY, r);
    }

    submitForm() {
        const yVal = parseFloat(this.y);
        if (isNaN(yVal) || yVal <= -3 || yVal >= 3) {
            this.msg.add({severity:'error', summary:'Ошибка', detail:'Y должен быть числом в интервале (-3; 3)'});
            return;
        }
        if (this.selectedX.length === 0 || this.selectedR.length === 0) {
            this.msg.add({severity:'warn', summary:'Ошибка', detail:'Выберите X и R'});
            return;
        }
        this.selectedX.forEach(x => {
            this.selectedR.forEach(r => {
                this.submitPoint(x, yVal, r);
            });
        });
    }

    submitPoint(x: number, y: number, r: number) {
        this.pointService.addPoint({ x, y, r }).subscribe({
            next: (newPoint) => {
                this.points = [newPoint, ...this.points];
                this.drawGraph();
            },
            error: () => {
                this.msg.add({severity:'error', summary:'Ошибка', detail:'Не удалось отправить точку'});
            }
        });
    }

    logout() {
        this.auth.logout();
    }
}