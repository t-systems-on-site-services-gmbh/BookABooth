import type { BookingStatus } from '@/enumerations/booking-status.model';

export interface IUserChecklist {
  verified?: boolean;
  address?: boolean;
  logo?: boolean;
  phoneNumber?: boolean;
  companyDescription?: boolean;
  bookingStatus?: BookingStatus;
  boothId?: number;
  mandatoryComplete?: boolean;
}

export class Location implements IUserChecklist {
  constructor(
    public verified?: boolean,
    public address?: boolean,
    public logo?: boolean,
    public phoneNumber?: boolean,
    public companyDescription?: boolean,
    public bookingStatus?: BookingStatus,
    public boothId?: number,
    public mandatoryComplete?: boolean,
  ) {}
}
