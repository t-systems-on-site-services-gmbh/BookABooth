import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ServicePackageService from './service-package.service';
import { type IServicePackage } from '@/shared/model/service-package.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ServicePackageDetails',
  setup() {
    const servicePackageService = inject('servicePackageService', () => new ServicePackageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const servicePackage: Ref<IServicePackage> = ref({});

    const retrieveServicePackage = async servicePackageId => {
      try {
        const res = await servicePackageService().find(servicePackageId);
        servicePackage.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.servicePackageId) {
      retrieveServicePackage(route.params.servicePackageId);
    }

    return {
      alertService,
      servicePackage,

      previousState,
    };
  },
});
