import { type ICompany } from '@/shared/model/company.model';
import { type IBooth } from '@/shared/model/booth.model';
import { type BookingStatus } from '@/shared/model/enumerations/booking-status.model';

export interface IBooking {
  id?: number;
  received?: Date | null;
  confirmed?: Date | null;
  status?: keyof typeof BookingStatus | null;
  company?: ICompany;
  booth?: IBooth;
  price?: number | null;
  cancellationfee?: number | null;
}

export class Booking implements IBooking {
  constructor(
    public id?: number,
    public received?: Date | null,
    public confirmed?: Date | null,
    public status?: keyof typeof BookingStatus | null,
    public company?: ICompany,
    public booth?: IBooth,
    public price?: number | null,
    public cancellationfee?: number | null,
  ) {}
}
