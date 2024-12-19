import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import BookingService from './booking.service';
import { useDateFormat } from '@/shared/composables';
import { type IBooking } from '@/shared/model/booking.model';
import { useAlertService } from '@/shared/alert/alert.service';
import CompanyService from '@/entities/company/company.service';
import type { ICompany } from '@/shared/model/company.model';
import BoothService from '@/entities/booth/booth.service';
import type { IBooth } from '@/shared/model/booth.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BookingDetails',
  setup() {
    const dateFormat = useDateFormat();
    const bookingService = inject('bookingService', () => new BookingService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const booking: Ref<IBooking> = ref({});

    const companyService = inject('companyService', () => new CompanyService());
    const companies: Ref<ICompany[]> = ref([]);

    const boothService = inject('boothService', () => new BoothService());
    const booths: Ref<IBooth[]> = ref([]);

    const retrieveBooking = async bookingId => {
      try {
        const res = await bookingService().find(bookingId);
        booking.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.bookingId) {
      retrieveBooking(route.params.bookingId);
    }

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
      ...dateFormat,
      alertService,
      booking,
      previousState,
      companies,
      booths,
    };
  },
});
