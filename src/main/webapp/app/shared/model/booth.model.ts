import { type ILocation } from '@/shared/model/location.model';
import { type IServicePackage } from '@/shared/model/service-package.model';

export interface IBooth {
  id?: number;
  title?: string;
  ceilingHeight?: number | null;
  available?: boolean;
  location?: ILocation | null;
  servicePackages?: IServicePackage[] | null;
}

export class Booth implements IBooth {
  constructor(
    public id?: number,
    public title?: string,
    public ceilingHeight?: number | null,
    public available?: boolean,
    public location?: ILocation | null,
    public servicePackages?: IServicePackage[] | null,
  ) {
    this.available = this.available ?? false;
  }
}
