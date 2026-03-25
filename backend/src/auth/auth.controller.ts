import { BadRequestException, Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { AuthService } from './auth.service';

@Controller('auth')
export class AuthController {
  constructor(private authService: AuthService) {}

  @HttpCode(HttpStatus.OK)
  @Post('login')
  signIn(@Body() signInDto: Record<string, any>) {
    console.log('Received sign-in request:', signInDto);
    if(!signInDto.email || !signInDto.password){
      console.warn('Missing email or password in the sign-in request');
      throw new BadRequestException("Email and password are required");
      // res.status(HttpStatus.BAD_REQUEST);
      // return {message: "Email and password are required"};
    }
    return this.authService.signIn(signInDto.email, signInDto.password);
  }
}
