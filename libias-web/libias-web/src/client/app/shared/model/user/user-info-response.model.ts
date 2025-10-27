import { UserRole } from './userRole.model';
import { WorkplaceResponse } from '../workplace/workplace-response.model';

export class UserInfoResponse {
  username: string;
  firstName: string;
  lastName: string;
  userRoleCollection: UserRole[];
  workplaceId: string;
  workplace: WorkplaceResponse[];
  active: number;
}
