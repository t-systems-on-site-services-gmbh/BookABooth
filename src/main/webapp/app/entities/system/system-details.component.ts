import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import SystemService from './system.service';
import { type ISystem } from '@/shared/model/system.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SystemDetails',
  setup() {
    const systemService = inject('systemService', () => new SystemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const system: Ref<ISystem> = ref({});

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

    return {
      alertService,
      system,

      previousState,
    };
  },
});
