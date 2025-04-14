export interface IPrivacyPolicy {
  id?: number;
  fromDate?: Date | null;
}

export class PrivacyPolicy implements IPrivacyPolicy {
  constructor(
    public id?: number,
    public fromDate?: Date | null,
  ) {}
}
