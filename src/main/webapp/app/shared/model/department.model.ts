import { type ICompany } from '@/shared/model/company.model';

export interface IDepartment {
  id?: number;
  name?: string | null;
  description?: string | null;
  companies?: ICompany[] | null;
}

export class Department implements IDepartment {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public companies?: ICompany[] | null,
  ) {}
}
