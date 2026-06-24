import { Controller, Post, Body, UnauthorizedException } from '@nestjs/common';
import { AuthService } from './auth.service';

@Controller('auth')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('login')
  login(@Body() body: { email: string; password: string }) {
    const result = this.authService.login(body.email, body.password);
    if (!result) throw new UnauthorizedException();
    return result;
  }
}
