import { type ICompany } from '@/shared/model/company.model';
import { type IBooth } from '@/shared/model/booth.model';

import { type BookingStatus } from '@/shared/model/enumerations/booking-status.model';
export interface IBooking {
  id?: number;
  received?: Date | null;
  status?: keyof typeof BookingStatus | null;
  company?: ICompany;
  booth?: IBooth;
}

export class Booking implements IBooking {
  constructor(
    public id?: number,
    public received?: Date | null,
    public status?: keyof typeof BookingStatus | null,
    public company?: ICompany,
    public booth?: IBooth,
  ) {}
}
