export interface IAdminChecklist {
  companyName?: string;
  address?: boolean;
  logo?: boolean;
  phoneNumber?: boolean;
  description?: boolean;
  booth?: string;
}

export class Location implements IAdminChecklist {
  constructor(
    public companyName?: string,
    public address?: boolean,
    public logo?: boolean,
    public phoneNumber?: boolean,
    public description?: boolean,
    public booth?: string,
  ) {}
}
