import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import SystemService from './system.service';
import { type ISystem } from '@/shared/model/system.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'System',
  setup() {
    const systemService = inject('systemService', () => new SystemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const system: Ref<ISystem> = ref();

    const isFetching = ref(false);

    const retrieveSystem = async () => {
      isFetching.value = true;
      try {
        const res = await systemService().retrieve();
        system.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const toggleSysEnabled = async () => {
      try {
        system.value.enabled = !system.value.enabled;
        const res = await systemService().partialUpdate(system.value);
        system.value = res;
      } catch (err) {
        alertService.showHttpError(err.response);
      }
    };

    onMounted(async () => {
      await retrieveSystem();
    });

    return {
      system,
      isFetching,
      retrieveSystem,
      toggleSysEnabled,
    };
  },
});
