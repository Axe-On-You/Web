import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
    // Проверь порт! У тебя бэкенд на 8080
    private apiUrl = 'http://localhost:8080/backend/api/auth';

    constructor(private http: HttpClient, private router: Router) {}

    login(user: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/login`, user);
    }

    register(user: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/register`, user);
    }

    saveToken(token: string) {
        localStorage.setItem('auth_token', token);
    }

    getToken() {
        return localStorage.getItem('auth_token');
    }

    isLoggedIn(): boolean {
        return !!this.getToken();
    }

    logout() {
        localStorage.removeItem('auth_token');
        this.router.navigate(['/login']);
    }
}