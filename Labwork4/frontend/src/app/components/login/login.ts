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
    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: (res: any) => {
        this.auth.saveToken(res.token);
        this.router.navigate(['/main']);
      },
      error: () => this.showError('Неверный логин или пароль')
    });
  }

  onRegister() {
    this.auth.register({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.msg.add({ severity: 'success', summary: 'Успех', detail: 'Вы зарегистрированы' })
        setTimeout(() => this.router.navigate(['/login']), 1000);
      },
      error: () => this.showError('Пользователь уже существует')
    });
  }

  private showError(detail: string) {
    this.msg.add({ severity: 'error', summary: 'Ошибка', detail });
  }
}