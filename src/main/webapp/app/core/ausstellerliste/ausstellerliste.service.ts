import axios from 'axios';
import { type ICompany } from '@/shared/model/company.model';

const baseApiUrl = 'api/ausstellerliste';

export default class Ausstellerlisteservice {
  public getPublicCompanies(): Promise<ICompany[]> {
    return new Promise<ICompany[]>((resolve, reject) => {
      axios
        .get<ICompany[]>(baseApiUrl)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          console.error('Fehler beim Abrufen der Unternehmensdaten:', err);
          reject(err);
        });
    });
  }
}
