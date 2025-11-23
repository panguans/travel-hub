import { IUser } from 'app/shared/model/user.model';

export interface IAgency {
  id?: number;
  description?: string | null;
  address?: string | null;
  website?: string | null;
  ratingAvg?: number | null;
  user_agency?: IUser | null;
}

export const defaultValue: Readonly<IAgency> = {};
