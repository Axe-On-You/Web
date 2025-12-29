import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Point {
    x: number;
    y: number;
    r: number;
    hit?: boolean;
    created_at?: string;
}

@Injectable({ providedIn: 'root' })
export class PointService {
    private apiUrl = 'http://localhost:8080/backend/api/points';

    constructor(private http: HttpClient, private auth: AuthService) {}

    private getHeaders() {
        const token = this.auth.getToken();
        return new HttpHeaders().set('Authorization', `Bearer ${token}`);
    }

    getPoints(): Observable<Point[]> {
        return this.http.get<Point[]>(this.apiUrl, { headers: this.getHeaders() });
    }

    addPoint(point: Point): Observable<Point> {
        return this.http.post<Point>(this.apiUrl, point, { headers: this.getHeaders() });
    }
}