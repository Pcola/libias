import { EnvConfig } from '../../../../../tools/env/env-config.interface';

export const Config: EnvConfig = JSON.parse('<%= ENV_CONFIG %>');
export const LIBIAS_REST_URL = window.location.origin + '/libias-rest';
export const LIBIAS_URL = '/libias';
