export interface ISystem {
  id?: number;
  enabled?: boolean | null;
  cancellationReimbursement?: number | null;
  cancellationReimbursementUntil?: string | null;
}

export class System implements ISystem {
  constructor(
    public id?: number,
    public enabled?: boolean | null,
    public cancellationReimbursement?: number | null,
    public cancellationReimbursementUntil?: string | null,
  ) {
    this.enabled = this.enabled ?? false;
    this.cancellationReimbursement = this.cancellationReimbursement ?? 0;
    this.cancellationReimbursementUntil = this.cancellationReimbursementUntil ?? Date.now().toString();
  }
}
