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
    const checklists: Ref<IAdminChecklist[]> = ref([]);
    const bccForAllUsers: Ref<string> = ref('');
    const countAllProfiles: Ref<number> = ref(0);
    const bccForAllUsersWithIncompleteProfile: Ref<string> = ref('');
    const countIncompleteProfiles: Ref<number> = ref(0);

    const initRelationships = () => {
      locationService()
        .retrieve()
        .then((res: { data: ILocation[] }) => {
          locations.value = res.data;
        });

      adminDashboardService()
        .checklist()
        .then((res: { data: IAdminChecklist[] }) => {
          checklists.value = res.data;
          const ListOfUsersWithIncompleteProfile: string[] = [];
          const ListOfUsers: string[] = [];

          res.checklist.forEach((c: IAdminChecklist) => {
            if (!c.mandatoryComplete && c.mail != null) {
              ListOfUsersWithIncompleteProfile.push(c.mail);
            }

            if (c.mail != null) {
              ListOfUsers.push(c.mail);
            }
          });
          bccForAllUsers.value = 'mailTo:?bcc=' + ListOfUsers.join(';') + '&subject=Vollständiges Profil&body=Moin!';
          countAllProfiles.value = ListOfUsers.length;
          bccForAllUsersWithIncompleteProfile.value =
            'mailTo:?bcc=' + ListOfUsersWithIncompleteProfile.join(';') + '&subject=Vollständiges Profil&body=Moin!';
          countIncompleteProfiles.value = ListOfUsersWithIncompleteProfile.length;
        })
        .catch((error: any) => {
          console.error('Error fetching checklist:', error);
        });
    };

    initRelationships();

    return {
      locations,
      checklists,
      adminDashboardService,
      bccForAllUsers,
      bccForAllUsersWithIncompleteProfile,
      countAllProfiles,
      countIncompleteProfiles,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init(): void {},
    async downloadExcel(): Promise<void> {
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
