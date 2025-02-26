import { defineComponent, inject, ref, type Ref } from 'vue';

import type { ILocation } from '@/shared/model/location.model';
import LocationService from '@/entities/location/location.service';
import type { IAdminChecklist } from '@/shared/model/admin-checklist.model';
import AdminDashboardService from '@/admin/dashboard/admin-dashboard.service';
import axios from 'axios';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AdminDashboard',
  setup() {
    const locationService = inject('locationService', () => new LocationService());
    const adminDashboardService = inject('adminDashboardService', () => new AdminDashboardService());

    const locations: Ref<ILocation[]> = ref([]);
    const checklists: Ref<IAdminChecklist> = ref({});

    const initRelationships = () => {
      locationService()
        .retrieve()
        .then((res: { data: ILocation[] }) => {
          locations.value = res.data;
        });
      adminDashboardService()
        .checklist()
        .then((res: { data: IAdminChecklist[] }) => {
          checklists.value = res;
        });
    };

    initRelationships();

    return {
      locations,
      checklists,
      adminDashboardService,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init(): void {},
    async downloadExcel(): void {
      const excelUrl = 'api/bookings/downloadexcel';
      const response = await axios.get(`${excelUrl}`, { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'report.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
    },
  },
});
