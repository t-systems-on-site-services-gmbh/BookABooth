import { defineComponent, inject, onMounted, ref, type Ref, computed } from 'vue';

import BoothService from './bookabooth.service';
import { type IBooth } from '@/shared/model/booth.model';
import { useAlertService } from '@/shared/alert/alert.service';
import LocationService from '@/entities/location/location.service';
import { type ILocation } from '@/shared/model/location.model';
import ServicePackageService from '@/entities/service-package/service-package.service';
import { type IServicePackage } from '@/shared/model/service-package.model';
import { error } from 'console';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Booth',
  setup() {
    const boothService = inject('boothService', () => new BoothService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const locationService = inject('locationService', () => new LocationService());
    const locations: Ref<ILocation[]> = ref([]);

    const servicePackageService = inject('servicePackageService', () => new ServicePackageService());
    const servicePackages: Ref<IServicePackage[]> = ref([]);

    const booths: Ref<IBooth[]> = ref([]);

    const isFetching = ref(false);

    const selectedLocation = ref('');

    const selectedBooth = ref();

    const clear = () => {};

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

    const handleSyncList = () => {
      retrieveBooths();
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

    initRelationships();

    onMounted(async () => {
      await retrieveBooths();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IBooth) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeBooth = async () => {
      try {
        await boothService().delete(removeId.value);
        const message = 'A Booth is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveBooths();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const filteredBooths = computed(() => {
      console.log(selectedLocation.value);
      if (selectedLocation.value) {
        return booths.value.filter(booth => booth.location.id === selectedLocation.value.id);
      }
      return booths.value;
    });

    return {
      booths,
      handleSyncList,
      isFetching,
      retrieveBooths,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeBooth,
      locations,
      servicePackages,
      filteredBooths,
      selectedLocation,
      selectedBooth,
      calculatePrice,
    };
  },
  methods: {
    showConfirmationModal(booth: IBooth) {
      this.bookingService()
        .create(booth.id)
        .then(() => {
          this.$refs['confirmation-modal'].show();
          this.selectedBooth = booth;
        })
        .catch(error => {
          this.alertService.showHttpError(error.response);
        });
    },
    hideConfirmationModal() {
      this.$refs['confirmation-modal'].hide();
    },
    resetConfirmationModal() {
      this.selectedBooth = null;
    },
    async abortBooking(boothId: number) {
      this.hideConfirmationModal();
      this.BookingService.deleteByBooth(boothId);
    },
    async createBooking(boothId: number) {},
    async confirmBooking(boothId: number) {
      this.bookingService()
        .confirmByBooth(boothId)
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
