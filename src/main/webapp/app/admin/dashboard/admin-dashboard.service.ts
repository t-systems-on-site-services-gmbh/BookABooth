import axios from 'axios';

import type { IAdminChecklist } from '@/shared/model/admin-checklist.model';

const checklistUrl = 'api/admin-dashboard/checklist';
const excelUrl = 'api/bookings/downloadexcel';

export default class AdminDashboardService {
  public checklist(): Promise<IAdminChecklist> {
    return new Promise<IAdminChecklist>((resolve, reject) => {
      axios
        .get(`${checklistUrl}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public async downloadExcel(): Promise<void> {
    debug;
    const response = await axios.get(`${excelUrl}`, { responseType: 'blob' });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'report.xlsx');
    document.body.appendChild(link);
    link.click();
    link.remove();
  }
}
