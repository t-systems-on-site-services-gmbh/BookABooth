export interface ISystem {
  id?: number;
  enabled?: boolean | null;
}

export class System implements ISystem {
  constructor(
    public id?: number,
    public enabled?: boolean | null,
  ) {
    this.enabled = this.enabled ?? false;
  }
}
