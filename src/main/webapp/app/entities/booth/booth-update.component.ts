import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import BoothService from './booth.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import LocationService from '@/entities/location/location.service';
import { type ILocation } from '@/shared/model/location.model';
import ServicePackageService from '@/entities/service-package/service-package.service';
import { type IServicePackage } from '@/shared/model/service-package.model';
import { type IBooth, Booth } from '@/shared/model/booth.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BoothUpdate',
  setup() {
    const boothService = inject('boothService', () => new BoothService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const booth: Ref<IBooth> = ref(new Booth());

    const locationService = inject('locationService', () => new LocationService());

    const locations: Ref<ILocation[]> = ref([]);

    const servicePackageService = inject('servicePackageService', () => new ServicePackageService());

    const servicePackages: Ref<IServicePackage[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveBooth = async boothId => {
      try {
        const res = await boothService().find(boothId);
        booth.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.boothId) {
      retrieveBooth(route.params.boothId);
    }

    const initRelationships = () => {
      locationService()
        .retrieve()
        .then(res => {
          locations.value = res.data;
        });
      servicePackageService()
        .retrieve()
        .then(res => {
          servicePackages.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required('Dieses Feld wird benötigt.'),
        maxLength: validations.maxLength('Dieses Feld darf max. 200 Zeichen lang sein.', 200),
      },
      ceilingHeight: {},
      available: {
        required: validations.required('Dieses Feld wird benötigt.'),
      },
      location: {},
      servicePackages: {},
    };
    const v$ = useVuelidate(validationRules, booth as any);
    v$.value.$validate();

    return {
      boothService,
      alertService,
      booth,
      previousState,
      isSaving,
      currentLanguage,
      locations,
      servicePackages,
      v$,
    };
  },
  created(): void {
    this.booth.servicePackages = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.booth.id) {
        this.boothService()
          .update(this.booth)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Booth is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.boothService()
          .create(this.booth)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Booth is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
