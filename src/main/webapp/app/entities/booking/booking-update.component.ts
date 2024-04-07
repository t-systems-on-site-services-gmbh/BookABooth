import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import BookingService from './booking.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CompanyService from '@/entities/company/company.service';
import { type ICompany } from '@/shared/model/company.model';
import BoothService from '@/entities/booth/booth.service';
import { type IBooth } from '@/shared/model/booth.model';
import { type IBooking, Booking } from '@/shared/model/booking.model';
import { BookingStatus } from '@/shared/model/enumerations/booking-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BookingUpdate',
  setup() {
    const bookingService = inject('bookingService', () => new BookingService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const booking: Ref<IBooking> = ref(new Booking());

    const companyService = inject('companyService', () => new CompanyService());

    const companies: Ref<ICompany[]> = ref([]);

    const boothService = inject('boothService', () => new BoothService());

    const booths: Ref<IBooth[]> = ref([]);
    const bookingStatusValues: Ref<string[]> = ref(Object.keys(BookingStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveBooking = async bookingId => {
      try {
        const res = await bookingService().find(bookingId);
        res.received = new Date(res.received);
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

    const validations = useValidation();
    const validationRules = {
      received: {},
      status: {},
      company: {
        required: validations.required('Dieses Feld wird benötigt.'),
      },
      booth: {
        required: validations.required('Dieses Feld wird benötigt.'),
      },
    };
    const v$ = useVuelidate(validationRules, booking as any);
    v$.value.$validate();

    return {
      bookingService,
      alertService,
      booking,
      previousState,
      bookingStatusValues,
      isSaving,
      currentLanguage,
      companies,
      booths,
      v$,
      ...useDateFormat({ entityRef: booking }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.booking.id) {
        this.bookingService()
          .update(this.booking)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Booking is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.bookingService()
          .create(this.booking)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Booking is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
