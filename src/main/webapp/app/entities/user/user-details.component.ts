import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import UserService from './user.service';
import { type IUser } from '@/shared/model/user.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserDetails',
  setup() {
    const userService = inject('userService', () => new UserService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const user: Ref<IUser> = ref({});

    const retrieveUser = async userLogin => {
      try {
        const res = await userService().find(userLogin);
        user.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userLogin) {
      retrieveUser(route.params.userLogin);
    }

    return {
      alertService,
      user,

      previousState,
    };
  },
});