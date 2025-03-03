import axios from 'axios';

import type { IUserChecklist } from '@/shared/model/user-checklist.model';

const baseApiUrl = 'api/checklist';

export default class UserChecklistService {
  public checklist(): Promise<IUserChecklist> {
    return new Promise<IUserChecklist>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
