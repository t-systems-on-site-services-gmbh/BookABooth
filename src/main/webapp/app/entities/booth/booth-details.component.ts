import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import BoothService from './booth.service';
import { type IBooth } from '@/shared/model/booth.model';
import { useAlertService } from '@/shared/alert/alert.service';
import LocationService from '@/entities/location/location.service';
import { type ILocation } from '@/shared/model/location.model';
import ServicePackageService from '@/entities/service-package/service-package.service';
import { type IServicePackage } from '@/shared/model/service-package.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BoothDetails',
  setup() {
    const boothService = inject('boothService', () => new BoothService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const locationService = inject('locationService', () => new LocationService());
    const locations: Ref<ILocation[]> = ref([]);

    const servicePackageService = inject('servicePackageService', () => new ServicePackageService());
    const servicePackages: Ref<IServicePackage[]> = ref([]);

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

    const initRelationships = () => {
      locationService()
        .retrieve()
        .then(res => {
          locations.value = res.data;
        });
      servicePackageService()
        .retrieve()
        .then(res => {
          servicePackages.value = res.data;
        });
    };

    initRelationships();

    return {
      alertService,
      booth,
      locations,
      servicePackages,
      previousState,
    };
  },
});
