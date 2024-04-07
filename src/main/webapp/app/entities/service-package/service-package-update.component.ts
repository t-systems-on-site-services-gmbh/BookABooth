import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ServicePackageService from './service-package.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import BoothService from '@/entities/booth/booth.service';
import { type IBooth } from '@/shared/model/booth.model';
import { type IServicePackage, ServicePackage } from '@/shared/model/service-package.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ServicePackageUpdate',
  setup() {
    const servicePackageService = inject('servicePackageService', () => new ServicePackageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const servicePackage: Ref<IServicePackage> = ref(new ServicePackage());

    const boothService = inject('boothService', () => new BoothService());

    const booths: Ref<IBooth[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveServicePackage = async servicePackageId => {
      try {
        const res = await servicePackageService().find(servicePackageId);
        servicePackage.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.servicePackageId) {
      retrieveServicePackage(route.params.servicePackageId);
    }

    const initRelationships = () => {
      boothService()
        .retrieve()
        .then(res => {
          booths.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      name: {},
      price: {},
      description: {},
      booths: {},
    };
    const v$ = useVuelidate(validationRules, servicePackage as any);
    v$.value.$validate();

    return {
      servicePackageService,
      alertService,
      servicePackage,
      previousState,
      isSaving,
      currentLanguage,
      booths,
      v$,
    };
  },
  created(): void {
    this.servicePackage.booths = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.servicePackage.id) {
        this.servicePackageService()
          .update(this.servicePackage)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A ServicePackage is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.servicePackageService()
          .create(this.servicePackage)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A ServicePackage is created with identifier ' + param.id);
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
