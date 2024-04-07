import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import BoothUserService from './booth-user.service';
import { type IBoothUser } from '@/shared/model/booth-user.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BoothUser',
  setup() {
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const boothUserService = inject('boothUserService', () => new BoothUserService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const boothUsers: Ref<IBoothUser[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveBoothUsers = async () => {
      isFetching.value = true;
      try {
        const res = await boothUserService().retrieve();
        boothUsers.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveBoothUsers();
    };

    onMounted(async () => {
      await retrieveBoothUsers();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IBoothUser) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeBoothUser = async () => {
      try {
        await boothUserService().delete(removeId.value);
        const message = 'A BoothUser is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveBoothUsers();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      boothUsers,
      handleSyncList,
      isFetching,
      retrieveBoothUsers,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeBoothUser,
      ...dataUtils,
    };
  },
});
