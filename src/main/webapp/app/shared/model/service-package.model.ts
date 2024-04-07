import { type IBooth } from '@/shared/model/booth.model';

export interface IServicePackage {
  id?: number;
  name?: string | null;
  price?: number | null;
  description?: string | null;
  booths?: IBooth[] | null;
}

export class ServicePackage implements IServicePackage {
  constructor(
    public id?: number,
    public name?: string | null,
    public price?: number | null,
    public description?: string | null,
    public booths?: IBooth[] | null,
  ) {}
}
