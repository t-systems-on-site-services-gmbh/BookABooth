export interface ICompany {
  id?: number;
  name?: string | null;
  mail?: string | null;
  billingAddressRow1?: string | null;
  billingAddressRow2?: string | null;
  billingAddressRow3?: string | null;
  billingAddressRow4?: string | null;
  billingZipCode?: string | null;
  billingCity?: string | null;
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
    public billingAddressRow1?: string | null,
    public billingAddressRow2?: string | null,
    public billingAddressRow3?: string | null,
    public billingAddressRow4?: string | null,
    public billingZipCode?: string | null,
    public billingCity?: string | null,
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
