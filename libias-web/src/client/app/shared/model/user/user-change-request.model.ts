export class UserChangeRequest {
  username: string;
  firstName: string;
  lastName: string;
  password: string;
  userRoleIds: number[];
  workplaceId: string;
  active: boolean;
}
