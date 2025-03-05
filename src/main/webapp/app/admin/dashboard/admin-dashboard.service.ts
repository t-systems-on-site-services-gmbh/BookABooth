import axios from 'axios';

import type { IAdminChecklist } from '@/shared/model/admin-checklist.model';

const dataUrl = 'api/admin-dashboard/data';
const excelUrl = 'api/bookings/downloadexcel';

export default class AdminDashboardService {
  public checklist(): Promise<IAdminChecklist> {
    return new Promise<IAdminChecklist>((resolve, reject) => {
      axios
        .get(`${dataUrl}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
