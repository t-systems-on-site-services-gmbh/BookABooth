import { type IDepartment } from '@/shared/model/department.model';
import { type IContact } from '@/shared/model/contact.model';

export interface ICompany {
  id?: number;
  name?: string | null;
  mail?: string | null;
  billingAddress?: string | null;
  logo?: string | null;
  description?: string | null;
  waitingList?: boolean | null;
  exhibitorList?: boolean | null;
  departments?: IDepartment[] | null;
  contacts?: IContact[] | null;
}

export class Company implements ICompany {
  constructor(
    public id?: number,
    public name?: string | null,
    public mail?: string | null,
    public billingAddress?: string | null,
    public logo?: string | null,
    public description?: string | null,
    public waitingList?: boolean | null,
    public exhibitorList?: boolean | null,
    public departments?: IDepartment[] | null,
    public contacts?: IContact[] | null,
  ) {
    this.waitingList = this.waitingList ?? false;
    this.exhibitorList = this.exhibitorList ?? false;
  }
}
