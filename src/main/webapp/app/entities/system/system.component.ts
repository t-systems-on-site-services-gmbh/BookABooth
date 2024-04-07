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

    const systems: Ref<ISystem[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveSystems = async () => {
      isFetching.value = true;
      try {
        const res = await systemService().retrieve();
        systems.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveSystems();
    };

    onMounted(async () => {
      await retrieveSystems();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ISystem) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeSystem = async () => {
      try {
        await systemService().delete(removeId.value);
        const message = 'A System is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveSystems();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      systems,
      handleSyncList,
      isFetching,
      retrieveSystems,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeSystem,
    };
  },
});
