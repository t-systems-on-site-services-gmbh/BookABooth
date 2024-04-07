import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ServicePackageService from './service-package.service';
import { type IServicePackage } from '@/shared/model/service-package.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ServicePackage',
  setup() {
    const servicePackageService = inject('servicePackageService', () => new ServicePackageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const servicePackages: Ref<IServicePackage[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveServicePackages = async () => {
      isFetching.value = true;
      try {
        const res = await servicePackageService().retrieve();
        servicePackages.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveServicePackages();
    };

    onMounted(async () => {
      await retrieveServicePackages();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IServicePackage) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeServicePackage = async () => {
      try {
        await servicePackageService().delete(removeId.value);
        const message = 'A ServicePackage is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveServicePackages();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      servicePackages,
      handleSyncList,
      isFetching,
      retrieveServicePackages,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeServicePackage,
    };
  },
});
