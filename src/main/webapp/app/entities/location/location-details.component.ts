import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import LocationService from './location.service';
import { type ILocation } from '@/shared/model/location.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'LocationDetails',
  setup() {
    const locationService = inject('locationService', () => new LocationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const siteUrl = inject('siteUrl', () => computed(() => window.location.origin), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const location: Ref<ILocation> = ref({});

    const retrieveLocation = async locationId => {
      try {
        const res = await locationService().find(locationId);
        location.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.locationId) {
      retrieveLocation(route.params.locationId);
    }

    return {
      alertService,
      location,

      previousState,
      siteUrl,
    };
  },
  computed: {
    absoluteImageUrl(): string {
      return this.location.imageUrl ? this.siteUrl + '/' + this.location.imageUrl.replace(/\\/g, '/') : '';
    },
  },
});
