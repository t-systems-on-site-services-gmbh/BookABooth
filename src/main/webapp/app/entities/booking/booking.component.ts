import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import BookingService from './booking.service';
import { type IBooking } from '@/shared/model/booking.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Booking',
  setup() {
    const dateFormat = useDateFormat();
    const bookingService = inject('bookingService', () => new BookingService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const bookings: Ref<IBooking[]> = ref([]);

    const isFetching = ref(false);

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
        await bookingService().delete(removeId.value);
        const message = 'Buchung mit ID ' + removeId.value + ' erfolgreich storniert';
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = 0;
        retrieveBookings();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error);
      }
    };

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
    };
  },
});
