import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, InputTextModule, PasswordModule, ButtonModule, CardModule, ToastModule],
  providers: [MessageService],
  templateUrl: './login.html'
})
export class LoginComponent {
  username = '';
  password = '';

  constructor(private auth: AuthService, private router: Router, private msg: MessageService) {}

  onLogin() {
    if (!this.username.trim() || !this.password.trim()) {
      this.msg.add({ severity: 'warn', summary: 'Ошибка', detail: 'Поля не могут быть пустыми' });
      return;
    }
    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: (res: any) => {
        this.auth.saveToken(res.token);
        this.router.navigate(['/main']);
      },
      error: () => this.showError('Неверный логин или пароль')
    });
  }

  onRegister() {
    if (!this.username.trim() || !this.password.trim()) {
      this.msg.add({ severity: 'warn', summary: 'Ошибка', detail: 'Поля не могут быть пустыми' });
      return;
    }

    this.auth.register({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.msg.add({ severity: 'success', summary: 'Успех', detail: 'Вы зарегистрированы' });
        // Убираем лишние ошибки, редирект через секунду
        setTimeout(() => this.router.navigate(['/login']), 1000);
      },
      error: (err) => {
        if (err.status === 409) {
          this.msg.add({ severity: 'error', summary: 'Ошибка', detail: 'Пользователь уже существует' });
        } else {
          if (err.status === 200) {
            this.msg.add({ severity: 'success', summary: 'Успех', detail: 'Вы зарегистрированы' });
          } else {
            this.msg.add({ severity: 'error', summary: 'Ошибка', detail: 'Ошибка сервера' });
          }
        }
      }
    });
  }

  private showError(detail: string) {
    this.msg.add({ severity: 'error', summary: 'Ошибка', detail });
  }
}