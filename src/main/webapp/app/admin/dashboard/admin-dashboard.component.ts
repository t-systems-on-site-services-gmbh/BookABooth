import { computed, defineComponent, inject, ref, type Ref } from 'vue';

import type { ILocation } from '@/shared/model/location.model';
import LocationService from '@/entities/location/location.service';
import type { IAdminChecklist } from '@/shared/model/admin-checklist.model';
import AdminDashboardService from '@/admin/dashboard/admin-dashboard.service';
import System from '@/entities/system/system.vue';
import axios from 'axios';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AdminDashboard',
  components: {
    system: System,
  },
  setup() {
    const locationService = inject('locationService', () => new LocationService());
    const adminDashboardService = inject('adminDashboardService', () => new AdminDashboardService());

    const locations: Ref<ILocation[]> = ref([]);
    const checklists: Ref<IAdminChecklist[]> = ref([]);
    const bccForAllUsers: Ref<string> = ref('');
    const countCompaniesWithBooking: Ref<number> = ref(0);
    const bccForCompaniesWithBooking: Ref<string> = ref('');
    const countAllProfiles: Ref<number> = ref(0);
    const bccForAllUsersWithIncompleteProfile: Ref<string> = ref('');
    const countIncompleteProfiles: Ref<number> = ref(0);
    const filterFirmenname: Ref<string> = ref('');
    const filterStand: Ref<string> = ref('');

    const filter = {
      filterRechnungsanschrift: ref<boolean | null>(null),
      filterLogo: ref<boolean | null>(null),
      filterTelefonnummer: ref<boolean | null>(null),
      filterKurzbeschreibung: ref<boolean | null>(null),
      filterAusstellerliste: ref<boolean | null>(null),
    };

    const initRelationships = () => {
      locationService()
        .retrieve()
        .then((res: { data: ILocation[] }) => {
          locations.value = res.data;
        });

      adminDashboardService()
        .checklist()
        .then((res: { data: IAdminChecklist[] }) => {
          checklists.value = res.checklist;
          const ListOfCompaniesWithBooking: string[] = [];
          const ListOfUsersWithIncompleteProfile: string[] = [];
          const ListOfUsers: string[] = [];

          res.checklist.forEach((c: IAdminChecklist) => {
            if (c.booth != null && c.booth.length > 0 && c.mail != null) {
              ListOfCompaniesWithBooking.push(c.mail);
            }
            if (!c.mandatoryComplete && c.mail != null) {
              ListOfUsersWithIncompleteProfile.push(c.mail);
            }

            if (c.mail != null) {
              ListOfUsers.push(c.mail);
            }
          });
          bccForAllUsers.value = 'mailTo:?bcc=' + ListOfUsers.join(';') + '&subject=Jade Karrieretag&body=Moin!';
          countAllProfiles.value = ListOfUsers.length;
          countCompaniesWithBooking.value = ListOfCompaniesWithBooking.length;
          bccForCompaniesWithBooking.value = 'mailTo:?bcc=' + ListOfCompaniesWithBooking.join(';') + '&subject=Jade Karrieretag&body=Moin!';
          bccForAllUsersWithIncompleteProfile.value =
            'mailTo:?bcc=' + ListOfUsersWithIncompleteProfile.join(';') + '&subject=Jade Karrieretag&body=Moin!';
          countIncompleteProfiles.value = ListOfUsersWithIncompleteProfile.length;
        })
        .catch((error: any) => {
          console.error('Error fetching checklist:', error);
        });
    };

    const filteredChecklists = computed(() => {
      return checklists.value.filter(
        c =>
          c.companyName?.toLowerCase().includes(filterFirmenname.value.toLowerCase()) &&
          (filter.filterRechnungsanschrift.value === null || c.address === filter.filterRechnungsanschrift.value) &&
          (filter.filterLogo.value === null || c.logo === filter.filterLogo.value) &&
          (filter.filterTelefonnummer.value === null || c.phoneNumber === filter.filterTelefonnummer.value) &&
          (filter.filterKurzbeschreibung.value === null || c.companyDescription === filter.filterKurzbeschreibung.value) &&
          (filter.filterAusstellerliste.value === null || c.onExhibitorList === filter.filterAusstellerliste.value) &&
          ((filterStand.value.trim().length == 0 && c.booth == null) || c.booth?.toLowerCase().includes(filterStand.value.toLowerCase())),
      );
    });

    function toggleTriStateFilter(key: keyof typeof filter, event: any) {
      const filterRef = filter[key];
      if (filterRef.value === null) {
        filterRef.value = true;
        event.target.indeterminate = false;
        event.target.checked = true;
      } else if (filterRef.value === true) {
        filterRef.value = false;
        event.target.indeterminate = false;
        event.target.checked = false;
      } else {
        filterRef.value = null;
        event.target.indeterminate = true;
      }
    }

    initRelationships();

    return {
      locations,
      checklists,
      filteredChecklists,
      filterFirmenname,
      filterStand,
      filter,
      toggleTriStateFilter,
      adminDashboardService,
      bccForAllUsers,
      bccForAllUsersWithIncompleteProfile,
      countAllProfiles,
      countIncompleteProfiles,
      countCompaniesWithBooking,
      bccForCompaniesWithBooking,
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
      //format Date to yyyy-mm-dd_hh:mm:ss
      const now = new Date();
      const date = now.toISOString().slice(0, 10);
      link.setAttribute('download', `Buchungen-${date}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    },
  },
});
