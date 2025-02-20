export interface ICompany {
  id?: number;
  name?: string | null;
  mail?: string | null;
  billingAddress?: string | null;
  logo?: string | null;
  description?: string | null;
  comment?: string | null;
  waitingList?: boolean | null;
  exhibitorList?: boolean | null;
}

export class Company implements ICompany {
  constructor(
    public id?: number,
    public name?: string | null,
    public mail?: string | null,
    public billingAddress?: string | null,
    public logo?: string | null,
    public description?: string | null,
    public comment?: string | null,
    public waitingList?: boolean | null,
    public exhibitorList?: boolean | null,
  ) {
    this.waitingList = this.waitingList ?? false;
    this.exhibitorList = this.exhibitorList ?? false;
  }
}
