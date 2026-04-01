
import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import UserService from './user.service';
import { type IUser } from '@/shared/model/user.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'User',
  setup() {
    const userService = inject('userService', () => new UserService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const users: Ref<IUser[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveUsers = async () => {
      isFetching.value = true;
      try {
        const res = await userService().retrieve();
        users.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveUsers();
    };

    onMounted(async () => {
      await retrieveUsers();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IUser) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeUser = async () => {
      try {
        await userService().delete(removeId.value);
        const message = 'A User is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveUsers();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      users,
      handleSyncList,
      isFetching,
      retrieveUsers,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeUser,
    };
  },
});
