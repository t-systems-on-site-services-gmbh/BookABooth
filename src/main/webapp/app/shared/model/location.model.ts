export interface ILocation {
  id?: number;
  location?: string | null;
  imageUrl?: string | null;
  amount?: number;
  booked?: number;
}

export class Location implements ILocation {
  constructor(
    public id?: number,
    public location?: string | null,
    public imageUrl?: string | null,
    public amount?: number,
    public booked?: number,
  ) {}
}
