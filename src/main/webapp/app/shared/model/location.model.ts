export interface ILocation {
  id?: number;
  location?: string | null;
  imageUrl?: string | null;
}

export class Location implements ILocation {
  constructor(
    public id?: number,
    public location?: string | null,
    public imageUrl?: string | null,
  ) {}
}
