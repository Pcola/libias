import { assignIn } from 'lodash';

export class SecurityToken {
  publicSecret: string;
  securityLevel: string;

  constructor(token: { publicSecret: string, securityLevel: string }) {
    assignIn(this, token);
  }

  isEncoding(encoding: string): boolean {
    return this.securityLevel && this.securityLevel === encoding;
  }

}
