import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import BoothService from './booth.service';
import { type IBooth } from '@/shared/model/booth.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BoothDetails',
  setup() {
    const boothService = inject('boothService', () => new BoothService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const booth: Ref<IBooth> = ref({});

    const retrieveBooth = async boothId => {
      try {
        const res = await boothService().find(boothId);
        booth.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.boothId) {
      retrieveBooth(route.params.boothId);
    }

    return {
      alertService,
      booth,

      previousState,
    };
  },
});
