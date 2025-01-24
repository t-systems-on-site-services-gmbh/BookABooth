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
import axios from 'axios';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Booth',
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
    const currentBooking: Ref<IBooking> = ref();
    const isBookingAllowed = ref(false);
    const system: Ref<ISystem> = ref();
    const boothId = ref(null);

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

    const handleSyncList = () => {
      retrieveBooths();
      getUnavailableBooths();
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

        isBookingAllowed.value =
          checklist.verified && checklist.address && checklist.logo && checklist.phoneNumber && checklist.companyDescription;

        boothId.value = checklist.boothId;
      } catch (error) {
        console.error('Fehler beim Abrufen der Checkliste:', error);
      }
    };

    const checkBookingAllowed = async () => {
      await retrieveSystem();
      await fetchUserChecklist();
      isBookingAllowed.value = isBookingAllowed.value && system.value.enabled;
    };

    initRelationships();
    checkBookingAllowed();

    onMounted(async () => {
      await retrieveBooths();
      await getUnavailableBooths();
    });

    const filteredBooths = computed(() => {
      console.log(selectedLocation.value);
      if (selectedLocation.value) {
        return booths.value.filter(booth => booth.location?.id === selectedLocation.value.id);
      }
      return booths.value;
    });

    return {
      booths,
      unavailableBooths,
      getUnavailableBooths,
      handleSyncList,
      isFetching,
      retrieveBooths,
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
    };
  },
  methods: {
    displayConfirmationModal(booth: IBooth) {
      this.getUnavailableBooths();
      if (this.unavailableBooths.find(b => b.id === booth.id)) {
        this.alertService.showError('Der Stand ist bereits belegt.');
        return;
      }
      this.bookingService()
        .create(booth.id)
        .then((res: { data: IBooking }) => {
          this.currentBooking = res;
          console.log(res);
          debugger;
          this.selectedBooth = booth;
          this.showConfirmationModal();
        })
        .catch(error => {
          console.log(error);
        });
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
          this.alertService.showSuccess('Buchung vorgenommen.');
        })
        .catch(error => {
          this.alertService.showHttpError(error.response);
        });
    },
  },
});
