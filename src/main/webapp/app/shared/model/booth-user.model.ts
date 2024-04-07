import { type IUser } from '@/shared/model/user.model';
import { type ICompany } from '@/shared/model/company.model';

export interface IBoothUser {
  id?: number;
  phone?: string | null;
  note?: string | null;
  verificationCode?: string | null;
  verified?: Date | null;
  lastLogin?: Date | null;
  disabled?: boolean | null;
  user?: IUser | null;
  company?: ICompany | null;
}

export class BoothUser implements IBoothUser {
  constructor(
    public id?: number,
    public phone?: string | null,
    public note?: string | null,
    public verificationCode?: string | null,
    public verified?: Date | null,
    public lastLogin?: Date | null,
    public disabled?: boolean | null,
    public user?: IUser | null,
    public company?: ICompany | null,
  ) {
    this.disabled = this.disabled ?? false;
  }
}
