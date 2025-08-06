import { assignIn } from 'lodash';

export class Account {
  login: string;
  authorities: Array<string>;
  authenticated = true;
  constructor(account?: { login: string, authorities: Array<string> }) {
    if (account) {
      assignIn(this, account);
      this.authenticated = false;
    }
  }
}
