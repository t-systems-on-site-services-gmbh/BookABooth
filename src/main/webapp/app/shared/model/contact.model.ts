import { type ICompany } from '@/shared/model/company.model';

import { type ContactResponsibility } from '@/shared/model/enumerations/contact-responsibility.model';
export interface IContact {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  mail?: string | null;
  phone?: string | null;
  responsibility?: keyof typeof ContactResponsibility | null;
  note?: string | null;
  companies?: ICompany[] | null;
}

export class Contact implements IContact {
  constructor(
    public id?: number,
    public firstName?: string | null,
    public lastName?: string | null,
    public mail?: string | null,
    public phone?: string | null,
    public responsibility?: keyof typeof ContactResponsibility | null,
    public note?: string | null,
    public companies?: ICompany[] | null,
  ) {}
}
