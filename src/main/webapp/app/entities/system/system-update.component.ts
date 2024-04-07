import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import SystemService from './system.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ISystem, System } from '@/shared/model/system.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SystemUpdate',
  setup() {
    const systemService = inject('systemService', () => new SystemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const system: Ref<ISystem> = ref(new System());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveSystem = async systemId => {
      try {
        const res = await systemService().find(systemId);
        system.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.systemId) {
      retrieveSystem(route.params.systemId);
    }

    const validations = useValidation();
    const validationRules = {
      enabled: {},
    };
    const v$ = useVuelidate(validationRules, system as any);
    v$.value.$validate();

    return {
      systemService,
      alertService,
      system,
      previousState,
      isSaving,
      currentLanguage,
      v$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.system.id) {
        this.systemService()
          .update(this.system)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A System is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.systemService()
          .create(this.system)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A System is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
