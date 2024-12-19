import { computed, defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import BookingService from './booking.service';
import { type IBooking } from '@/shared/model/booking.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import CompanyService from '@/entities/company/company.service';
import type { ICompany } from '@/shared/model/company.model';
import BoothService from '@/entities/booth/booth.service';
import type { IBooth } from '@/shared/model/booth.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Booking',
  setup() {
    const dateFormat = useDateFormat();
    const bookingService = inject('bookingService', () => new BookingService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const bookings: Ref<IBooking[]> = ref([]);

    const companyService = inject('companyService', () => new CompanyService());
    const companies: Ref<ICompany[]> = ref([]);

    const boothService = inject('boothService', () => new BoothService());
    const booths: Ref<IBooth[]> = ref([]);

    const isFetching = ref(false);
    const searchQuery = ref('');

    const clear = () => {};

    const retrieveBookings = async () => {
      isFetching.value = true;
      try {
        const res = await bookingService().retrieve();
        bookings.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveBookings();
    };

    onMounted(async () => {
      await retrieveBookings();
    });

    const sortedBookings = computed(() => {
      const statusOrder = ['CONFIRMED', 'PREBOOKED', 'CANCELED'];
      return bookings.value.sort((a, b) => statusOrder.indexOf(a.status) - statusOrder.indexOf(b.status));
    });

    const filteredBookings = computed(() => {
      if (!searchQuery.value) {
        return sortedBookings.value;
      }
      return sortedBookings.value.filter(booking => {
        const company = companies.value.find(c => c.id === booking.company.id);
        const companyName = company?.name?.toLowerCase();
        return companyName && companyName.includes(searchQuery.value.toLowerCase());
      });
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IBooking) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeBooking = async () => {
      try {
        await bookingService().cancel(removeId.value);
        const message = 'Buchung mit ID ' + removeId.value + ' erfolgreich storniert';
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = 0;
        retrieveBookings();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error);
      }
    };
    const initRelationships = () => {
      companyService()
        .retrieve()
        .then(res => {
          companies.value = res.data;
        });
      boothService()
        .retrieve()
        .then(res => {
          booths.value = res.data;
        });
    };

    initRelationships();

    return {
      bookings,
      handleSyncList,
      isFetching,
      retrieveBookings,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeBooking,
      filteredBookings,
      companies,
      booths,
      searchQuery,
    };
  },
});
