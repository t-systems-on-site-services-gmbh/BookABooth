import axios from 'axios';

import type { IAdminChecklist } from '@/shared/model/admin-checklist.model';

const baseApiUrl = 'api/admin-dashboard/checklist';

export default class AdminDashboardService {
  public checklist(): Promise<IAdminChecklist> {
    return new Promise<IAdminChecklist>((resolve, reject) => {
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
