import { defineComponent, inject, onMounted, ref, type Ref, computed } from 'vue';

import BoothService from './bookabooth.service';
import { type IBooth } from '@/shared/model/booth.model';
import { useAlertService } from '@/shared/alert/alert.service';
import LocationService from '@/entities/location/location.service';
import { type ILocation } from '@/shared/model/location.model';
import ServicePackageService from '@/entities/service-package/service-package.service';
import { type IServicePackage } from '@/shared/model/service-package.model';
import BookingService from '@/entities/booking/booking.service';
import { type IBooking } from '@/shared/model/booking.model';
import SystemService from '@/entities/system/system.service';
import { type ISystem } from '@/shared/model/system.model';
import Ausstellerinfo from '@/core/ausstellerinfo/ausstellerinfo.vue';
import useVuelidate from '@vuelidate/core';
import { required } from '@vuelidate/validators';
import axios from 'axios';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Booth',
  components: {
    ausstellerinfo: Ausstellerinfo,
  },
  setup() {
    const systemService = inject('systemService', () => new SystemService());
    const boothService = inject('boothService', () => new BoothService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const bookingService = inject('bookingService', () => new BookingService());
    const locationService = inject('locationService', () => new LocationService());
    const servicePackageService = inject('servicePackageService', () => new ServicePackageService());

    const locations: Ref<ILocation[]> = ref([]);
    const servicePackages: Ref<IServicePackage[]> = ref([]);
    const booths: Ref<IBooth[]> = ref([]);
    const unavailableBooths: Ref<IBooth[]> = ref([]);
    const isFetching = ref(false);
    const selectedLocation = ref('');
    const selectedBooth: Ref<IBooth> = ref();
    const myBooking: Ref<IBooking> = ref();
    const currentBooking: Ref<IBooking> = ref();
    const checklistIncomplete = ref(false);
    const system: Ref<ISystem> = ref();
    const isBookingAllowed = computed(() => checklistIncomplete.value && system.value.enabled);
    const boothId = ref(null);
    const componentKey = ref(new Date().getTime());
    const confirmConditions = ref(false);

    const getMyBooking = async () => {
      isFetching.value = true;
      try {
        const res = await bookingService().retrieveMyBooking();
        myBooking.value = res;
      } catch (err) {
        console.error(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const retrieveBooths = async () => {
      isFetching.value = true;
      try {
        const res = await boothService().retrieve();
        booths.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const getUnavailableBooths = async () => {
      isFetching.value = true;
      try {
        const res = await bookingService().retrieveUnavailableBooths();
        unavailableBooths.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const calculatePrice = (booth: IBooth) => {
      var boothPrice = 0;
      booth.servicePackages.forEach((servicePackage: { id: number }) => {
        boothPrice += servicePackages.value.find(sp => sp.id === servicePackage.id).price;
      });
      return boothPrice;
    };

    const initRelationships = () => {
      locationService()
        .retrieve()
        .then((res: { data: ILocation[] }) => {
          locations.value = res.data;
        });
      servicePackageService()
        .retrieve()
        .then((res: { data: IServicePackage[] }) => {
          servicePackages.value = res.data;
        });
    };

    const retrieveSystem = async () => {
      try {
        const res = await systemService().retrieve();
        system.value = res.data;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const fetchUserChecklist = async () => {
      try {
        const response = await axios.get('api/checklist');
        const checklist = response.data;

        checklistIncomplete.value =
          checklist.verified && checklist.address && checklist.logo && checklist.phoneNumber && checklist.companyDescription;

        boothId.value = checklist.boothId;
      } catch (error) {
        console.error('Fehler beim Abrufen der Checkliste:', error);
      }
    };

    const checkBookingAllowed = () => {
      retrieveSystem();
      fetchUserChecklist();
    };

    initRelationships();
    checkBookingAllowed();
    getMyBooking();

    onMounted(async () => {
      await retrieveBooths();
      await getUnavailableBooths();
    });

    const filteredBooths = computed(() => {
      if (selectedLocation.value) {
        return booths.value.filter(booth => booth.location?.id === selectedLocation.value.id);
      }
      return booths.value;
    });

    // filter for service packages that have an entry for booths
    const filteredServicePackages = computed(() => {
      return servicePackages.value.filter(servicePackage => servicePackage.booths.length > 0);
    });

    const rules = {
      confirmConditions: { required },
    };

    const v$ = useVuelidate(rules, { confirmConditions });

    return {
      alertService,
      booths,
      unavailableBooths,
      getUnavailableBooths,
      isFetching,
      myBooking,
      retrieveBooths,
      retrieveSystem,
      locations,
      servicePackages,
      filteredBooths,
      selectedLocation,
      selectedBooth,
      currentBooking,
      calculatePrice,
      bookingService,
      isBookingAllowed,
      system,
      boothId,
      componentKey,
      filteredServicePackages,
      confirmConditions,
      v$,
    };
  },
  methods: {
    async displayConfirmationModal(booth: IBooth) {
      await this.retrieveSystem();
      if (!this.system.enabled) {
        this.alertService.showErrorNoHide('Die Standbuchung wurde systemseitig gesperrt.');
        return;
      }
      await this.getUnavailableBooths();
      if (this.unavailableBooths.find(b => b.id === booth.id)) {
        this.alertService.showErrorNoHide('Der Stand wurde in der Zwischenzeit geblockt oder gebucht.');
        return;
      }
      this.bookingService()
        .create(booth.id)
        .then((res: { data: IBooking }) => {
          this.currentBooking = res;
          this.selectedBooth = booth;
          this.showConfirmationModal();
        })
        .catch(error => {
          console.log(error);
        });
    },
    showInfoModal() {
      this.$refs['ausstellerinfo-modal'].show();
    },
    hideInfoModal() {
      this.$refs['ausstellerinfo-modal'].hide();
    },
    showLageplanModal() {
      this.$refs['lageplan-modal'].show();
    },
    hideLageplanModal() {
      this.$refs['lageplan-modal'].hide();
    },
    showConfirmationModal() {
      this.$refs['confirmation-modal'].show();
    },
    hideConfirmationModal() {
      this.$refs['confirmation-modal'].hide();
    },
    resetConfirmationModal() {
      this.selectedBooth = null;
      this.currentBooking = null;
    },
    async abortBooking(bookingId: number) {
      this.hideConfirmationModal();
      this.bookingService().delete(bookingId);
    },
    async confirmBooking(bookingId: number) {
      this.bookingService()
        .confirm(bookingId)
        .then(() => {
          this.hideConfirmationModal();
          this.$router.push({ path: '/' }).then(() => {
            this.$router.go(0);
          });
        })
        .catch(error => {
          this.alertService.showHttpError(error.response);
        });
    },
    formatDate(dateString: string): string {
      const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: '2-digit', year: 'numeric' };
      return new Date(dateString).toLocaleDateString('de-DE', options);
    },
    formatCurrency(value: number): string {
      return new Intl.NumberFormat('de-DE', { style: 'currency', currency: 'EUR' }).format(value);
    },
  },
});
