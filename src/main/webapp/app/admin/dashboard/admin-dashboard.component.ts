import { defineComponent, inject, ref, type Ref } from 'vue';

import type { ILocation } from '@/shared/model/location.model';
import LocationService from '@/entities/location/location.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AdminDashboard',
  setup() {
    const locationService = inject('locationService', () => new LocationService());

    const locations: Ref<ILocation[]> = ref([]);

    const initRelationships = () => {
      locationService()
        .retrieve()
        .then((res: { data: ILocation[] }) => {
          locations.value = res.data;
        });
    };

    initRelationships();

    return {
      locations,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init(): void {
      //this.items.push({ name: 'Greeting', value: 'Moin World' });
    },
  },
});
