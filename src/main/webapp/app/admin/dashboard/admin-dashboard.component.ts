import { defineComponent, inject, ref, type Ref } from 'vue';

import type { ILocation } from '@/shared/model/location.model';
import LocationService from '@/entities/location/location.service';
import type { IAdminChecklist } from '@/shared/model/admin-checklist.model';
import AdminDashboardService from '@/admin/dashboard/admin-dashboard.service';

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
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init(): void {
      //this.items.push({ name: 'Greeting', value: 'Moin World' });
    },
  },
});
