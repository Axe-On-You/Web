import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { PointService } from '../../services/point.service';

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
    imports: [CommonModule, FormsModule, ButtonModule, TableModule, MultiSelectModule, InputTextModule, CardModule, ToastModule],
    providers: [MessageService],
    templateUrl: './main.html'
})
export class MainComponent implements OnInit, AfterViewInit {
    @ViewChild('canvasGraph', { static: false }) canvasRef!: ElementRef<HTMLCanvasElement>;

    xOptions = [
        { label: '-5', val: -5 }, { label: '-4', val: -4 }, { label: '-3', val: -3 },
        { label: '-2', val: -2 }, { label: '-1', val: -1 }, { label: '0', val: 0 },
        { label: '1', val: 1 }, { label: '2', val: 2 }, { label: '3', val: 3 }
    ];
    rOptions = [
        { label: '1', val: 1 }, { label: '2', val: 2 }, { label: '3', val: 3 }
    ];

    selectedX: number[] = [];
    selectedR: number[] = [];
    y: string = "0";
    points: any[] = [];

    constructor(
        private pointService: PointService,
        private auth: AuthService,
        private msg: MessageService,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit() { this.loadPoints(); }
    ngAfterViewInit() { this.drawGraph(); }

    loadPoints() {
        this.pointService.getPoints().subscribe(data => {
            this.points = data;
            this.drawGraph();
        });
    }

    validateY(event: any) {
        let val = event.target.value.replace(/[^0-9.-]/g, '');
        if ((val.match(/\./g) || []).length > 1) val = val.substring(0, val.lastIndexOf("."));
        this.y = val;
    }

    drawGraph() {
        if (!this.canvasRef) return;
        const canvas = this.canvasRef.nativeElement;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const w = canvas.width;
        const h = canvas.height;

        const hasR = this.selectedR.length > 0;
        // Если R не выбран, рисуем для R = 3
        const drawRList = hasR ? this.selectedR : [3];
        const maxRScale = Math.max(...drawRList);

        const margin = 35;
        const scale = (w / 2 - margin) / maxRScale;

        ctx.clearRect(0, 0, w, h);

        drawRList.forEach(r => {
            const rVal = r * scale;
            ctx.fillStyle = 'rgba(63, 145, 255, 0.3)';
            ctx.beginPath();
            ctx.moveTo(w/2, h/2);
            ctx.arc(w/2, h/2, rVal/2, -Math.PI/2, 0, false);
            ctx.fill();
            ctx.fillRect(w/2 - rVal, h/2 - rVal/2, rVal, rVal/2);
            ctx.beginPath();
            ctx.moveTo(w/2, h/2);
            ctx.lineTo(w/2 - rVal, h/2);
            ctx.lineTo(w/2, h/2 + rVal/2);
            ctx.fill();
        });

        this.drawAxes(ctx, w, h, scale, maxRScale);

        if (hasR) {
            this.points.forEach(p => {
                if (this.selectedR.includes(p.r)) {
                    const px = w / 2 + p.x * scale;
                    const py = h / 2 - p.y * scale;
                    ctx.beginPath();
                    ctx.arc(px, py, 4, 0, 2 * Math.PI);
                    ctx.fillStyle = p.hit ? '#4CAF50' : '#F44336';
                    ctx.fill();
                    ctx.strokeStyle = '#fff';
                    ctx.stroke();
                }
            });
        }
    }

    drawAxes(ctx: CanvasRenderingContext2D, w: number, h: number, scale: number, r: number) {
        ctx.strokeStyle = '#333';
        ctx.fillStyle = '#333';
        ctx.font = '12px Arial';
        ctx.beginPath();
        ctx.moveTo(0, h/2); ctx.lineTo(w, h/2);
        ctx.moveTo(w/2, 0); ctx.lineTo(w/2, h);
        ctx.stroke();

        const rVal = r * scale;
        const labels = [
            {x: w/2 - rVal, y: h/2, txt: '-'+r},
            {x: w/2 - rVal/2, y: h/2, txt: '-'+r/2},
            {x: w/2 + rVal/2, y: h/2, txt: r/2},
            {x: w/2 + rVal, y: h/2, txt: r},
            {x: w/2, y: h/2 - rVal, txt: r},
            {x: w/2, y: h/2 - rVal/2, txt: r/2},
            {x: w/2, y: h/2 + rVal/2, txt: '-'+r/2},
            {x: w/2, y: h/2 + rVal, txt: '-'+r}
        ];
        labels.forEach(l => {
            ctx.beginPath();
            ctx.arc(l.x, l.y, 2, 0, 2*Math.PI);
            ctx.fill();
            ctx.fillText(l.txt.toString(), l.x + 5, l.y - 5);
        });
    }

    onCanvasClick(event: MouseEvent) {
        if (this.selectedR.length !== 1) {
            this.msg.add({severity:'warn', summary:'Инфо', detail:'Выберите один R для клика'});
            return;
        }
        const r = this.selectedR[0];
        const rect = this.canvasRef.nativeElement.getBoundingClientRect();
        const scale = (this.canvasRef.nativeElement.width / 2 - 35) / r;
        const x = (event.clientX - rect.left - 150) / scale;
        const y = (150 - (event.clientY - rect.top)) / scale;
        this.submitPoint(x, y, r);
    }

    submitForm() {
        const yVal = parseFloat(this.y);
        if (isNaN(yVal) || yVal <= -3 || yVal >= 3) {
            this.msg.add({severity:'error', summary:'Валидация', detail:'Y должен быть в (-3; 3)'});
            return;
        }
        if (!this.selectedX.length || !this.selectedR.length) {
            this.msg.add({severity:'warn', summary:'Ошибка', detail:'Выберите X и R'});
            return;
        }
        this.selectedX.forEach(x => {
            this.selectedR.forEach(r => this.submitPoint(x, yVal, r));
        });
    }

    submitPoint(x: number, y: number, r: number) {
        this.pointService.addPoint({ x, y, r }).subscribe({
            next: (res: any) => {
                const newP = {
                    ...res,
                    createdAt: res.createdAt || res.created_at || new Date().toISOString()
                };
                this.points = [newP, ...this.points];
                this.cdr.detectChanges();
                this.drawGraph();
            },
            error: () => this.msg.add({severity:'error', summary:'Сервер', detail:'Ошибка отправки'})
        });
    }

    logout() { this.auth.logout(); }
}