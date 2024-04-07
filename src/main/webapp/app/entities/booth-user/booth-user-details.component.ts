import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import BoothUserService from './booth-user.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IBoothUser } from '@/shared/model/booth-user.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BoothUserDetails',
  setup() {
    const dateFormat = useDateFormat();
    const boothUserService = inject('boothUserService', () => new BoothUserService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const boothUser: Ref<IBoothUser> = ref({});

    const retrieveBoothUser = async boothUserId => {
      try {
        const res = await boothUserService().find(boothUserId);
        boothUser.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.boothUserId) {
      retrieveBoothUser(route.params.boothUserId);
    }

    return {
      ...dateFormat,
      alertService,
      boothUser,

      ...dataUtils,

      previousState,
    };
  },
});
