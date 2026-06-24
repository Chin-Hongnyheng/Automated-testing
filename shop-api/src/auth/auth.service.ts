import { Injectable } from '@nestjs/common';

@Injectable()
export class AuthService {
  login(email: string, password: string) {
    // simple hardcoded check for now
    if (email === 'admin@orderzone.net' && password === 'secret') {
      return { access_token: 'fake-token-123' };
    }
    return null;
  }
}
